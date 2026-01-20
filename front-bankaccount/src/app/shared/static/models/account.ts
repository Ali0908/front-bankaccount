export interface Account {
  number: string;
  balance: number;
  currency: string;
  savingsBalance?: number;
  overdraftLimit?: number;
  savingsDepositLimit?: number;
}
