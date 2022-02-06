package com.example.smartcontract.services;


import com.example.smartcontract.dtos.ChangeOwnershipInput;
import com.example.smartcontract.dtos.ImmobilierInput;
import com.example.smartcontract.dtos.StatusImmobilier;
import com.example.smartcontract.entities.Immobillier;

import java.util.List;

public interface ImmobillierContractService {
   public List<Immobillier> allImmobiliers();
   public List<Immobillier> myImmobiliers();
   public List<Immobillier> approvedImmobiliers();
   public List<Immobillier> waitingImmobiliers();
   public List<Immobillier> waitingChangeImmobiliers();
   public List<Immobillier> forSellImmobiliers();
   public Immobillier forSell(String _propId) throws Exception;
   public String addNewUser(String _newUser) throws Exception;
   public  Immobillier approveChangeOwnership(String _propId) throws Exception;
   public StatusImmobilier approveProperty(String _propId) throws Exception;
   public  String approveUsers(String _newUser) throws Exception;
   public  String  changeOwnership(ChangeOwnershipInput changeOwnershipInput) throws Exception;
   public Immobillier createProperty(ImmobilierInput immobilierInput ) throws Exception;
   public void creatorAdmin() throws Exception;
   public Immobillier getPropertyDetails(String _propId) throws Exception;
   public void propOwnerChange(String param0) throws Exception;
}
