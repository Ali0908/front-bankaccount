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
      providers: [BankAccountService, provideHttpClient(), provideHttpClientTesting()],
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
        { id: 2, accountNumber: 'ACC002', balance: 5000, accountType: 'SAVINGS_ACCOUNT' },
      ];

      service.getAllBankAccounts().subscribe({
        next: (response: any) => {
          expect(response).toEqual(mockResponse);
          expect(response.length).toBe(2);
          done();
        },
        error: () => fail('should have succeeded'),
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
        },
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
        accountType: 'CURRENT_ACCOUNT',
      };

      service.deposit(accountNumber, amount).subscribe({
        next: (response: any) => {
          expect(response).toEqual(mockResponse);
          expect(response.balance).toBe(1500);
          done();
        },
        error: () => fail('should have succeeded'),
      });

      const req = httpMock.expectOne(apiUrl + Paths.PATH_BANK_ACCOUNT + Paths.PATH_CASH_DEPOSIT);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({
        accountNumber: accountNumber,
        amount: amount,
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
        accountType: 'SAVINGS_ACCOUNT',
      };

      service.deposit(accountNumber, amount).subscribe({
        next: (response: any) => {
          expect(response.balance).toBe(6000);
          expect(response.accountType).toBe('SAVINGS_ACCOUNT');
          done();
        },
        error: () => fail('should have succeeded'),
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
        },
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
        },
      });

      const req = httpMock.expectOne(apiUrl + Paths.PATH_BANK_ACCOUNT + Paths.PATH_CASH_DEPOSIT);
      req.flush('Deposit amount exceeds savings account limit', {
        status: 400,
        statusText: 'Bad Request',
      });
    });

    it('should handle server error during deposit', (done) => {
      const accountNumber = 'ACC001';
      const amount = 500;

      service.deposit(accountNumber, amount).subscribe({
        next: () => fail('should have failed with 500 error'),
        error: (error: any) => {
          expect(error.status).toBe(500);
          done();
        },
      });

      const req = httpMock.expectOne(apiUrl + Paths.PATH_BANK_ACCOUNT + Paths.PATH_CASH_DEPOSIT);
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('withdraw', () => {
    it('should withdraw amount successfully', (done) => {
      const accountNumber = 'ACC001';
      const amount = 300;
      const mockResponse = {
        id: 1,
        accountNumber: accountNumber,
        balance: 700,
        accountType: 'CURRENT_ACCOUNT',
      };

      service.withdraw(accountNumber, amount).subscribe({
        next: (response: any) => {
          expect(response).toEqual(mockResponse);
          expect(response.balance).toBe(700);
          done();
        },
        error: () => fail('should have succeeded'),
      });

      const req = httpMock.expectOne(apiUrl + Paths.PATH_BANK_ACCOUNT + Paths.PATH_CASH_WITHDRAWAL);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({
        accountNumber: accountNumber,
        amount: amount,
      });
      req.flush(mockResponse);
    });

    it('should handle error when withdrawing more than available balance', (done) => {
      const accountNumber = 'ACC001';
      const amount = 2000; // Exceeds balance

      service.withdraw(accountNumber, amount).subscribe({
        next: () => fail('should have failed with insufficient balance error'),
        error: (error: any) => {
          expect(error.status).toBe(400);
          done();
        },
      });

      const req = httpMock.expectOne(apiUrl + Paths.PATH_BANK_ACCOUNT + Paths.PATH_CASH_WITHDRAWAL);
      req.flush('Insufficient balance for withdrawal', {
        status: 400,
        statusText: 'Bad Request',
      });
    });

    it('should allow withdrawal with overdraft authorization', (done) => {
      const accountNumber = 'ACC001';
      const amount = 1200;
      const mockResponse = {
        id: 1,
        accountNumber: accountNumber,
        balance: -200,
        overdraftLimit: 300,
        accountType: 'CURRENT_ACCOUNT',
      };

      service.withdraw(accountNumber, amount).subscribe({
        next: (response: any) => {
          expect(response.balance).toBe(-200);
          expect(response.overdraftLimit).toBe(300);
          done();
        },
        error: () => fail('should have succeeded'),
      });

      const req = httpMock.expectOne(apiUrl + Paths.PATH_BANK_ACCOUNT + Paths.PATH_CASH_WITHDRAWAL);
      expect(req.request.method).toBe('POST');
      req.flush(mockResponse);
    });

    it('should handle validation error when withdrawal amount is invalid', (done) => {
      const accountNumber = 'ACC001';
      const amount = -50; // Invalid negative amount

      service.withdraw(accountNumber, amount).subscribe({
        next: () => fail('should have failed with validation error'),
        error: (error: any) => {
          expect(error.status).toBe(400);
          done();
        },
      });

      const req = httpMock.expectOne(apiUrl + Paths.PATH_BANK_ACCOUNT + Paths.PATH_CASH_WITHDRAWAL);
      req.flush('Invalid withdrawal amount', { status: 400, statusText: 'Bad Request' });
    });

    it('should handle server error during withdrawal', (done) => {
      const accountNumber = 'ACC001';
      const amount = 100;

      service.withdraw(accountNumber, amount).subscribe({
        next: () => fail('should have failed with 500 error'),
        error: (error: any) => {
          expect(error.status).toBe(500);
          done();
        },
      });

      const req = httpMock.expectOne(apiUrl + Paths.PATH_BANK_ACCOUNT + Paths.PATH_CASH_WITHDRAWAL);
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('depositToSavings', () => {
    it('should deposit to savings account successfully', (done) => {
      const accountNumber = 'SAV001';
      const amount = 1000;
      const mockResponse = {
        id: 3,
        accountNumber: accountNumber,
        savingsBalance: 5000,
        savingsDepositLimit: 22950,
        accountType: 'SAVINGS_ACCOUNT',
      };

      service.depositToSavings(accountNumber, amount).subscribe({
        next: (response: any) => {
          expect(response).toEqual(mockResponse);
          expect(response.savingsBalance).toBe(5000);
          done();
        },
        error: () => fail('should have succeeded'),
      });

      const req = httpMock.expectOne(apiUrl + Paths.PATH_BANK_ACCOUNT + Paths.PATH_SAVINGS_DEPOSIT);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({
        accountNumber: accountNumber,
        amount: amount,
      });
      req.flush(mockResponse);
    });

    it('should handle partial deposit when exceeding savings limit', (done) => {
      const accountNumber = 'SAV001';
      const amount = 1000;
      const mockResponse = {
        id: 3,
        accountNumber: accountNumber,
        savingsBalance: 22950,
        savingsDepositLimit: 22950,
        accountType: 'SAVINGS_ACCOUNT',
      };

      service.depositToSavings(accountNumber, amount).subscribe({
        next: (response: any) => {
          expect(response.savingsBalance).toBe(22950);
          done();
        },
        error: () => fail('should have succeeded'),
      });

      const req = httpMock.expectOne(apiUrl + Paths.PATH_BANK_ACCOUNT + Paths.PATH_SAVINGS_DEPOSIT);
      req.flush(mockResponse);
    });

    it('should handle error when savings account is at maximum capacity', (done) => {
      const accountNumber = 'SAV001';
      const amount = 100;

      service.depositToSavings(accountNumber, amount).subscribe({
        next: () => fail('should have failed with capacity error'),
        error: (error: any) => {
          expect(error.status).toBe(400);
          done();
        },
      });

      const req = httpMock.expectOne(apiUrl + Paths.PATH_BANK_ACCOUNT + Paths.PATH_SAVINGS_DEPOSIT);
      req.flush('Savings account is at maximum capacity', {
        status: 400,
        statusText: 'Bad Request',
      });
    });

    it('should handle error when depositing to unknown savings account', (done) => {
      const accountNumber = 'UNKNOWN';
      const amount = 500;

      service.depositToSavings(accountNumber, amount).subscribe({
        next: () => fail('should have failed with not found error'),
        error: (error: any) => {
          expect(error.status).toBe(404);
          done();
        },
      });

      const req = httpMock.expectOne(apiUrl + Paths.PATH_BANK_ACCOUNT + Paths.PATH_SAVINGS_DEPOSIT);
      req.flush('Account not found', { status: 404, statusText: 'Not Found' });
    });

    it('should handle server error during savings deposit', (done) => {
      const accountNumber = 'SAV001';
      const amount = 500;

      service.depositToSavings(accountNumber, amount).subscribe({
        next: () => fail('should have failed with 500 error'),
        error: (error: any) => {
          expect(error.status).toBe(500);
          done();
        },
      });

      const req = httpMock.expectOne(apiUrl + Paths.PATH_BANK_ACCOUNT + Paths.PATH_SAVINGS_DEPOSIT);
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
    });
  });
});
