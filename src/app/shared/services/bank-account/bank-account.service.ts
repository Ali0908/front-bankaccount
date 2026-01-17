import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment.development';
import { Paths } from '../../static/path';
import { BankAccount } from '../../static/models/bank-account';
import { DepositRequest } from '../../static/models/deposit-request';

@Injectable({
  providedIn: 'root',
})
export class BankAccountService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private readonly httpClient: HttpClient) {}

  /**
   * Fetch all bank accounts
   * @returns Observable of BankAccountDto array
   */
  getAllBankAccounts(): Observable<BankAccount[]> {
    return this.httpClient.get<BankAccount[]>(`${this.apiUrl}${Paths.PATH_BANK_ACCOUNT}`);
  }

  /**
   * Deposit money to a bank account
   * @param accountNumber The account number
   * @param amount The amount to deposit
   * @returns Observable of updated BankAccountDto
   */
  deposit(accountNumber: string, amount: number): Observable<BankAccount> {
    const depositRequest: DepositRequest = {
      accountNumber: accountNumber,
      amount: amount
    };
    return this.httpClient.post<BankAccount>(
      `${this.apiUrl}${Paths.PATH_BANK_ACCOUNT}${Paths.PATH_CASH_DEPOSIT}`,
      depositRequest
    );
  }

  /**
   * Withdraw money from a bank account
   * @param accountNumber The account number
   * @param amount The amount to withdraw
   * @returns Observable of updated BankAccountDto
   */
  withdraw(accountNumber: string, amount: number): Observable<BankAccount> {
    const withdrawalRequest: DepositRequest = {
      accountNumber: accountNumber,
      amount: amount
    };
    return this.httpClient.post<BankAccount>(
      `${this.apiUrl}${Paths.PATH_BANK_ACCOUNT}${Paths.PATH_CASH_WITHDRAWAL}`,
      withdrawalRequest
    );
  }
}
