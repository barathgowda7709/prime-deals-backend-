package com.amazon.backend.repository;

import com.amazon.backend.model.Address;
import com.amazon.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
      List<Address> findByUser(User user);
      Optional<Address> findByUserAndIsDefaultTrue(User user);
}
