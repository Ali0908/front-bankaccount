import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, CurrencyPipe, registerLocaleData } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';
import localeFr from '@angular/common/locales/fr';
import { AccountStatementService } from '../../shared/services/account-statement/account-statement.service';
import { AccountStatement } from '../../shared/static/models/account-statement';

import { TransactionModal } from '../../shared/components/transaction-modal/transaction-modal';
import { TransactionResult } from '../../shared/static/models/transaction-result.model';
import {
  TRANSACTION_MODAL_CONSTANTS,
  TRANSACTION_TYPES,
} from '../../shared/static/constants/transaction-modal.constants';
import { BANK_OPERATIONS_CONSTANTS } from '../../shared/static/constants/bank-operations.constants';
import { BankAccountService } from '../../shared/services/bank-account/bank-account.service';
import { OverdraftService } from '../../shared/services/overdraft/overdraft.service';
import { Account } from '../../shared/static/models/account';
import { GENERAL_CONSTANTS } from '../../shared/static/constants/general.constants';
import { MessageService } from '../../shared/services/message/message.service';
import { AccountContextService } from '../../shared/services/account/account-context.service';
import { ErrorHandlerService } from '../../shared/services/error-handler/error-handler.service';

// Enregistrer le locale français
registerLocaleData(localeFr);

@Component({
  selector: 'app-account-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatSlideToggleModule,
    MatDialogModule,
    MatTableModule,
    MatCardModule,
    MatButtonModule,
    CurrencyPipe,
    MatSnackBarModule,
  ],
  templateUrl: './account-dashboard.html',
  styleUrl: './account-dashboard.scss',
})
export class AccountDashboard implements OnInit {
  private readonly dialog = inject(MatDialog);
  private readonly bankAccountService = inject(BankAccountService);
  private readonly overdraftService = inject(OverdraftService);
  private readonly messageService = inject(MessageService);
  private readonly accountContextService = inject(AccountContextService);
  private readonly errorHandler = inject(ErrorHandlerService);
  private readonly router = inject(Router);
  private readonly statementService = inject(AccountStatementService);

  account: Account | null = null;
  isOverdraftEnabled = signal(false);
  showStatement = signal(false);
  statement = signal<AccountStatement | null>(null);
  loadingStatement = signal(false);
  displayedColumns: string[] = ['date', 'type', 'amount', 'balanceAfter'];
  GENERAL_CONSTANTS: typeof GENERAL_CONSTANTS = GENERAL_CONSTANTS;
  TRANSACTION_MODAL_CONSTANTS: typeof TRANSACTION_MODAL_CONSTANTS = TRANSACTION_MODAL_CONSTANTS;
  BANK_OPERATIONS_CONSTANTS: typeof BANK_OPERATIONS_CONSTANTS = BANK_OPERATIONS_CONSTANTS;

  ngOnInit(): void {
    this.loadAccount();

    // Listen to account context changes
    this.accountContextService.currentAccount$.subscribe((account) => {
      if (account) {
        this.account = account;
        // Update overdraft toggle state
        this.isOverdraftEnabled.set((account.overdraftLimit ?? 0) > 0);
      }
    });
  }

  private loadAccount(): void {
    this.bankAccountService.getAllBankAccounts().subscribe({
      next: (accounts) => {
        if (accounts && accounts.length > 0) {
          const firstAccount = accounts[0];
          this.account = {
            number: firstAccount.accountNumber,
            balance: firstAccount.balance,
            currency: GENERAL_CONSTANTS.CURRENCY.EURO_CODE,
            overdraftLimit: firstAccount.overdraftLimit,
            savingsBalance: firstAccount.savingsBalance,
            savingsDepositLimit: firstAccount.savingsDepositLimit,
          };
          this.accountContextService.setCurrentAccount(this.account);
        }
      },
      error: (error) => {
        console.error('❌ Erreur lors du chargement du compte:', error);
        this.messageService.showError(this.GENERAL_CONSTANTS.MESSAGES.LOAD_ACCOUNT_ERROR);
      },
    });
  }
  openDepositModal(): void {
    const accountNumber = this.account?.number || 'ACC001';
    const dialogRef = this.dialog.open(TransactionModal, {
      data: { type: TRANSACTION_TYPES.DEPOSIT, accountNumber: accountNumber },
    });

    dialogRef.afterClosed().subscribe((result: TransactionResult | undefined) => {
      if (result) {
        // Choose deposit method based on account type
        const depositMethod =
          result.accountType === 'savings'
            ? this.bankAccountService.depositToSavings(result.accountNumber, result.amount)
            : this.bankAccountService.deposit(result.accountNumber, result.amount);

        depositMethod.subscribe({
          next: (updatedAccount) => {
            // Update account context with new values
            if (updatedAccount) {
              const currentAccount = this.accountContextService.getCurrentAccount();
              if (currentAccount) {
                currentAccount.balance = updatedAccount.balance;
                currentAccount.savingsBalance = updatedAccount.savingsBalance;
                currentAccount.overdraftLimit = updatedAccount.overdraftLimit;
                this.accountContextService.setCurrentAccount(currentAccount);
              }
            }
            this.messageService.showSuccess(
              this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.SUCCESS[TRANSACTION_TYPES.DEPOSIT],
              {
                action: this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.ACTIONS.SUCCESS_ICON,
                duration: this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.DURATION_MS.SUCCESS,
              }
            );
          },
          error: (error) => {
            console.error('❌ Erreur lors du dépôt:', error);
            this.messageService.showError(
              this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.ERROR[TRANSACTION_TYPES.DEPOSIT],
              {
                action: this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.ACTIONS.ERROR_ICON,
                duration: this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.DURATION_MS.ERROR,
              }
            );
          },
        });
      }
    });
  }

