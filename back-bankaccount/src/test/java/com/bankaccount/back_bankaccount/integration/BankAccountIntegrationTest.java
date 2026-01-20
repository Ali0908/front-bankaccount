package com.bankaccount.back_bankaccount.integration;

import com.bankaccount.back_bankaccount.dto.DepositRequestDto;
import com.bankaccount.back_bankaccount.dto.WithdrawRequestDto;
import com.bankaccount.back_bankaccount.dto.OverdraftRequestDto;
import com.bankaccount.back_bankaccount.model.BankAccountEntity;
import com.bankaccount.back_bankaccount.repository.IBankAcountRepository;
import com.bankaccount.back_bankaccount.repository.ITransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DisplayName("Bank Account Integration Tests")
class BankAccountIntegrationTest {

  protected MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private IBankAcountRepository bankAccountRepository;

  @Autowired
  private ITransactionRepository transactionRepository;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    objectMapper = new ObjectMapper();
    transactionRepository.deleteAll();
    bankAccountRepository.deleteAll();
  }

  // ========================================
  // FEATURE 1: BASIC OPERATIONS (Deposit/Withdraw)
  // ========================================
  @Nested
  @DisplayName("Feature 1: Basic Operations")
  class BasicOperationsTests {

    @Test
    @DisplayName("Should successfully deposit money to current account")
    void should_deposit_to_current_account() throws Exception {
      // Arrange
      createAccount("ACC001", 1000.0);

      // Act & Assert
      mockMvc
          .perform(
              post("/bank-accounts/cash-deposit")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(asJson(new DepositRequestDto("ACC001", 500.0))))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.balance").value(1500.0))
          .andExpect(jsonPath("$.accountNumber").value("ACC001"));
    }

    @Test
    @DisplayName("Should successfully withdraw money from current account")
    void should_withdraw_from_current_account() throws Exception {
      // Arrange
      createAccount("ACC002", 1000.0);

      // Act & Assert
      mockMvc
          .perform(
              post("/bank-accounts/cash-withdrawal")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(asJson(new WithdrawRequestDto("ACC002", 300.0))))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.balance").value(700.0));
    }

    @Test
    @DisplayName("Should record transaction after deposit")
    void should_record_deposit_transaction() throws Exception {
      // Arrange
      createAccount("ACC003", 1000.0);

      // Act
      mockMvc.perform(
          post("/bank-accounts/cash-deposit")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJson(new DepositRequestDto("ACC003", 250.0))));

      // Assert - Verify transaction was recorded
      mockMvc
          .perform(get("/bank-accounts/statement/ACC003"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.transactions", hasSize(1)))
          .andExpect(jsonPath("$.transactions[0].type").value("Dépôt sur compte courant"))
          .andExpect(jsonPath("$.transactions[0].amount").value(250.0));
    }

    @Test
    @DisplayName("Should reject withdrawal with insufficient balance and no overdraft")
    void should_reject_withdrawal_insufficient_balance() throws Exception {
      // Arrange
      createAccount("ACC004", 100.0);
      // No overdraft enabled

      // Act & Assert
      mockMvc
          .perform(
              post("/bank-accounts/cash-withdrawal")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(asJson(new WithdrawRequestDto("ACC004", 200.0))))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject deposit for non-existent account")
    void should_reject_deposit_nonexistent_account() throws Exception {
      // Act & Assert
      mockMvc
          .perform(
              post("/bank-accounts/cash-deposit")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(asJson(new DepositRequestDto("NONEXISTENT", 100.0))))
          .andExpect(status().isNotFound());
    }
  }

  // ========================================
  // FEATURE 2: OVERDRAFT SYSTEM
  // ========================================
  @Nested
  @DisplayName("Feature 2: Overdraft System")
  class OverdraftSystemTests {

    @Test
    @DisplayName("Should allow withdrawal within overdraft limit")
    void should_allow_withdrawal_within_overdraft_limit() throws Exception {
      // Arrange
      createAccount("OD001", 100.0, 300.0);

      // Act & Assert
      mockMvc
          .perform(
              post("/bank-accounts/cash-withdrawal")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(asJson(new WithdrawRequestDto("OD001", 250.0))))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.balance").value(-150.0));
    }

    @Test
    @DisplayName("Should reject withdrawal exceeding overdraft limit")
    void should_reject_withdrawal_exceeding_overdraft() throws Exception {
      // Arrange
      createAccount("OD002", 100.0, 300.0);

      // Act & Assert
      mockMvc
          .perform(
              post("/bank-accounts/cash-withdrawal")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(asJson(new WithdrawRequestDto("OD002", 500.0))))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should set overdraft limit to 300€ when enabled")
    void should_set_overdraft_limit_300_when_enabled() throws Exception {
      // Arrange
      createAccount("OD003", 1000.0, 0.0);

      // Act & Assert
      mockMvc
          .perform(
              post("/bank-accounts/overdraft")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(asJson(new OverdraftRequestDto("OD003", 300.0))))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.overdraftLimit").value(300.0));
    }

    @Test
    @DisplayName("Should reject overdraft limit exceeding 300€")
    void should_reject_overdraft_exceeding_300() throws Exception {
      // Arrange
      createAccount("OD004", 1000.0, 0.0);

      // Act & Assert
      mockMvc
          .perform(
              post("/bank-accounts/overdraft")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(asJson(new OverdraftRequestDto("OD004", 400.0))))
          .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should allow overdraft limit of 0€ (disable overdraft)")
    void should_allow_overdraft_zero() throws Exception {
      // Arrange
      createAccount("OD005", 1000.0, 200.0);

      // Act & Assert
      mockMvc
          .perform(
              post("/bank-accounts/overdraft")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(asJson(new OverdraftRequestDto("OD005", 0.0))))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.overdraftLimit").value(0.0));
    }
  }

  // ========================================
  // FEATURE 3: SAVINGS ACCOUNT (Livret d'épargne)
  // ========================================
  @Nested
  @DisplayName("Feature 3: Savings Account (Livret d'épargne)")
  class SavingsAccountTests {

    @Test
    @DisplayName("Should successfully deposit to savings account")
    void should_deposit_to_savings_account() throws Exception {
      // Arrange
      createAccountWithSavings("SAV001", 1000.0, 0.0);

      // Act & Assert
      mockMvc
          .perform(
              post("/bank-accounts/savings-deposit")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(asJson(new DepositRequestDto("SAV001", 5000.0))))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.savingsBalance").value(5000.0));
    }

    @Test
    @DisplayName("Should cap savings deposit at 22950€ limit")
    void should_cap_savings_at_limit() throws Exception {
      // Arrange
      createAccountWithSavings("SAV002", 1000.0, 0.0);

      // Act - Try to deposit 25000 (should be capped at 22950)
      mockMvc
          .perform(
              post("/bank-accounts/savings-deposit")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(asJson(new DepositRequestDto("SAV002", 25000.0))))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.savingsBalance").value(22950.0));
    }

    @Test
    @DisplayName("Should partially deposit when it would exceed the limit")
    void should_partially_deposit_to_reach_limit() throws Exception {
      // Arrange
      createAccountWithSavings("SAV003", 1000.0, 15000.0);

      // Act
      mockMvc
          .perform(
              post("/bank-accounts/savings-deposit")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(asJson(new DepositRequestDto("SAV003", 10000.0))))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.savingsBalance").value(22950.0));
    }

    @Test
    @DisplayName("Should record deposit to savings transaction")
    void should_record_savings_deposit_transaction() throws Exception {
      // Arrange
      createAccountWithSavings("SAV004", 1000.0, 0.0);

      // Act
      mockMvc.perform(
          post("/bank-accounts/savings-deposit")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJson(new DepositRequestDto("SAV004", 3000.0))));

      // Assert
      mockMvc
          .perform(get("/bank-accounts/statement/SAV004"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.transactions[0].type").value("Dépôt sur livret d'épargne"))
          .andExpect(jsonPath("$.transactions[0].amount").value(3000.0));
    }

    @Test
    @DisplayName("Should reject overdraft on savings account")
    void should_reject_overdraft_on_savings_account() throws Exception {
      // Arrange
      createAccountWithSavings("SAV-OD", 1000.0, 0.0);

      // Act & Assert
      mockMvc
          .perform(
              post("/bank-accounts/overdraft")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(asJson(new OverdraftRequestDto("SAV-OD", 300.0))))
          .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should maintain savings balance independently from current account")
    void should_maintain_independent_savings_balance() throws Exception {
      // Arrange
      createAccountWithSavings("SAV005", 1000.0, 5000.0);

      // Act - Withdraw from current account and verify balance
      mockMvc.perform(
          post("/bank-accounts/cash-withdrawal")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJson(new WithdrawRequestDto("SAV005", 500.0))))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.balance").value(500.0))
          .andExpect(jsonPath("$.savingsBalance").value(5000.0));
    }
  }

  // ========================================
  // FEATURE 4: STATEMENT & TRANSACTION HISTORY
  // ========================================
  @Nested
  @DisplayName("Feature 4: Statement & Transaction History")
  class StatementTests {

    @Test
    @DisplayName("Should retrieve statement with all transactions from last 30 days")
    void should_retrieve_statement_with_transactions() throws Exception {
      // Arrange
      createAccount("STMT001", 1000.0);

      // Act - Create transactions
      mockMvc.perform(
          post("/bank-accounts/cash-deposit")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJson(createDepositRequest("STMT001", 500.0))));

      mockMvc.perform(
          post("/bank-accounts/cash-withdrawal")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJson(createWithdrawalRequest("STMT001", 200.0))));

      // Assert
      mockMvc
          .perform(get("/bank-accounts/statement/STMT001"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.accountNumber").value("STMT001"))
          .andExpect(jsonPath("$.transactions", hasSize(2)))
          .andExpect(jsonPath("$.transactions[0].type").value("Retrait"))
          .andExpect(jsonPath("$.transactions[1].type").value("Dépôt sur compte courant"));
    }

    @Test
    @DisplayName("Should order transactions in reverse chronological order (newest first)")
    void should_order_transactions_descending() throws Exception {
      // Arrange
      createAccount("STMT002", 1000.0);

      // Act - Create multiple transactions
      for (int i = 0; i < 3; i++) {
        mockMvc.perform(
            post("/bank-accounts/cash-deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(createDepositRequest("STMT002", 100.0))));
      }

      // Assert - First transaction should be the most recent
      MvcResult result =
          mockMvc
              .perform(get("/bank-accounts/statement/STMT002"))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.transactions", hasSize(3)))
              .andReturn();

      String jsonResponse = result.getResponse().getContentAsString();
      // Most recent transaction should be first
      assertThat(jsonResponse).contains("\"type\":\"Dépôt sur compte courant\"");
    }

    @Test
    @DisplayName("Should include balance after each transaction")
    void should_include_balance_after_transaction() throws Exception {
      // Arrange
      createAccount("STMT003", 1000.0);

      // Act
      mockMvc.perform(
          post("/bank-accounts/cash-deposit")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJson(createDepositRequest("STMT003", 500.0))));

      // Assert
      mockMvc
          .perform(get("/bank-accounts/statement/STMT003"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.transactions[0].balanceAfter").value(1500.0));
    }

    @Test
    @DisplayName("Should return empty statement for account with no transactions")
    void should_return_empty_statement_for_new_account() throws Exception {
      // Arrange
      createAccount("STMT004", 1000.0);

      // Act & Assert
      mockMvc
          .perform(get("/bank-accounts/statement/STMT004"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.transactions", hasSize(0)));
    }

    @Test
    @DisplayName("Should return 404 for non-existent account statement")
    void should_return_404_for_nonexistent_account() throws Exception {
      // Act & Assert
      mockMvc.perform(get("/bank-accounts/statement/NONEXISTENT")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should show both savings and withdrawal transactions in statement")
    void should_show_mixed_transaction_types_in_statement() throws Exception {
      // Arrange
      createAccountWithSavings("STMT005", 2000.0, 0.0);

      // Act
      mockMvc.perform(
          post("/bank-accounts/cash-deposit")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJson(createDepositRequest("STMT005", 300.0))));

      mockMvc.perform(
          post("/bank-accounts/savings-deposit")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJson(createSavingsDepositRequest("STMT005", 500.0))));

      mockMvc.perform(
          post("/bank-accounts/cash-withdrawal")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJson(createWithdrawalRequest("STMT005", 200.0))));

      // Assert
      mockMvc
          .perform(get("/bank-accounts/statement/STMT005"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.transactions", hasSize(3)));
    }
  }

  // ========================================
  // HELPER METHODS
  // ========================================

  private BankAccountEntity createAccount(String accountNumber, Double balance) {
    return createAccount(accountNumber, balance, 0.0);
  }

  private BankAccountEntity createAccount(String accountNumber, Double balance, Double overdraftLimit) {
    BankAccountEntity account = new BankAccountEntity();
    account.setAccountNumber(accountNumber);
    account.setBalance(balance);
    account.setOverdraftLimit(overdraftLimit);
    account.setSavingsBalance(0.0);
    account.setSavingsDepositLimit(22950.0);
    return bankAccountRepository.save(account);
  }

  private BankAccountEntity createAccountWithSavings(
      String accountNumber, Double balance, Double savingsBalance) {
    BankAccountEntity account = new BankAccountEntity();
    account.setAccountNumber(accountNumber);
    account.setBalance(balance);
    account.setOverdraftLimit(0.0);
    account.setSavingsBalance(savingsBalance);
    account.setSavingsDepositLimit(22950.0);
    return bankAccountRepository.save(account);
  }

  private DepositRequestDto createDepositRequest(String accountNumber, Double amount) {
    return new DepositRequestDto(accountNumber, amount);
  }

  private WithdrawRequestDto createWithdrawalRequest(String accountNumber, Double amount) {
    return new WithdrawRequestDto(accountNumber, amount);
  }

  private DepositRequestDto createSavingsDepositRequest(String accountNumber, Double amount) {
    return new DepositRequestDto(accountNumber, amount);
  }

  private String asJson(Object obj) throws Exception {
    return objectMapper.writeValueAsString(obj);
  }
}
