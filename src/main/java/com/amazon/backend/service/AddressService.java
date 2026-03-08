package com.amazon.backend.service;

import com.amazon.backend.dto.AddressRequest;
import com.amazon.backend.dto.AddressResponse;
import com.amazon.backend.model.Address;
import com.amazon.backend.model.User;
import com.amazon.backend.repository.AddressRepository;
import com.amazon.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public List<AddressResponse> getAddresses(String email) {
        User user = getUser(email);
        return addressRepository.findByUser(user)
                .stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse addAddress(String email, AddressRequest request) {
        User user = getUser(email);

        // If this is set as default, remove default from others
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.findByUserAndIsDefaultTrue(user)
                    .ifPresent(existing -> {
                        existing.setIsDefault(false);
                        addressRepository.save(existing);
                    });
        }

        Address address = Address.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .pinCode(request.getPinCode())
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                .build();

        return buildResponse(addressRepository.save(address));
    }

    @Transactional
    public AddressResponse setDefault(String email, Long addressId) {
        User user = getUser(email);

        // Remove existing default
        addressRepository.findByUserAndIsDefaultTrue(user)
                .ifPresent(existing -> {
                    existing.setIsDefault(false);
                    addressRepository.save(existing);
                });

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        address.setIsDefault(true);
        return buildResponse(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(String email, Long addressId) {
        User user = getUser(email);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        addressRepository.delete(address);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private AddressResponse buildResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setFullName(address.getFullName());
        response.setPhone(address.getPhone());
        response.setStreet(address.getStreet());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setPinCode(address.getPinCode());
        response.setIsDefault(address.getIsDefault());
        return response;
    }
}
