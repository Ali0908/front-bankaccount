import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, registerLocaleData } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import localeFr from '@angular/common/locales/fr';

import { TransactionModal } from '../../shared/components/transaction-modal/transaction-modal';
import { TransactionResult } from '../../shared/static/models/transaction-result.model';
import { TRANSACTION_MODAL_CONSTANTS, TRANSACTION_TYPES } from '../../shared/static/constants/transaction-modal.constants';
import { BankAccountService } from '../../shared/services/bank-account/bank-account.service';
import { Account } from '../../shared/static/models/account';
import { GENERAL_CONSTANTS } from '../../shared/static/constants/general.constants';
import { MessageService } from '../../shared/services/message/message.service';
import { AccountContextService } from '../../shared/services/account/account-context.service';
import { BankErrorHandlerService } from '../../shared/services/error-handler/bank-error-handler.service';

// Enregistrer le locale français
registerLocaleData(localeFr);



@Component({
  selector: 'app-account-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    CurrencyPipe,
    MatSnackBarModule,
  ],
  templateUrl: './account-dashboard.html',
  styleUrl: './account-dashboard.scss',
})
export class AccountDashboard implements OnInit {

  private readonly dialog = inject(MatDialog);
  private readonly bankAccountService = inject(BankAccountService);
  private readonly messageService = inject(MessageService);
  private readonly accountContextService = inject(AccountContextService);
  private readonly errorHandler = inject(BankErrorHandlerService);

  account: Account | null = null;
  GENERAL_CONSTANTS: typeof GENERAL_CONSTANTS = GENERAL_CONSTANTS;
  TRANSACTION_MODAL_CONSTANTS: typeof TRANSACTION_MODAL_CONSTANTS = TRANSACTION_MODAL_CONSTANTS;


ngOnInit(): void {
    this.loadAccount();
  }

  private loadAccount(): void {
    this.bankAccountService.getAllBankAccounts().subscribe({
      next: (accounts) => {
        if (accounts && accounts.length > 0) {
          const firstAccount = accounts[0];
          this.account = {
            number: firstAccount.accountNumber,
            balance: firstAccount.balance,
            currency: GENERAL_CONSTANTS.CURRENCY.EURO_CODE
          };
          this.accountContextService.setCurrentAccount(this.account);
        }
      },
      error: (error) => {
        console.error('❌ Erreur lors du chargement du compte:', error);
        this.messageService.showError(this.GENERAL_CONSTANTS.MESSAGES.LOAD_ACCOUNT_ERROR);
      }
    });
  }
  openDepositModal(): void {
    const accountNumber = this.account?.number || 'ACC001';
    const dialogRef = this.dialog.open(TransactionModal, {
      data: { type: TRANSACTION_TYPES.DEPOSIT, accountNumber: accountNumber }
    });

    dialogRef.afterClosed().subscribe((result: TransactionResult | undefined) => {
      if (result) {
        this.bankAccountService.deposit(result.accountNumber, result.amount).subscribe({
          next: () => {
            this.messageService.showSuccess(
              this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.SUCCESS[TRANSACTION_TYPES.DEPOSIT],
              {
                action: this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.ACTIONS.SUCCESS_ICON,
                duration: this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.DURATION_MS.SUCCESS
              }
            );
            this.loadAccount();
          },
          error: (error) => {
            console.error('❌ Erreur lors du dépôt:', error);
            this.messageService.showError(
              this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.ERROR[TRANSACTION_TYPES.DEPOSIT],
              {
                action: this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.ACTIONS.ERROR_ICON,
                duration: this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.DURATION_MS.ERROR
              }
            );
          }
        });
      }
    });
  }

  openWithdrawalModal(): void {
    const accountNumber = this.account?.number || 'ACC001';
    const dialogRef = this.dialog.open(TransactionModal, {
      data: { type: TRANSACTION_TYPES.WITHDRAWAL, accountNumber: accountNumber }
    });

    dialogRef.afterClosed().subscribe((result: TransactionResult | undefined) => {
      if (result) {
        this.bankAccountService.withdraw(result.accountNumber, result.amount).subscribe({
          next: () => {
            this.messageService.showSuccess(
              this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.SUCCESS[TRANSACTION_TYPES.WITHDRAWAL],
              {
                action: this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.ACTIONS.SUCCESS_ICON,
                duration: this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.DURATION_MS.SUCCESS
              }
            );
            this.loadAccount();
          },
          error: (error) => {
            const errorMessage = this.errorHandler.getWithdrawalErrorMessage(error);
            this.messageService.showError(errorMessage, {
              action: this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.ACTIONS.ERROR_ICON,
              duration: this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK.DURATION_MS.ERROR
            });
          }
        });
      }
    });
  }
}
