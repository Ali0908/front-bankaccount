import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';

import { App } from './app';
import { BankAccountService } from './shared/services/bank-account/bank-account.service';
import { MessageService } from './shared/services/message/message.service';
import { AccountContextService } from './shared/services/account/account-context.service';
import { ErrorHandlerService } from './shared/services/error-handler/error-handler.service';

describe('App', () => {
  let mockDialog: jasmine.SpyObj<MatDialog>;
  let mockBankAccountService: jasmine.SpyObj<BankAccountService>;
  let mockMessageService: jasmine.SpyObj<MessageService>;
  let mockAccountContextService: Partial<AccountContextService>;
  let mockErrorHandler: jasmine.SpyObj<ErrorHandlerService>;

  beforeEach(async () => {
    mockDialog = jasmine.createSpyObj('MatDialog', ['open']);
    mockDialog.open.and.returnValue({ afterClosed: () => of(undefined) } as any);

    mockBankAccountService = jasmine.createSpyObj('BankAccountService', [
      'getAllBankAccounts',
      'deposit',
      'depositToSavings',
      'withdraw',
    ]);
    mockBankAccountService.getAllBankAccounts.and.returnValue(of([]));

    mockMessageService = jasmine.createSpyObj('MessageService', ['showError', 'showSuccess']);
    mockAccountContextService = {
      currentAccount$: of(null),
      setCurrentAccount: jasmine.createSpy('setCurrentAccount'),
      getCurrentAccount: jasmine.createSpy('getCurrentAccount').and.returnValue(null),
    };
    mockErrorHandler = jasmine.createSpyObj('ErrorHandlerService', [
      'getWithdrawalErrorMessage',
      'getDepositErrorMessage',
    ]);
    mockErrorHandler.getWithdrawalErrorMessage.and.returnValue('Erreur de retrait');

    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        { provide: MatDialog, useValue: mockDialog },
        { provide: BankAccountService, useValue: mockBankAccountService },
        { provide: MessageService, useValue: mockMessageService },
        { provide: AccountContextService, useValue: mockAccountContextService },
        { provide: ErrorHandlerService, useValue: mockErrorHandler },
        provideRouter([]),
        provideHttpClient(),
      ],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should render title', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('router-outlet')).toBeTruthy();
  });
});
