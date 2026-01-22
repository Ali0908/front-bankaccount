import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AccountStatementComponent } from './account-statement';
import { AccountStatementService } from './../../shared/services/account-statement/account-statement.service';
import { AccountStatement } from './../../shared/static/models/account-statement';

describe('AccountStatementComponent', () => {
  let component: AccountStatementComponent;
  let fixture: ComponentFixture<AccountStatementComponent>;
  let service: jasmine.SpyObj<AccountStatementService>;
  let router: jasmine.SpyObj<Router>;

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
    ],
  };

  beforeEach(async () => {
    const serviceSpy = jasmine.createSpyObj('AccountStatementService', ['getStatement']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [AccountStatementComponent],
      providers: [
        { provide: AccountStatementService, useValue: serviceSpy },
        { provide: Router, useValue: routerSpy },
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    }).compileComponents();

    service = TestBed.inject(AccountStatementService) as jasmine.SpyObj<AccountStatementService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    fixture = TestBed.createComponent(AccountStatementComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load statement on init when account number is present', () => {
    // Simulate navigation state using History API
    history.pushState({ accountNumber: 'ACC-001' }, '', '/');

    service.getStatement.and.returnValue(of(mockStatement));

    fixture.detectChanges(); // Triggers ngOnInit

    expect(service.getStatement).toHaveBeenCalledWith('ACC-001');
    expect(component.statement()).toEqual(mockStatement);
    expect(component.loading()).toBe(false);
    expect(component.error()).toBeNull();
  });

  it('should display transactions in antéchronological order', () => {
    // Simulate navigation state
    history.pushState({ accountNumber: 'ACC-001' }, '', '/');

    service.getStatement.and.returnValue(of(mockStatement));

    fixture.detectChanges();

    const transactions = component.statement()!.transactions;
    expect(transactions.length).toBe(2);

    const firstDate = new Date(transactions[0].date);
    const secondDate = new Date(transactions[1].date);
    expect(firstDate.getTime()).toBeGreaterThan(secondDate.getTime());
  });

  it('should handle error when loading statement', fakeAsync(() => {
    // Simulate navigation state with unknown account
    history.pushState({ accountNumber: 'UNKNOWN' }, '', '/');

    service.getStatement.and.returnValue(throwError(() => new Error('Account not found')));

    spyOn(console, 'error'); // Suppress error logging

    fixture.detectChanges();
    tick();

    expect(component.error()).toBe('Impossible de charger le relevé de compte');
    expect(component.loading()).toBe(false);
    expect(component.statement()).toBeNull();
  }));

  it('should navigate back when goBack is called', () => {
    component.goBack();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should format date correctly', () => {
    const formattedDate = component.formatDate('2026-01-18T10:30:00');
    expect(formattedDate).toMatch(/18\/01\/2026.*10:30/);
  });

  it('should format amount with euro symbol', () => {
    expect(component.formatAmount(100)).toBe('100.00 €');
    expect(component.formatAmount(-50.5)).toBe('-50.50 €');
  });

  it('should handle statement with savings account', () => {
    const statementWithSavings: AccountStatement = {
      ...mockStatement,
      accountType: "Compte Courant + Livret d'épargne",
      savingsBalance: 5000,
    };

    // Simulate navigation state
    history.pushState({ accountNumber: 'ACC-001' }, '', '/');

    service.getStatement.and.returnValue(of(statementWithSavings));

    fixture.detectChanges();

    expect(component.statement()!.accountType).toBe("Compte Courant + Livret d'épargne");
    expect(component.statement()!.savingsBalance).toBe(5000);
  });

  it('should display message when no transactions', () => {
    const emptyStatement: AccountStatement = {
      ...mockStatement,
      transactions: [],
    };

    // Simulate navigation state
    history.pushState({ accountNumber: 'ACC-001' }, '', '/');

    service.getStatement.and.returnValue(of(emptyStatement));

    fixture.detectChanges();

    expect(component.statement()!.transactions.length).toBe(0);
  });
});
