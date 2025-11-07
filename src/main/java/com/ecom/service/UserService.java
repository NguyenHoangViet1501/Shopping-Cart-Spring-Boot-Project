package com.ecom.service;

import com.ecom.model.UserDtls;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    public void updateUserResetToken(String email, String resetToken);

    public UserDtls saveUser(UserDtls userDtls);

    public UserDtls getUserByEmail(String email);

    public List<UserDtls> getUsers(String role);

    public Boolean updateUserAccountStatus(Integer id, Boolean status);

    public void increaseFailedAttempt(UserDtls user);

    public void userAccountLock(UserDtls user);

    public boolean unlockAccountTimeExpired(UserDtls user);

    public void resetAttempt(int userId);

    public UserDtls getUserByToken(String token);

    public UserDtls updateUser(UserDtls userDtls);

    public UserDtls updateUserProfile(UserDtls user, MultipartFile img);

    public UserDtls saveAdmin(UserDtls userDtls);

    public Boolean existEmail(String email);
}
