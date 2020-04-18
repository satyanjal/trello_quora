package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Date;


@Service
public class AuthenticationService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider CryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity authenticate (final String user, final String email, final String password) throws SignUpRestrictedException {

        UserEntity userentity = userDao.getUserByEmail(email);

        if (user.equals(userentity.getUserName())){
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }

        if(email.equals(userentity.getEmail())){
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }

        final String encryptedPassword = CryptographyProvider.encrypt(password, userentity.getSalt());
        if (encryptedPassword.equals(userentity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthEntity userAuthTokenEntity = new UserAuthEntity();
            userAuthTokenEntity.setUser(userentity);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expires = now.plusHours(8);
            final Date issuedAt = new Date(now.getLong(ChronoField.INSTANT_SECONDS));
            final Date expiresAt = new Date(expires.getLong(ChronoField.INSTANT_SECONDS));

            userAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(userentity.getUuid(), now, expires));

            userAuthTokenEntity.setLoginAt(issuedAt);
            userAuthTokenEntity.setExpiresAt(expiresAt);

            userDao.createAuthToken(userAuthTokenEntity);

            userDao.updateUser(userentity);
            userAuthTokenEntity.setLoginAt(issuedAt);
            return userAuthTokenEntity;
        }
        else {
            throw new SignUpRestrictedException("ATH-002", "Password failed");
        }
    }
}
