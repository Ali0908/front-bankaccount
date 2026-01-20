import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Account } from '../../static/models/account';

@Injectable({ providedIn: 'root' })
export class AccountContextService {
  private readonly currentAccountSubject = new BehaviorSubject<Account | null>(null);

  public readonly currentAccount$: Observable<Account | null> =
    this.currentAccountSubject.asObservable();

  /**
   * Update the current account
   * @param account The account to set as current
   */
  setCurrentAccount(account: Account | null): void {
    this.currentAccountSubject.next(account);
  }

  /**
   * Get the current account value synchronously
   * @returns The current account or null if not set
   */
  getCurrentAccount(): Account | null {
    return this.currentAccountSubject.value;
  }
}
