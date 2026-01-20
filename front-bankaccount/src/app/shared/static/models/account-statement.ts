import { Transaction } from './transaction';
export interface AccountStatement {
  accountNumber: string;
  accountType: string;
  currentBalance: number;
  savingsBalance: number;
  statementDate: string;
  transactions: Transaction[];
}
