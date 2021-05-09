package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerAuthDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class CustomerService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CustomerAuthDao customerAuthDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException {
        validateExistingCustomer(customerEntity);
        validateEmailAddressFormat(customerEntity);
        validatePhoneNumberFormat(customerEntity);
        validatePasswordFormat(customerEntity);

        String[] encryptedPassword = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedPassword[0]);
        customerEntity.setPassword(encryptedPassword[1]);

        customerDao.createCustomer(customerEntity);

        return customerEntity;
    }

    public CustomerEntity updateCustomer(CustomerEntity customerEntity) throws UpdateCustomerException {
        return customerDao.updateCustomer(customerEntity);
    }

    public CustomerAuthEntity authenticate(final String username, final String password) throws AuthenticationFailedException {
        CustomerEntity customer = customerDao.getCustomerByContactNumber(username);
        if (customer == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }
        String encryptedPassword = PasswordCryptographyProvider.encrypt(password, customer.getSalt());
        if (!customer.getPassword().equals(encryptedPassword)) {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        } else {
            ZonedDateTime expirationTime = ZonedDateTime.now().plusHours(4);
            ZonedDateTime issuedTime = ZonedDateTime.now();
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(password);

            String accessToken = jwtTokenProvider.generateToken(customer.getUuid(), issuedTime, expirationTime);

            CustomerAuthEntity customerAuth = new CustomerAuthEntity();
            customerAuth.setAccessToken(accessToken);
            customerAuth.setExpiresAt(expirationTime);
            customerAuth.setLoginAt(issuedTime);
            customerAuth.setCustomer(customer);
            customerAuth.setUuid(UUID.randomUUID().toString());

            return customerAuthDao.createCustomerAuthEntity(customerAuth);
        }
    }

    private void validatePasswordFormat(CustomerEntity customerEntity) throws SignUpRestrictedException {
        if (!customerEntity.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$")) {
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        }
    }

    private void validatePhoneNumberFormat(CustomerEntity customerEntity) throws SignUpRestrictedException {
        if (!customerEntity.getContactNumber().matches("^\\d{10}$")) {
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        }
    }

    private void validateEmailAddressFormat(CustomerEntity customerEntity) throws SignUpRestrictedException {
        if (!customerEntity.getEmail().matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")) {
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }
    }

    private void validateExistingCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException {
        CustomerEntity existingCustomerEntity = customerDao.getCustomerByContactNumber(customerEntity.getContactNumber());
        if (existingCustomerEntity != null) {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
        }
    }

    public CustomerAuthEntity logout(String authorization) throws AuthorizationFailedException {

        String token = authorization.split("Bearer ")[1];
        CustomerAuthEntity customerAuth = customerAuthDao.getCustomerAuthByAccessToken(token);
        validateCustomerAuth(customerAuth);
        customerAuth.setLogoutAt(ZonedDateTime.now());
        return customerAuthDao.updateCustomerAuthEntity(customerAuth);
    }

    private void validateCustomerAuth(CustomerAuthEntity customerAuth) throws AuthorizationFailedException {
        if (customerAuth == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if (customerAuth.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        if (customerAuth.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }
    }


    public CustomerEntity getCustomer(String authorization) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuth = customerAuthDao.getCustomerAuthByAccessToken(authorization);
        validateCustomerAuth(customerAuth);
        return customerAuth.getCustomer();
    }

    public CustomerEntity updateCustomerPassword(String oldPassword, String newPassword, CustomerEntity customerEntity) throws UpdateCustomerException {
        if (!newPassword.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$")) {
            throw new UpdateCustomerException("UCR-001", "Weak password!");
        }

        String encryptedPassword = PasswordCryptographyProvider.encrypt(oldPassword, customerEntity.getSalt());
        if (!customerEntity.getPassword().equals(encryptedPassword)) {
            throw new UpdateCustomerException("UCR-004", "Incorrect old password!");
        }

        String[] newEncryptedPassword = passwordCryptographyProvider.encrypt(newPassword);
        customerEntity.setSalt(newEncryptedPassword[0]);
        customerEntity.setPassword(newEncryptedPassword[1]);

        return customerDao.updateCustomer(customerEntity);
    }
}
