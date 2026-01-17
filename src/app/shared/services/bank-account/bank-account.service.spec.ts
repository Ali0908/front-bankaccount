import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { BankAccountService } from './bank-account.service';
import { Paths } from '../../static/path';
import { environment } from '../../../../environments/environment.development';

describe('BankAccountService', () => {
  let service: BankAccountService;
  let httpMock: HttpTestingController;
  const apiUrl = environment.apiUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        BankAccountService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(BankAccountService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAllBankAccounts', () => {
    it('should fetch all bank accounts', (done) => {
      const mockResponse = [
        { id: 1, accountNumber: 'ACC001', balance: 1000, accountType: 'CURRENT_ACCOUNT' },
        { id: 2, accountNumber: 'ACC002', balance: 5000, accountType: 'SAVINGS_ACCOUNT' }
      ];

      service.getAllBankAccounts().subscribe({
        next: (response: any) => {
          expect(response).toEqual(mockResponse);
          expect(response.length).toBe(2);
          done();
        },
        error: () => fail('should have succeeded')
      });

      const req = httpMock.expectOne(apiUrl + Paths.PATH_BANK_ACCOUNT);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should handle error when fetching all bank accounts', (done) => {
      service.getAllBankAccounts().subscribe({
        next: () => fail('should have failed with 500 error'),
        error: (error: any) => {
          expect(error.status).toBe(500);
          done();
        }
      });

      const req = httpMock.expectOne(apiUrl + Paths.PATH_BANK_ACCOUNT);
      req.flush('Server error', { status: 500, statusText: 'Server Error' });
    });
  });

  describe('deposit', () => {
    it('should deposit amount successfully', (done) => {
      const accountNumber = 'ACC001';
      const amount = 500;
      const mockResponse = {
        id: 1,
        accountNumber: accountNumber,
        balance: 1500,
        accountType: 'CURRENT_ACCOUNT'
      };

      service.deposit(accountNumber, amount).subscribe({
        next: (response: any) => {
          expect(response).toEqual(mockResponse);
          expect(response.balance).toBe(1500);
          done();
        },
        error: () => fail('should have succeeded')
      });

      const req = httpMock.expectOne(apiUrl + Paths.PATH_BANK_ACCOUNT + Paths.PATH_CASH_DEPOSIT);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({
        accountNumber: accountNumber,
        amount: amount
      });
      req.flush(mockResponse);
    });

    it('should deposit amount on savings account', (done) => {
      const accountNumber = 'ACC002';
      const amount = 1000;
      const mockResponse = {
        id: 2,
        accountNumber: accountNumber,
        balance: 6000,
        accountType: 'SAVINGS_ACCOUNT'
      };

      service.deposit(accountNumber, amount).subscribe({
        next: (response: any) => {
          expect(response.balance).toBe(6000);
          expect(response.accountType).toBe('SAVINGS_ACCOUNT');
          done();
        },
        error: () => fail('should have succeeded')
      });

      const req = httpMock.expectOne(apiUrl + Paths.PATH_BANK_ACCOUNT + Paths.PATH_CASH_DEPOSIT);
      expect(req.request.method).toBe('POST');
      expect(req.request.body.accountNumber).toBe(accountNumber);
      expect(req.request.body.amount).toBe(amount);
      req.flush(mockResponse);
    });

    it('should handle validation error when deposit amount is invalid', (done) => {
      const accountNumber = 'ACC001';
      const amount = -100; // Invalid negative amount

      service.deposit(accountNumber, amount).subscribe({
        next: () => fail('should have failed with validation error'),
        error: (error: any) => {
          expect(error.status).toBe(400);
          done();
        }
      });

      const req = httpMock.expectOne(apiUrl + Paths.PATH_BANK_ACCOUNT + Paths.PATH_CASH_DEPOSIT);
      req.flush('Invalid deposit amount', { status: 400, statusText: 'Bad Request' });
    });

    it('should handle error when deposit exceeds savings account limit', (done) => {
      const accountNumber = 'ACC002';
      const amount = 50000; // Exceeds limit

      service.deposit(accountNumber, amount).subscribe({
        next: () => fail('should have failed with business logic error'),
        error: (error: any) => {
          expect(error.status).toBe(400);
          done();
        }
      });

      const req = httpMock.expectOne(apiUrl + Paths.PATH_BANK_ACCOUNT + Paths.PATH_CASH_DEPOSIT);
      req.flush('Deposit amount exceeds savings account limit', { status: 400, statusText: 'Bad Request' });
    });

    it('should handle server error during deposit', (done) => {
      const accountNumber = 'ACC001';
      const amount = 500;

      service.deposit(accountNumber, amount).subscribe({
        next: () => fail('should have failed with 500 error'),
        error: (error: any) => {
          expect(error.status).toBe(500);
          done();
        }
      });

      const req = httpMock.expectOne(apiUrl + Paths.PATH_BANK_ACCOUNT + Paths.PATH_CASH_DEPOSIT);
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
    });
  });
});
