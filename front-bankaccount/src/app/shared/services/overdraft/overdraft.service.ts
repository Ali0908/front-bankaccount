import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment.development';
import { Paths } from '../../static/path';

@Injectable({ providedIn: 'root' })
export class OverdraftService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl + Paths.PATH_BANK_ACCOUNT;

  setOverdraftLimit(accountNumber: string, overdraftLimit: number): Observable<any> {
    return this.http.post(`${this.apiUrl}${Paths.PATH_OVERDRAFT}`, {
      accountNumber,
      overdraftLimit,
    });
  }
}
