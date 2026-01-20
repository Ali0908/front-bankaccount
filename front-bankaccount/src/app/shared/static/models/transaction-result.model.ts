/**
 * Represents the result returned from the transaction modal dialog
 */
export interface TransactionResult {
  type: 'deposit' | 'withdrawal';
  accountNumber: string;
  amount: number;
  accountType: 'current' | 'savings';
}
