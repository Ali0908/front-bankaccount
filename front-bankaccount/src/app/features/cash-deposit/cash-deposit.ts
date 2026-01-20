import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';

import { TransactionModal } from '../../shared/components/transaction-modal/transaction-modal';
import { TransactionResult } from '../../shared/static/models/transaction-result.model';
import { TRANSACTION_MODAL_CONSTANTS } from '../../shared/static/constants/transaction-modal.constants';
import { TRANSACTION_TYPES } from '../../shared/static/constants/transaction-types.constants';
import { BankAccountService } from '../../shared/services/bank-account/bank-account.service';
import { MessageService } from '../../shared/services/message/message.service';
import { AccountContextService } from '../../shared/services/account/account-context.service';

@Component({
  selector: 'app-cash-deposit',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, MatSnackBarModule],
  template: `
    <button mat-raised-button color="accent" class="deposit-button" (click)="openDepositModal()">
      <mat-icon>arrow_downward</mat-icon>
      {{ TRANSACTION_MODAL_CONSTANTS.TITLES[TRANSACTION_TYPES.DEPOSIT] }}
    </button>
  `,
  styles: [
    `
      .deposit-button {
        display: flex;
        align-items: center;
        gap: 8px;
      }
    `,
  ],
})
export class CashDeposit {
  private readonly dialog = inject(MatDialog);
  private readonly bankAccountService = inject(BankAccountService);
  private readonly router = inject(Router);
  private readonly messageService = inject(MessageService);
  private readonly accountContextService = inject(AccountContextService);

  TRANSACTION_MODAL_CONSTANTS: typeof TRANSACTION_MODAL_CONSTANTS = TRANSACTION_MODAL_CONSTANTS;
  TRANSACTION_TYPES = TRANSACTION_TYPES;

  openDepositModal(): void {
    const currentAccount = this.accountContextService.getCurrentAccount();
    const targetAccountNumber = currentAccount?.number || 'ACC001';
    const dialogRef = this.dialog.open(TransactionModal, {
      data: { type: TRANSACTION_TYPES.DEPOSIT, accountNumber: targetAccountNumber },
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
            // Update the account in context service
            if (updatedAccount) {
              const currentAccount = this.accountContextService.getCurrentAccount();
              if (currentAccount) {
                currentAccount.balance = updatedAccount.balance;
                currentAccount.savingsBalance = updatedAccount.savingsBalance;
                currentAccount.overdraftLimit = updatedAccount.overdraftLimit;
                this.accountContextService.setCurrentAccount(currentAccount);
              }
            }
            this.showSuccessSnackbar();
          },
          error: (error) => {
            console.error('❌ Erreur lors du dépôt:', error);
            this.showErrorSnackbar();
          },
        });
      } else {
        // Retour au dashboard si l'utilisateur annule
        this.router.navigate(['/dashboard']);
      }
    });
  }

  private showSuccessSnackbar(): void {
    const feedback = this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK;
    this.messageService.showSuccess(feedback.SUCCESS[this.TRANSACTION_TYPES.DEPOSIT], {
      action: feedback.ACTIONS.SUCCESS_ICON,
      duration: feedback.DURATION_MS.SUCCESS,
    });
  }

  private showErrorSnackbar(): void {
    const feedback = this.TRANSACTION_MODAL_CONSTANTS.FEEDBACK;
    this.messageService.showError(feedback.ERROR[this.TRANSACTION_TYPES.DEPOSIT], {
      action: feedback.ACTIONS.ERROR_ICON,
      duration: feedback.DURATION_MS.ERROR,
    });
  }
}
