package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class AddressController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AddressService addressService;

    @RequestMapping(path = "/address", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestHeader final String authorization
            , @RequestBody SaveAddressRequest saveAddressRequest) throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException {

        String[] tokens = authorization.split("Bearer ");
        if (tokens.length != 2) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        } else {
            CustomerEntity customerEntity = customerService.getCustomer(tokens[1]);
            AddressEntity addressEntity = createAddressEntity(saveAddressRequest);
            AddressEntity processedAddressEntity = addressService.saveAddress(addressEntity, customerEntity);
            SaveAddressResponse response = new SaveAddressResponse().id(processedAddressEntity.getUuid()).status("ADDRESS SUCCESSFULLY REGISTERED");
            return new ResponseEntity<SaveAddressResponse>(response, HttpStatus.CREATED);
        }
    }

    @RequestMapping(path = "/address/customer", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getAllAddressesForCustomer(@RequestHeader final String authorization) throws AuthorizationFailedException {

        String[] tokens = authorization.split("Bearer ");
        if (tokens.length != 2) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        } else {
            CustomerEntity customerEntity = customerService.getCustomer(tokens[1]);
            List<AddressEntity> addresses = addressService.getAllAddress(customerEntity);
            AddressListResponse response = new AddressListResponse();

            List<AddressList> addressList = new ArrayList<>();
            addresses.forEach(i -> {
                AddressList address = new AddressList();
                address.setCity(i.getCity());
                address.setFlatBuildingName(i.getFlatBuilNo());
                address.setId(UUID.fromString(i.getUuid()));
                address.setLocality(i.getLocality());
                address.setPincode(i.getPincode());

                AddressListState addressState = new AddressListState();
                addressState.setId(UUID.fromString(i.getState().getUuid()));
                addressState.setStateName(i.getState().getStateName());

                address.setState(addressState);
                addressList.add(address);
            });

            response.setAddresses(addressList);

            return new ResponseEntity<AddressListResponse>(response, HttpStatus.OK);
        }
    }

    private AddressEntity createAddressEntity(SaveAddressRequest saveAddressRequest) throws AddressNotFoundException {
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setActive(1);
        addressEntity.setCity(saveAddressRequest.getCity());
        addressEntity.setFlatBuilNo(saveAddressRequest.getFlatBuildingName());
        addressEntity.setLocality(saveAddressRequest.getLocality());
        addressEntity.setPincode(saveAddressRequest.getPincode());
        addressEntity.setState(addressService.getStateByUUID(saveAddressRequest.getStateUuid()));
        addressEntity.setUuid(UUID.randomUUID().toString());
        return addressEntity;
    }

    @RequestMapping(path = "/address/{address_id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DeleteAddressResponse> deleteAddress(@RequestHeader final String authorization
            , @PathVariable(name = "address_id") final String addressId) throws AuthorizationFailedException, AddressNotFoundException {
        if (StringUtils.isEmpty(addressId)) {
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
        }
        String[] tokens = authorization.split("Bearer ");
        if (tokens.length != 2) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        } else {
            CustomerEntity customerEntity = customerService.getCustomer(tokens[1]);
            AddressEntity addressEntity = addressService.getAddressByUUID(addressId, customerEntity);

            AddressEntity deletedAddressEntity = addressService.deleteAddress(addressEntity);

            DeleteAddressResponse response = new DeleteAddressResponse().id(UUID.fromString(deletedAddressEntity.getUuid())).status("ADDRESS DELETED SUCCESSFULLY");
            return new ResponseEntity<DeleteAddressResponse>(response, HttpStatus.OK);
        }
    }


    @RequestMapping(path = "/states", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatesListResponse> getStates() {
        List<StateEntity> states = addressService.getAllStates();
        StatesListResponse response = new StatesListResponse();
        if (CollectionUtils.isEmpty(states)) {
            response.setStates(null);
        } else {
            List<StatesList> statesList = new ArrayList<>();
            states.forEach(i -> {
                StatesList state = new StatesList();

                state.setId(UUID.fromString(i.getUuid()));
                state.setStateName(i.getStateName());

                statesList.add(state);
            });
            response.setStates(statesList);
        }
        return new ResponseEntity<StatesListResponse>(response, HttpStatus.OK);
    }

}
