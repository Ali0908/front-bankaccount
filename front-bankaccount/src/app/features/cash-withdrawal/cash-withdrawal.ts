import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';

import { TransactionModal } from '../../shared/components/transaction-modal/transaction-modal';
import { TransactionResult } from '../../shared/static/models/transaction-result.model';
import { TRANSACTION_TYPES } from '../../shared/static/constants/transaction-types.constants';
import { BANK_OPERATIONS_CONSTANTS } from '../../shared/static/constants/bank-operations.constants';
import { BankAccountService } from '../../shared/services/bank-account/bank-account.service';
import { MessageService } from '../../shared/services/message/message.service';
import { ErrorHandlerService } from '../../shared/services/error-handler/error-handler.service';
import { AccountContextService } from '../../shared/services/account/account-context.service';

@Component({
  selector: 'app-cash-withdrawal',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule],
  template: `
    <button
      mat-raised-button
      color="warn"
      class="withdrawal-button"
      (click)="openWithdrawalModal()"
    >
      <mat-icon>arrow_upward</mat-icon>
      {{ WITHDRAWAL_TITLE }}
    </button>
  `,
  styles: [
    `
      .withdrawal-button {
        display: flex;
        align-items: center;
        gap: 8px;
      }
    `,
  ],
})
export class CashWithdrawal {
  private readonly dialog = inject(MatDialog);
  private readonly bankAccountService = inject(BankAccountService);
  private readonly messageService = inject(MessageService);
  private readonly errorHandler = inject(ErrorHandlerService);
  private readonly router = inject(Router);
  private readonly accountContextService = inject(AccountContextService);
  private readonly WITHDRAWAL_SUCCESS = BANK_OPERATIONS_CONSTANTS.WITHDRAWAL.SUCCESS_MESSAGE;
  protected readonly WITHDRAWAL_TITLE = BANK_OPERATIONS_CONSTANTS.WITHDRAWAL.TITLE;

  openWithdrawalModal(): void {
    const currentAccount = this.accountContextService.getCurrentAccount();
    const targetAccountNumber = currentAccount?.number || 'ACC001';
    const dialogRef = this.dialog.open(TransactionModal, {
      data: { type: TRANSACTION_TYPES.WITHDRAWAL, accountNumber: targetAccountNumber },
    });

    dialogRef.afterClosed().subscribe((result: TransactionResult | undefined) => {
      if (result) {
        this.bankAccountService.withdraw(result.accountNumber, result.amount).subscribe({
          next: () => {
            this.messageService.showSuccess(this.WITHDRAWAL_SUCCESS);
          },
          error: (error) => {
            const errorMessage = this.errorHandler.getWithdrawalErrorMessage(error);
            this.messageService.showError(errorMessage);
          },
        });
      } else {
        this.router.navigate(['/dashboard']);
      }
    });
  }
}
