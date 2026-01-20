/**
 * Represents the data passed to the transaction modal dialog
 */
export interface TransactionDialogData {
  type: 'deposit' | 'withdrawal';
  accountNumber: string;
}
