import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatRadioModule } from '@angular/material/radio';
import { TRANSACTION_MODAL_CONSTANTS } from '../../static/constants/transaction-modal.constants';
import { TRANSACTION_TYPES } from '../../static/constants/transaction-types.constants';
import { BANK_OPERATIONS_CONSTANTS } from '../../static/constants/bank-operations.constants';
import { TransactionDialogData } from '../../static/models/transaction-dialog-data.model';
import { TransactionResult } from '../../static/models/transaction-result.model';

@Component({
  selector: 'app-transaction-modal',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatRadioModule,
  ],
  templateUrl: './transaction-modal.html',
  styleUrl: './transaction-modal.scss',
})
export class TransactionModal {
  form: FormGroup;
  readonly constants = TRANSACTION_MODAL_CONSTANTS;
  readonly bankConstants = BANK_OPERATIONS_CONSTANTS;
  readonly amountFieldName = this.constants.FORM_FIELDS.AMOUNT;
  readonly accountTypeFieldName = 'accountType';
  readonly TRANSACTION_TYPES = TRANSACTION_TYPES;

  constructor(
    private readonly fb: FormBuilder,
    public dialogRef: MatDialogRef<TransactionModal>,
    @Inject(MAT_DIALOG_DATA) public data: TransactionDialogData
  ) {
    const formConfig: any = {
      [this.amountFieldName]: [
        '',
        [Validators.required, Validators.min(this.constants.FORM_FIELD.MIN_VALUE)],
      ],
    };

    // Add account type field only for deposits
    if (this.data.type === TRANSACTION_TYPES.DEPOSIT) {
      formConfig[this.accountTypeFieldName] = ['current', Validators.required];
    }

    this.form = this.fb.group(formConfig);
  }

  getTitle(): string {
    return this.constants.TITLES[this.data.type];
  }

  getButtonLabel(): string {
    return this.constants.BUTTONS[this.data.type];
  }

  submit(): void {
    if (this.form.valid) {
      const result: TransactionResult = {
        type: this.data.type,
        accountNumber: this.data.accountNumber,
        amount: this.form.get(this.amountFieldName)?.value,
        accountType: this.form.get(this.accountTypeFieldName)?.value || 'current',
      };
      this.dialogRef.close(result);
    }
  }

  cancel(): void {
    this.dialogRef.close();
  }
}
