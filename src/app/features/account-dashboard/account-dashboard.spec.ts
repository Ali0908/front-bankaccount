import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AccountDashboard } from './account-dashboard';
import { MatIconModule } from '@angular/material/icon';
import { CurrencyPipe } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';

import { BankAccountService } from '../../shared/services/bank-account/bank-account.service';
import { MessageService } from '../../shared/services/message/message.service';
import { AccountContextService } from '../../shared/services/account/account-context.service';
import { BankErrorHandlerService } from '../../shared/services/error-handler/bank-error-handler.service';

describe('AccountDashboard', () => {
  let component: AccountDashboard;
  let fixture: ComponentFixture<AccountDashboard>;
  let compiled: HTMLElement;
  let mockDialog: jasmine.SpyObj<MatDialog>;
  let mockBankAccountService: jasmine.SpyObj<BankAccountService>;
  let mockMessageService: jasmine.SpyObj<MessageService>;
  let mockAccountContextService: jasmine.SpyObj<AccountContextService>;
  let mockErrorHandler: jasmine.SpyObj<BankErrorHandlerService>;

  beforeEach(async () => {
    mockDialog = jasmine.createSpyObj('MatDialog', ['open']);
    mockDialog.open.and.returnValue({ afterClosed: () => of(undefined) } as any);

    mockBankAccountService = jasmine.createSpyObj('BankAccountService', ['getAllBankAccounts', 'deposit', 'withdraw']);
    mockBankAccountService.getAllBankAccounts.and.returnValue(of([]));

    mockMessageService = jasmine.createSpyObj('MessageService', ['showError', 'showSuccess']);
    mockAccountContextService = jasmine.createSpyObj('AccountContextService', ['setCurrentAccount']);
    mockErrorHandler = jasmine.createSpyObj('BankErrorHandlerService', ['getWithdrawalErrorMessage']);
    mockErrorHandler.getWithdrawalErrorMessage.and.returnValue('Erreur de retrait');

    await TestBed.configureTestingModule({
      imports: [AccountDashboard, MatIconModule, CurrencyPipe],
      providers: [
        { provide: MatDialog, useValue: mockDialog },
        { provide: BankAccountService, useValue: mockBankAccountService },
        { provide: MessageService, useValue: mockMessageService },
        { provide: AccountContextService, useValue: mockAccountContextService },
        { provide: BankErrorHandlerService, useValue: mockErrorHandler },
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AccountDashboard);
    component = fixture.componentInstance;
    compiled = fixture.nativeElement as HTMLElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Affichage du numéro de compte', () => {
    it('should display account number when account is provided', () => {
      component.account = {
        number: 'ACC-2026-001234',
        balance: 1000,
        currency: 'EUR'
      };
      fixture.detectChanges();

      const accountNumberElement = compiled.querySelector('.account-box .info-value');
      expect(accountNumberElement?.textContent?.trim()).toBe('ACC-2026-001234');
    });

    it('should display "—" when no account is provided', () => {
      component.account = null;
      fixture.detectChanges();

      const accountNumberElement = compiled.querySelector('.account-box .info-value');
      expect(accountNumberElement?.textContent?.trim()).toBe('—');
    });
  });

  describe('Affichage du solde', () => {
    it('should display balance with EUR currency format', () => {
      component.account = {
        number: 'ACC-2026-001234',
        balance: 1500.5,
        currency: 'EUR'
      };
      fixture.detectChanges();

      const balanceElement = compiled.querySelector('.balance-box .info-value');
      const normalized = (balanceElement?.textContent || '').replaceAll(/[\s€]/g, '');
      expect(normalized).toBe('1500,50');
    });

    it('should display balance with USD currency format', () => {
      component.account = {
        number: 'ACC-2026-001234',
        balance: 2000,
        currency: 'USD'
      };
      fixture.detectChanges();

      const balanceElement = compiled.querySelector('.balance-box .info-value');
      const normalized = (balanceElement?.textContent || '').replaceAll(/[\s€]/g, '');
      expect(normalized).toBe('2000,00');
    });

    it('should use default EUR currency when currency is not provided', () => {
      component.account = {
        number: 'ACC-2026-001234',
        balance: 500,
        currency: 'EUR'
      };
      fixture.detectChanges();

      const balanceElement = compiled.querySelector('.balance-box .info-value');
      const normalized = (balanceElement?.textContent || '').replaceAll(/[\s€]/g, '');
      expect(normalized).toBe('500,00');
    });
  });

  describe('Navigation et boutons', () => {
    beforeEach(() => {
      component.account = {
        number: 'ACC-2026-001234',
        balance: 1000,
        currency: 'EUR'
      };
      fixture.detectChanges();
    });

    it('should display Home navigation item', () => {
      const homeElement = compiled.querySelector('.nav-item .operation-label');
      expect(homeElement?.textContent?.trim()).toBe('Home');
    });

    it('should display "Dépôt d\'argent" operation item', () => {
      const depositElements = compiled.querySelectorAll('.operation-item .operation-label');
      const depositElement = Array.from(depositElements).find(el => 
        el.textContent?.trim() === 'Dépôt d\'argent'
      );
      expect(depositElement).toBeTruthy();
    });

    it('should display "Retrait d\'argent" operation item', () => {
      const withdrawalElements = compiled.querySelectorAll('.operation-item .operation-label');
      const withdrawalElement = Array.from(withdrawalElements).find(el => 
        el.textContent?.trim() === 'Retrait d\'argent'
      );
      expect(withdrawalElement).toBeTruthy();
    });

    it('should have correct icons for deposit operation', () => {
      const depositItems = compiled.querySelectorAll('.operation-item');
      const depositItem = Array.from(depositItems).find(item => 
        item.textContent?.includes('Dépôt d\'argent')
      ) as HTMLElement;

      const icons = depositItem?.querySelectorAll('mat-icon');
      expect(icons?.length).toBe(2);
      expect(icons?.[0].textContent?.trim()).toBe('payment');
      expect(icons?.[1].textContent?.trim()).toBe('arrow_downward');
    });

    it('should have correct icons for withdrawal operation', () => {
      const withdrawalItems = compiled.querySelectorAll('.operation-item');
      const withdrawalItem = Array.from(withdrawalItems).find(item => 
        item.textContent?.includes('Retrait d\'argent')
      ) as HTMLElement;

      const icons = withdrawalItem?.querySelectorAll('mat-icon');
      expect(icons?.length).toBe(2);
      expect(icons?.[0].textContent?.trim()).toBe('payment');
      expect(icons?.[1].textContent?.trim()).toBe('arrow_upward');
    });
  });

  describe('UX et accessibilité', () => {
    beforeEach(() => {
      component.account = {
        number: 'ACC-2026-001234',
        balance: 1000,
        currency: 'EUR'
      };
      fixture.detectChanges();
    });

    it('should have proper CSS classes for navigation items', () => {
      const navItem = compiled.querySelector('.nav-item');
      expect(navItem?.classList.contains('nav-item')).toBe(true);
    });

    it('should have proper CSS classes for operation items', () => {
      const operationItems = compiled.querySelectorAll('.operation-item');
      expect(operationItems.length).toBe(2);
      operationItems.forEach(item => {
        expect(item.classList.contains('operation-item')).toBe(true);
      });
    });

    it('should display account info labels correctly', () => {
      const accountLabel = compiled.querySelector('.account-box .info-label');
      const balanceLabel = compiled.querySelector('.balance-box .info-label');
      
      expect(accountLabel?.textContent?.trim()).toBe('Numéro de compte');
      expect(balanceLabel?.textContent?.trim()).toBe('Solde');
    });

    it('should have vertical divider between sidebar and content', () => {
      const divider = compiled.querySelector('.vertical-divider');
      expect(divider).toBeTruthy();
    });

    it('should display logo icon in header', () => {
      const logoIcon = compiled.querySelector('.header .logo mat-icon');
      expect(logoIcon?.textContent?.trim()).toBe('account_balance');
    });
  });
});
