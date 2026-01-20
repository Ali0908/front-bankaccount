import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AccountStatementService } from './account-statement.service';
import { AccountStatement } from './../../static/models/account-statement';
import { environment } from '../../../../environments/environment';

describe('AccountStatementService', () => {
  let service: AccountStatementService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AccountStatementService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AccountStatementService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get statement with transactions in antéchronological order', () => {
    const mockStatement: AccountStatement = {
      accountNumber: 'ACC-001',
      accountType: 'Compte Courant',
      currentBalance: 1000,
      savingsBalance: 0,
      statementDate: '2026-01-18T12:00:00',
      transactions: [
        {
          date: '2026-01-18T10:00:00',
          type: 'Dépôt sur compte courant',
          amount: 100,
          balanceAfter: 1000,
        },
        {
          date: '2026-01-17T15:30:00',
          type: 'Retrait',
          amount: -50,
          balanceAfter: 900,
        },
        {
          date: '2026-01-15T09:00:00',
          type: 'Dépôt sur compte courant',
          amount: 200,
          balanceAfter: 950,
        },
      ],
    };

    service.getStatement('ACC-001').subscribe((statement) => {
      expect(statement).toEqual(mockStatement);
      expect(statement.transactions.length).toBe(3);

      // Vérifier l'ordre antéchronologique
      const firstDate = new Date(statement.transactions[0].date);
      const secondDate = new Date(statement.transactions[1].date);
      const thirdDate = new Date(statement.transactions[2].date);

      expect(firstDate.getTime()).toBeGreaterThan(secondDate.getTime());
      expect(secondDate.getTime()).toBeGreaterThan(thirdDate.getTime());
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/bank-accounts/statement/ACC-001`);
    expect(req.request.method).toBe('GET');
    req.flush(mockStatement);
  });

  it('should get statement for account with savings', () => {
    const mockStatement: AccountStatement = {
      accountNumber: 'ACC-002',
      accountType: "Compte Courant + Livret d'épargne",
      currentBalance: 1000,
      savingsBalance: 5000,
      statementDate: '2026-01-18T12:00:00',
      transactions: [
        {
          date: '2026-01-16T14:00:00',
          type: "Dépôt sur livret d'épargne",
          amount: 500,
          balanceAfter: 5000,
        },
      ],
    };

    service.getStatement('ACC-002').subscribe((statement) => {
      expect(statement.accountType).toBe("Compte Courant + Livret d'épargne");
      expect(statement.savingsBalance).toBe(5000);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/bank-accounts/statement/ACC-002`);
    expect(req.request.method).toBe('GET');
    req.flush(mockStatement);
  });

  it('should handle empty transactions list', () => {
    const mockStatement: AccountStatement = {
      accountNumber: 'ACC-003',
      accountType: 'Compte Courant',
      currentBalance: 1000,
      savingsBalance: 0,
      statementDate: '2026-01-18T12:00:00',
      transactions: [],
    };

    service.getStatement('ACC-003').subscribe((statement) => {
      expect(statement.transactions.length).toBe(0);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/bank-accounts/statement/ACC-003`);
    req.flush(mockStatement);
  });

  it('should handle error when account not found', () => {
    service.getStatement('UNKNOWN').subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.status).toBe(404);
      },
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/bank-accounts/statement/UNKNOWN`);
    req.flush('Account not found', { status: 404, statusText: 'Not Found' });
  });
});
