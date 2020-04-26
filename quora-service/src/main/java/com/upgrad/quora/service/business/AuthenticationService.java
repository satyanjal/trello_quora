package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


@Service
public class AuthenticationService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthDao userAuthDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;


    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity signin(final String username, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userDao.getUserByUsername(username);
        if (userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        final String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());
        if (encryptedPassword.equals(userEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthEntity userAuthTokenEntity = new UserAuthEntity();
            userAuthTokenEntity.setUser(userEntity);
            userAuthTokenEntity.setUuid(UUID.randomUUID().toString());
            final Date now = new Date();
            final Date expiresAt = this.addHoursToJavaUtilDate(now, 8);

            userAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
            userAuthTokenEntity.setLoginAt(now);
            userAuthTokenEntity.setExpiresAt(expiresAt);

            userAuthDao.createAuthToken(userAuthTokenEntity);

            return userAuthTokenEntity;
        }
        else {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }
    }

    private Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signout(final String accesstoken) throws SignOutRestrictedException{
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accesstoken);

        if(userAuthEntity == null){
            throw new SignOutRestrictedException("SGR-001", "User is not signed in");
        }
        userAuthEntity.setLogoutAt(Date.from(Instant.now()));
        userAuthDao.updateUserAuth(userAuthEntity);
        return userAuthEntity.getUser();
    }
}
