package com.fintech.smartwallet.service;

import com.fintech.smartwallet.dto.AccountDTO;
import com.fintech.smartwallet.entity.Account;
import com.fintech.smartwallet.entity.User;
import com.fintech.smartwallet.exception.ResourceNotFoundException;
import com.fintech.smartwallet.repository.AccountRepository;
import com.fintech.smartwallet.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public List<AccountDTO> getUserAccounts(User user) {
        List<Account> accounts = accountRepository.findByUserAndActiveTrue(user);
        return accounts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public AccountDTO createAccount(User user, AccountDTO dto) {
        Account account = new Account();
        account.setUser(user);
        account.setName(dto.getName());
        account.setType(dto.getType());
        account.setBalance(dto.getBalance() != null ? dto.getBalance() : BigDecimal.ZERO);
        account.setDescription(dto.getDescription());

        account = accountRepository.save(account);
        return convertToDTO(account);
    }

    public Account getAccountById(Long id, User user) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Account not found");
        }

        return account;
    }

    private AccountDTO convertToDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setName(account.getName());
        dto.setType(account.getType());
        dto.setBalance(account.getBalance());
        dto.setDescription(account.getDescription());
        return dto;
    }
}
