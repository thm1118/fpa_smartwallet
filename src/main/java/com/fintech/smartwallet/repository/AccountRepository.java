package com.fintech.smartwallet.repository;

import com.fintech.smartwallet.entity.Account;
import com.fintech.smartwallet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserAndActiveTrue(User user);
    List<Account> findByUser(User user);
}
