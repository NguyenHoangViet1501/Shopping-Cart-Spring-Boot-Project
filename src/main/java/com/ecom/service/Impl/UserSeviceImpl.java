package com.ecom.service.Impl;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;
import com.ecom.util.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserSeviceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void updateUserResetToken(String email, String resetToken) {
        UserDtls findbyEmail = userRepository.findByEmail(email);
        findbyEmail.setResetToken(resetToken);
        userRepository.save(findbyEmail);
    }

    @Override
    public UserDtls saveUser(UserDtls userDtls) {
        userDtls.setRole("ROLE_USER");
        userDtls.setIsEnable(true);
        userDtls.setAccountNonLocked(true);
        userDtls.setFailedAttempt(0);
        userDtls.setLockTime(null);
        String encodePassword = passwordEncoder.encode(userDtls.getPassword());
        userDtls.setPassword(encodePassword);
        UserDtls saveUser = userRepository.save(userDtls);
        return saveUser; // Replace with actual implementation
    }

    @Override
    public UserDtls getUserByEmail(String email) {

        UserDtls userDtls = userRepository.findByEmail(email);
        return userDtls;
    }

    @Override
    public List<UserDtls> getUsers(String role) {
        return userRepository.findByRole(role);
    }

    @Override
    public Boolean updateUserAccountStatus(Integer id, Boolean status) {
        Optional<UserDtls> findByuser = userRepository.findById(id);
        if(findByuser.isPresent()){
            UserDtls userDtls = findByuser.get();
            userDtls.setIsEnable(status);
            userRepository.save(userDtls);
            return true;
        }
        return false;
    }

    @Override
    public void increaseFailedAttempt(UserDtls user) {
        int attempt = user.getFailedAttempt() + 1;
        user.setFailedAttempt(attempt);
        userRepository.save(user);
    }

    @Override
    public void userAccountLock(UserDtls user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userRepository.save(user);
    }

    @Override
    public boolean unlockAccountTimeExpired(UserDtls user) {
        long lockTime = user.getLockTime().getTime();
        long unlockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;

        long currentTime = System.currentTimeMillis();

        if(unlockTime < currentTime){
            user.setAccountNonLocked(true);
            user.setFailedAttempt(0);
            user.setLockTime(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void resetAttempt(int userId) {

    }

    @Override
    public UserDtls getUserByToken(String token) {
        return userRepository.findByResetToken(token);

    }

    @Override
    public UserDtls updateUser(UserDtls userDtls) {
        return userRepository.save(userDtls);
    }

    @Override
    public UserDtls updateUserProfile(UserDtls user, MultipartFile img)  {

        UserDtls dbUser = userRepository.findById(user.getId()).get();

        if(!img.isEmpty()){
            dbUser.setProfileImage(img.getOriginalFilename());
        }

        if(!ObjectUtils.isEmpty(dbUser)){
           dbUser.setName(user.getName());
           dbUser.setMobileNumber(user.getMobileNumber());
           dbUser.setAddress(user.getAddress());
           dbUser.setCity(user.getCity());
           dbUser.setState(user.getState());
           dbUser.setPincode(user.getPincode());
           dbUser = userRepository.save(dbUser);
        }
        try {
            if (!img.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();
                File profileImgDir = new File(saveFile.getAbsolutePath() + File.separator + "profile_img");
                if (!profileImgDir.exists()) {
                    profileImgDir.mkdirs();
                }
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
                        + img.getOriginalFilename());

                Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return dbUser;
    }

    @Override
    public UserDtls saveAdmin(UserDtls userDtls) {
        userDtls.setRole("ROLE_ADMIN");
        userDtls.setIsEnable(true);
        userDtls.setAccountNonLocked(true);
        userDtls.setFailedAttempt(0);
        userDtls.setLockTime(null);
        String encodePassword = passwordEncoder.encode(userDtls.getPassword());
        userDtls.setPassword(encodePassword);
        UserDtls saveUser = userRepository.save(userDtls);
        return saveUser; // Replace with actual implementation
    }

    @Override
    public Boolean existEmail(String email) {
        return userRepository.existsByEmail(email);
    }


}
