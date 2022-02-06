package com.example.payment_service.services;

import com.example.payment_service.PaymentServiceApplication;
import com.example.payment_service.Repositories.PaymentRepository;
import com.example.payment_service.Repositories.SellInfoRepository;
import com.example.payment_service.constant.LoadedContarct;
import com.example.payment_service.contracts.PaymentContract;
//import com.example.payment_service.entities.Payment;
import com.example.payment_service.dtos.*;
import com.example.payment_service.entities.ForSellInfo;
import com.example.payment_service.entities.Payment;
import com.example.payment_service.restClient.ImmobilierRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.Date;

@Service
@Transactional
public class PaymentContractServiceImpl implements PaymentContractService {


    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    SellInfoRepository sellInfoRepository;
    @Autowired
    ImmobilierRestClient immobilierRestClient;

    private final static String PRIVATE_KEY = "16aea201d9e8f46d1b54e4192ecfbfbeb03b2b1c806141a7126df224b72ab85e";
    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(6721975L);
    private final static BigInteger GAS_PRICE = BigInteger.valueOf(20000000000L);
    private final static String CONTRACT_ADDRESS = "0x02b7e8542a5d4f48de93323745640aa26c2ab525";
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceApplication.class);


    @Override
    public BigInteger invest(PaymentInput paymentInput) throws Exception {
        Payment payment = new Payment(
                null,
                paymentInput.getImmoId(),
                paymentInput.getValue(),
                CONTRACT_ADDRESS,
                paymentInput.getFrom(),
                new Date(),
                StatusPayment.WAITING
                );
        paymentRepository.save(payment);
        BigInteger value = Convert.toWei(String.valueOf(paymentInput.getValue()), Convert.Unit.ETHER).toBigInteger();
        PaymentContract paymentContract = loadContract(paymentInput.getPrivateKey());
        paymentContract.invest(value).send();
        BigInteger a= paymentContract.balaceOf().send();
        return a;
    }
    @Override
    public BigInteger taxe(TaxeInput taxeInput) throws Exception {
        BigInteger value = Convert.toWei(String.valueOf(taxeInput.getValue()), Convert.Unit.ETHER).toBigInteger();
        PaymentContract paymentContract = loadContract(taxeInput.getPrivateKey());
        paymentContract.invest(value).send();
        BigInteger a= paymentContract.balaceOf().send();
        return a;
    }
    @Override
    public ForSellInfo forSell(ForSellInput forSellInput) throws Exception {
        ForSellInfo forSellInfo = new ForSellInfo(null,forSellInput.getImmoId(),forSellInput.getPrivateKey());
        sellInfoRepository.save(forSellInfo);
        immobilierRestClient.forSell(forSellInput.getImmoId());
        return sellInfoRepository.save(forSellInfo);
    }

    @Override
    public String buy(BuyInput buyInput) throws Exception {
        this.invest(buyInput.getPaymentInput());
        ForSellInfo forSellInfo=  sellInfoRepository.findForSellInfoByImmoid(buyInput.getPaymentInput().getImmoId());
        ChangeOwnershipInput changeOwnershipInput = new ChangeOwnershipInput(
                forSellInfo.getPrivateKey(),
                buyInput.getPaymentInput().getImmoId(),
                buyInput.get_newOwner()
                );
        immobilierRestClient.changeOwnerShip(changeOwnershipInput);
        return "Waiting .....";
    }
    @Override
    public Immobillier accepteChange(ApproveImmoInput approveImmoInput) throws Exception {
        Immobillier immobillier1= immobilierRestClient.immobillierDetails(approveImmoInput.getPropId());
        Payment payment = paymentRepository.findPaymentByImmoid(approveImmoInput.getPropId());
        this.acceptePayment(immobillier1.getOwnerAddress(),payment.getPrice());
        Immobillier immobillier= immobilierRestClient.approveChangeOwnerShip(approveImmoInput);
        return immobillier;
    }
    @Override
    public String  refuseChange(ApproveImmoInput approveImmoInput) throws Exception {
        Payment payment = paymentRepository.findPaymentByImmoid(approveImmoInput.getPropId());
        this.refusePayment(payment.getFrom(),payment.getPrice());
        return "refuse it ......";
    }

    @Override
    public BigInteger balanceOf() throws Exception {
        PaymentContract paymentContract = loadContract(PRIVATE_KEY);
        BigInteger a= paymentContract.balaceOf().send();
        BigInteger value = Convert.fromWei(String.valueOf(a), Convert.Unit.ETHER).toBigInteger();
        return value;
    }

    @Override
    public BigInteger acceptePayment(String to, int ether) throws Exception {
        BigInteger value = Convert.toWei(String.valueOf(ether), Convert.Unit.ETHER).toBigInteger();
        PaymentContract paymentContract = loadContract(PRIVATE_KEY);
        paymentContract.paymentAccepted(to,value).send();
        BigInteger a= paymentContract.balaceOf().send();
        return a;
    }


    @Override
    public BigInteger refusePayment(String to, int ether) throws Exception {
        BigInteger value = Convert.toWei(String.valueOf(ether), Convert.Unit.ETHER).toBigInteger();
        PaymentContract paymentContract = loadContract(PRIVATE_KEY);
        paymentContract.paymentDenied(to,value).send();
        BigInteger a= paymentContract.balaceOf().send();
        return a;
    }

    private PaymentContract loadContract(String privateKey) throws Exception {
        String node = "HTTP://0.0.0.0:7545";
        System.out.println("Connecting to Ethereum …");
        Web3j web3 = Web3j.build(new HttpService(node));
        System.out.println("Ethereum connected ");
        BigInteger privkey = new BigInteger(privateKey, 16);
        ECKeyPair ecKeyPair = ECKeyPair.create(privkey);
        Credentials credentials = Credentials.create(ecKeyPair);
        PaymentContract contract = PaymentContract.load(CONTRACT_ADDRESS, web3, credentials, GAS_PRICE, GAS_LIMIT);
        LoadedContarct.setLoadedContarct(contract);
        String  address_ = LoadedContarct.getLoadedContarct().getContractAddress();
        log.info("Smart contract deployed to address "+address_ );
        log.info("Creator  address "+ contract.creatorAdmin().send() );
        return contract;
    }
}

