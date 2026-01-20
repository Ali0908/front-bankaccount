import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AccountStatement } from './../../static/models/account-statement';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AccountStatementService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/bank-accounts/statement`;

  getStatement(accountNumber: string): Observable<AccountStatement> {
    return this.http.get<AccountStatement>(`${this.apiUrl}/${accountNumber}`);
  }
}
