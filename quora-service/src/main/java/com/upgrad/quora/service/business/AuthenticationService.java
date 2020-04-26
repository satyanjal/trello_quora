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
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Date;


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
//        System.out.println(userEntity.getUserName());
//        System.out.println(username);
        if (userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        final String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());
        if (encryptedPassword.equals(userEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthEntity userAuthTokenEntity = new UserAuthEntity();
            userAuthTokenEntity.setUser(userEntity);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expires = now.plusHours(8);
            final Date issuedAt = new Date(now.getLong(ChronoField.INSTANT_SECONDS));
            final Date expiresAt = new Date(expires.getLong(ChronoField.INSTANT_SECONDS));

            userAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expires));

            userAuthTokenEntity.setLoginAt(issuedAt);
            userAuthTokenEntity.setExpiresAt(expiresAt);

            userAuthDao.createAuthToken(userAuthTokenEntity);

            userDao.updateUser(userEntity);
            //userAuthTokenEntity.setLoginAt(issuedAt);
            return userAuthTokenEntity;
        }
        else {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }
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
