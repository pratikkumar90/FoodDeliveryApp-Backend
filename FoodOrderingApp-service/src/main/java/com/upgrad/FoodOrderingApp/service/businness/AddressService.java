package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private StateDao stateDao;

    public StateEntity getStateByUUID(String stateUuid) throws AddressNotFoundException {
        return stateDao.getStateByUUid(stateUuid);
    }

    public AddressEntity saveAddress(AddressEntity addressEntity, CustomerEntity customerEntity) throws AddressNotFoundException, SaveAddressException {
        if (StringUtils.isEmpty(addressEntity.getCity())
                || StringUtils.isEmpty(addressEntity.getFlatBuilNo())
                || StringUtils.isEmpty(addressEntity.getLocality())
                || StringUtils.isEmpty(addressEntity.getPincode())) {
            throw new SaveAddressException("SAR-001", "No field can be empty");
        }

        if (this.getStateByUUID(addressEntity.getState().getUuid()) == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        }

        if (!addressEntity.getPincode().matches("^\\d{6}$")) {
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        }

        addressDao.createAddress(addressEntity);

        CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
        customerAddressEntity.setAddress(addressEntity);
        customerAddressEntity.setCustomer(customerEntity);
        addressDao.createCustomerAddress(customerAddressEntity);

        return addressEntity;
    }

    public List<AddressEntity> getAllAddress(CustomerEntity customerEntity) {
        List<AddressEntity> addresses = new ArrayList<>();
        List<CustomerAddressEntity> customerAddressEntities = addressDao.getAllCustomerAddresses(customerEntity);
        System.out.println("customerAddressEntities=" + customerAddressEntities.size());
        customerAddressEntities.forEach(i -> addresses.add(i.getAddress()));

        return addresses;
    }

    public AddressEntity getAddressByUUID(String addressUuid, CustomerEntity customerEntity) throws AddressNotFoundException, AuthorizationFailedException {
        List<CustomerAddressEntity> customerAddressEntities = addressDao.getAllCustomerAddresses(customerEntity);
        for (CustomerAddressEntity i : customerAddressEntities) {
            if (i.getAddress().getUuid().equals(addressUuid)) {
                return i.getAddress();
            } else {
                throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
            }
        }
        throw new AddressNotFoundException("ANF-003", "No address by this id");
    }

    public AddressEntity deleteAddress(AddressEntity addressEntity) {
        addressDao.removeAddress(addressEntity);
        return addressEntity;
    }

    public List<StateEntity> getAllStates() {
        return stateDao.getAllStates();
    }
}
