package com.fintech.smartwallet.service;

import com.fintech.smartwallet.dto.TransactionDTO;
import com.fintech.smartwallet.entity.Account;
import com.fintech.smartwallet.entity.Category;
import com.fintech.smartwallet.entity.Transaction;
import com.fintech.smartwallet.entity.User;
import com.fintech.smartwallet.repository.AccountRepository;
import com.fintech.smartwallet.repository.CategoryRepository;
import com.fintech.smartwallet.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final AccountService accountService;

    @Transactional
    public TransactionDTO createTransaction(User user, TransactionDTO dto) {
        Account account = accountService.getAccountById(dto.getAccountId(), user);

        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
        }

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setType(dto.getType());
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setTransactionDate(dto.getTransactionDate());

        // Update account balance
        BigDecimal newBalance = account.getBalance();
        if (dto.getType() == Transaction.TransactionType.INCOME) {
            newBalance = newBalance.add(dto.getAmount());
        } else {
            newBalance = newBalance.subtract(dto.getAmount());
        }
        account.setBalance(newBalance);
        accountRepository.save(account);

        transaction = transactionRepository.save(transaction);
        return convertToDTO(transaction);
    }

    public Page<TransactionDTO> getUserTransactions(User user, Pageable pageable) {
        List<Account> accounts = accountRepository.findByUser(user);
        Page<Transaction> transactions = transactionRepository.findByAccountIn(accounts, pageable);
        return transactions.map(this::convertToDTO);
    }

    private TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setAccountId(transaction.getAccount().getId());
        dto.setAccountName(transaction.getAccount().getName());
        if (transaction.getCategory() != null) {
            dto.setCategoryId(transaction.getCategory().getId());
            dto.setCategoryName(transaction.getCategory().getName());
        }
        dto.setType(transaction.getType());
        dto.setAmount(transaction.getAmount());
        dto.setDescription(transaction.getDescription());
        dto.setTransactionDate(transaction.getTransactionDate());
        return dto;
    }
}
