package com.foodie.service;

import com.foodie.dto.request.AddressRequest;
import com.foodie.dto.response.AddressResponse;
import com.foodie.entity.Address;
import com.foodie.entity.User;
import com.foodie.exception.ResourceNotFoundException;
import com.foodie.mapper.EntityDtoMapper;
import com.foodie.repository.AddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;
    private final EntityDtoMapper mapper;

    public AddressService(AddressRepository addressRepository, EntityDtoMapper mapper) {
        this.addressRepository = addressRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getUserAddresses(User user) {
        return addressRepository.findByUserId(user.getId())
                .stream()
                .map(mapper::toAddressResponse)
                .collect(Collectors.toList());
    }

    public AddressResponse addAddress(User user, AddressRequest req) {
        if (req.isDefaultAddress()) {
            List<Address> existing = addressRepository.findByUserId(user.getId());
            for (Address a : existing) {
                a.setDefaultAddress(false);
            }
            addressRepository.saveAll(existing);
        }

        Address address = new Address();
        address.setUser(user);
        address.setLabel(req.getLabel());
        address.setStreet(req.getStreet());
        address.setCity(req.getCity());
        address.setPincode(req.getPincode());
        address.setLatitude(req.getLatitude());
        address.setLongitude(req.getLongitude());
        address.setDefaultAddress(req.isDefaultAddress());

        address = addressRepository.save(address);
        return mapper.toAddressResponse(address);
    }

    public void deleteAddress(User user, Long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + addressId));
        addressRepository.delete(address);
    }
}