package com.amazon.backend.service;

import com.amazon.backend.dto.ShippingAddressRequest;
import com.amazon.backend.dto.ShippingAddressResponse;
import com.amazon.backend.model.ShippingAddress;
import com.amazon.backend.model.User;
import com.amazon.backend.repository.ShippingAddressRepository;
import com.amazon.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShippingAddressService {

    private final ShippingAddressRepository addressRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private ShippingAddressResponse toResponse(ShippingAddress a) {
        ShippingAddressResponse r = new ShippingAddressResponse();
        r.setId(a.getId());
        r.setFullName(a.getFullName());
        r.setPhone(a.getPhone());
        r.setAddressLine1(a.getAddressLine1());
        r.setAddressLine2(a.getAddressLine2());
        r.setCity(a.getCity());
        r.setState(a.getState());
        r.setPincode(a.getPincode());
        r.setCountry(a.getCountry());
        r.setIsDefault(a.getIsDefault());
        return r;
    }

    public List<ShippingAddressResponse> getMyAddresses() {
        User user = getCurrentUser();
        return addressRepository.findByUser(user).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ShippingAddressResponse addAddress(ShippingAddressRequest request) {
        User user = getCurrentUser();

        // If this is the first address or marked as default, clear existing defaults
        if (Boolean.TRUE.equals(request.getIsDefault()) || addressRepository.countByUser(user) == 0) {
            addressRepository.findByUser(user).forEach(a -> {
                a.setIsDefault(false);
                addressRepository.save(a);
            });
            request.setIsDefault(true);
        }

        ShippingAddress address = new ShippingAddress();
        address.setUser(user);
        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setCountry(request.getCountry() != null ? request.getCountry() : "India");
        address.setIsDefault(request.getIsDefault());

        return toResponse(addressRepository.save(address));
    }

    public ShippingAddressResponse updateAddress(Long id, ShippingAddressRequest request) {
        User user = getCurrentUser();
        ShippingAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        // Handle default switching
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.findByUser(user).forEach(a -> {
                a.setIsDefault(false);
                addressRepository.save(a);
            });
        }

        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setCountry(request.getCountry() != null ? request.getCountry() : "India");
        address.setIsDefault(request.getIsDefault());

        return toResponse(addressRepository.save(address));
    }

    public void deleteAddress(Long id) {
        User user = getCurrentUser();
        ShippingAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        addressRepository.delete(address);
    }

    public ShippingAddressResponse setDefault(Long id) {
        User user = getCurrentUser();

        // Clear all defaults first
        addressRepository.findByUser(user).forEach(a -> {
            a.setIsDefault(false);
            addressRepository.save(a);
        });

        ShippingAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        address.setIsDefault(true);
        return toResponse(addressRepository.save(address));
    }
}
