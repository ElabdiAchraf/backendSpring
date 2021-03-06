package com.example.jwt.services;

import com.example.jwt.dtos.*;
import com.example.jwt.entities.AppRole;
import com.example.jwt.entities.AppUser;


public interface AccountService {
    public AppUser addUser(String username , String password , String comfirmPassword,String address);
    public UserActive getUserActive(String username , String password);
    public AppRole saveRole(AppRole appRole);
    public AppUser loadUserbyUsername(String username);
    public void addRoleToUser(String username,String role);
    public AccountResponse activeAccount(ActiveAccount activeAccount);
    public AccountResponse resetPassword(ResetPassword resetPassword);
    public UserResp getUser(String username);

    UserResp getUserByAddress(String address);
}
