export interface BankAccount {
  id?: number;
  accountNumber: string;
  balance: number;
  overdraftLimit: number;
  savingsBalance: number;
  savingsDepositLimit: number;
}
