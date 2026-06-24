package com.fooddelivery.service;

import com.fooddelivery.dto.AddressRequest;
import com.fooddelivery.entity.Address;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.repository.AddressRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public List<Address> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    public Address getAddressById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));
    }

    @Transactional
    public Address createAddress(Long userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getIsDefault() != null && request.getIsDefault()) {
            clearDefaultAddresses(userId);
        }

        Address address = new Address();
        address.setUser(user);
        address.setAddressLine(request.getAddressLine());
        address.setCity(request.getCity());
        address.setPincode(request.getPincode());
        address.setAddressType(request.getAddressType() != null ? request.getAddressType() : "Home");
        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : false);

        return addressRepository.save(address);
    }

    @Transactional
    public Address updateAddress(Long userId, Long addressId, AddressRequest request) {
        Address address = getAddressById(addressId);
        
        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Address not found");
        }

        if (request.getAddressLine() != null) {
            address.setAddressLine(request.getAddressLine());
        }
        if (request.getCity() != null) {
            address.setCity(request.getCity());
        }
        if (request.getPincode() != null) {
            address.setPincode(request.getPincode());
        }
        if (request.getAddressType() != null) {
            address.setAddressType(request.getAddressType());
        }
        if (request.getIsDefault() != null) {
            if (request.getIsDefault()) {
                clearDefaultAddresses(userId);
            }
            address.setIsDefault(request.getIsDefault());
        }

        return addressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = getAddressById(addressId);
        
        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Address not found");
        }

        addressRepository.delete(address);
    }

    private void clearDefaultAddresses(Long userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        for (Address addr : addresses) {
            addr.setIsDefault(false);
            addressRepository.save(addr);
        }
    }
}
