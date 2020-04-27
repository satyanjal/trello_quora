package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CommonService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthDao userAuthDao;

    /**
     * This method fetches user details from the system.
     *
     * @param userId  The userId of the User to be fetched from database.
     * @param accessToken The JWT access token of the user passed in the request header.
     * @return UserEntity The user object with all the details
     * @throws AuthorizationFailedException This exception is thrown, if the user is not signed in or it has signed out
     * @throws UserNotFoundException This exception is thrown if the user is not present in the database for the requested userUuid
     */
    public UserEntity userProfile(String accessToken, String userId) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthEntity userAuthEntity= userAuthDao.getUserAuthByToken((accessToken));
        if(userAuthEntity==null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        if(userAuthEntity.getLogoutAt()!=null || userAuthEntity.getExpiresAt().before(new Date())) {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
        }

        UserEntity userEntity=userDao.getUserById(userId);
        if(userEntity==null) {
            throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
        }
        return userEntity;
    }
}