  openWithdrawalModal(): void {
    const accountNumber = this.account?.number || 'ACC001';
    const dialogRef = this.dialog.open(TransactionModal, {
      data: { type: TRANSACTION_TYPES.WITHDRAWAL, accountNumber: accountNumber },
    });

    dialogRef.afterClosed().subscribe((result: TransactionResult | undefined) => {
      if (result) {
        this.bankAccountService.withdraw(result.accountNumber, result.amount).subscribe({
          next: () => {
            this.messageService.showSuccess(
              this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.SUCCESS[TRANSACTION_TYPES.WITHDRAWAL],
              {
                action: this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.ACTIONS.SUCCESS_ICON,
                duration: this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.DURATION_MS.SUCCESS,
              }
            );
            this.loadAccount();
          },
          error: (error) => {
            const errorMessage = this.errorHandler.getWithdrawalErrorMessage(error);
            this.messageService.showError(errorMessage, {
              action: this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.ACTIONS.ERROR_ICON,
              duration: this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.DURATION_MS.ERROR,
            });
          },
        });
      }
    });
  }
  viewStatement(): void {
    if (!this.account?.number) {
      this.messageService.showError(this.GENERAL_CONSTANTS.MESSAGES.SELECT_ACCOUNT_ERROR);
      return;
    }

    if (this.showStatement()) {
      // Si déjà affiché, on cache
      this.showStatement.set(false);
    } else {
      // Charger et afficher le relevé
      this.loadingStatement.set(true);
      this.statementService.getStatement(this.account.number).subscribe({
        next: (statement) => {
          this.statement.set(statement);
          this.showStatement.set(true);
          this.loadingStatement.set(false);
        },
        error: (err) => {
          this.messageService.showError(this.GENERAL_CONSTANTS.MESSAGES.LOAD_STATEMENT_ERROR);
          this.loadingStatement.set(false);
          console.error('Error loading statement:', err);
        },
      });
    }
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  formatAmount(amount: number): string {
    return amount.toFixed(2) + ' €';
  }
  onOverdraftToggleChange(isEnabled: boolean): void {
    const accountNumber = this.account?.number;
    if (!accountNumber) {
      this.messageService.showError(
        this.BANK_OPERATIONS_CONSTANTS.OVERDRAFT.MESSAGES.ACCOUNT_NUMBER_NOT_FOUND
      );
      this.isOverdraftEnabled.set(false);
      return;
    }

    const overdraftLimit = isEnabled ? 300 : 0;
    this.overdraftService.setOverdraftLimit(accountNumber, overdraftLimit).subscribe({
      next: () => {
        const message = isEnabled
          ? this.BANK_OPERATIONS_CONSTANTS.OVERDRAFT.MESSAGES.ENABLED
          : this.BANK_OPERATIONS_CONSTANTS.OVERDRAFT.MESSAGES.DISABLED;
        const actionIcon = isEnabled
          ? this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.ACTIONS.SUCCESS_ICON
          : this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.ACTIONS.ERROR_ICON;
        this.messageService.showSuccess(message, {
          action: actionIcon,
          duration: 3000,
        });
        this.isOverdraftEnabled.set(isEnabled);
      },
      error: (error) => {
        console.error('❌ Erreur lors de la modification du découvert:', error);
        this.messageService.showError(
          this.BANK_OPERATIONS_CONSTANTS.OVERDRAFT.MESSAGES.UPDATE_ERROR,
          {
            action: 'close',
            duration: 5000,
          }
        );
        this.isOverdraftEnabled.set(!isEnabled);
      },
    });
  }
}
