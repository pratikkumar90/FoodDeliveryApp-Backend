package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Controller
@CrossOrigin
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @RequestMapping(method = RequestMethod.POST, path = "/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> customerSignUp(@RequestBody final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {
        CustomerEntity customerEntity = createCustomerEntity(signupCustomerRequest);
        validateCustomerFields(signupCustomerRequest);
        CustomerEntity processedCustomerEntity = customerService.saveCustomer(customerEntity);

        SignupCustomerResponse signupCustomerResponse = new SignupCustomerResponse()
                .id(processedCustomerEntity.getUuid())
                .status("CUSTOMER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupCustomerResponse>(signupCustomerResponse, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> login(@RequestHeader final String authorization) throws AuthenticationFailedException {
        byte[] authData = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedAuthData = new String(authData);

        String[] decodedAuthDataArray = decodedAuthData.split(":");
        if (decodedAuthDataArray.length != 2) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }
        String username = decodedAuthData.split(":")[0];
        String password = decodedAuthData.split(":")[1];

        CustomerAuthEntity customerAuthEntity = customerService.authenticate(username, password);

        LoginResponse loginResponse = new LoginResponse().id(customerAuthEntity.getCustomer().getUuid())
                .message("LOGGED IN SUCCESSFULLY").firstName(customerAuthEntity.getCustomer()
                        .getFirstName()).lastName(customerAuthEntity.getCustomer().getLastName())
                .emailAddress(customerAuthEntity.getCustomer().getEmail())
                .contactNumber(customerAuthEntity.getCustomer().getContactNumber());

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", "Bearer " + customerAuthEntity.getAccessToken());

        List<String> header = new ArrayList<>();
        header.add("access-token");
        headers.setAccessControlExposeHeaders(header);

        return new ResponseEntity<>(loginResponse, headers, HttpStatus.OK);
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public ResponseEntity<LogoutResponse> logoutCustomer(@RequestHeader final String authorization) throws AuthorizationFailedException {
        String[] tokens = authorization.split("Bearer ");
        if (tokens.length != 2) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        } else {
            CustomerAuthEntity customerAuth = customerService.logout(tokens[1]);

            LogoutResponse response = new LogoutResponse().id(customerAuth.getCustomer().getUuid())
                    .message("LOGGED OUT SUCCESSFULLY");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> updateCustomer(@RequestHeader final String authorization
            , @RequestBody final UpdateCustomerRequest updateCustomerRequest) throws AuthorizationFailedException, UpdateCustomerException {
        if (StringUtils.isEmpty(updateCustomerRequest.getFirstName())) {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }

        String[] tokens = authorization.split("Bearer ");
        if (tokens.length != 2) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        } else {
            CustomerEntity customerEntity = customerService.getCustomer(tokens[1]);
            createCustomerEntity(customerEntity, updateCustomerRequest);

            customerService.updateCustomer(customerEntity);

            UpdateCustomerResponse response = new UpdateCustomerResponse().id(customerEntity.getUuid())
                    .status("CUSTOMER DETAILS UPDATED SUCCESSFULLY")
                    .firstName(customerEntity.getFirstName())
                    .lastName(customerEntity.getLastName());
            return new ResponseEntity<UpdateCustomerResponse>(response, HttpStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/password", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordResponse> updatePassword(@RequestHeader final String authorization
            , @RequestBody final UpdatePasswordRequest updatePasswordRequest) throws AuthorizationFailedException, UpdateCustomerException {
        if (StringUtils.isEmpty(updatePasswordRequest.getOldPassword()) || StringUtils.isEmpty(updatePasswordRequest.getNewPassword())) {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }

        String[] tokens = authorization.split("Bearer ");
        if (tokens.length != 2) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        } else {
            CustomerEntity customerEntity = customerService.getCustomer(tokens[1]);

            customerEntity = customerService.updateCustomerPassword(updatePasswordRequest.getOldPassword()
                    , updatePasswordRequest.getNewPassword(), customerEntity);

            UpdatePasswordResponse response = new UpdatePasswordResponse().id(customerEntity.getUuid())
                    .status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");
            return new ResponseEntity<UpdatePasswordResponse>(response, HttpStatus.OK);
        }
    }


    private CustomerEntity createCustomerEntity(SignupCustomerRequest signupCustomerRequest) {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setPassword(signupCustomerRequest.getPassword());
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setUuid(UUID.randomUUID().toString());
        return customerEntity;
    }

    private CustomerEntity createCustomerEntity(CustomerEntity customerEntity, UpdateCustomerRequest updateCustomerRequest) {
        customerEntity.setFirstName(updateCustomerRequest.getFirstName());
        customerEntity.setLastName(updateCustomerRequest.getLastName());
        return customerEntity;
    }

    private void validateCustomerFields(SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {
        if (StringUtils.isEmpty(signupCustomerRequest.getContactNumber()) ||
                StringUtils.isEmpty(signupCustomerRequest.getEmailAddress()) ||
                StringUtils.isEmpty(signupCustomerRequest.getFirstName()) ||
                StringUtils.isEmpty(signupCustomerRequest.getPassword())) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }
    }

}
