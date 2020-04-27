
package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.upgrad.quora.service.business.CommonService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private CommonService commonService;

    /**
     * This method fetches user details from the system.
     *
     * @param userId  The userId of the User to be fetched from database.
     * @param accessToken The JWT access token of the user passed in the request header.
     * @return ResponseEntity
     * @throws AuthorizationFailedException This exception is thrown, if the user is not signed in or it has signed out
     * @throws UserNotFoundException This exception is thrown if the user is not present in the database for the requested userUuid
     */
    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> userProfile(@RequestHeader("authorization") final String accessToken, @PathVariable("userId") final String userId) throws AuthorizationFailedException, UserNotFoundException {

        UserEntity userEntity= commonService.userProfile(accessToken, userId);
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().userName(userEntity.getUserName())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .aboutMe(userEntity.getAboutMe())
                .contactNumber(userEntity.getContactNumber())
                .country(userEntity.getCountry())
                .dob(userEntity.getDob())
                .emailAddress(userEntity.getEmail());

        return new ResponseEntity<>(userDetailsResponse, HttpStatus.OK);
    }
}
