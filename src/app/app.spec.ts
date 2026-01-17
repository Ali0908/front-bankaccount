import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';

import { App } from './app';
import { BankAccountService } from './shared/services/bank-account/bank-account.service';
import { MessageService } from './shared/services/message/message.service';
import { AccountContextService } from './shared/services/account/account-context.service';
import { BankErrorHandlerService } from './shared/services/error-handler/bank-error-handler.service';

describe('App', () => {
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
      imports: [App],
      providers: [
        { provide: MatDialog, useValue: mockDialog },
        { provide: BankAccountService, useValue: mockBankAccountService },
        { provide: MessageService, useValue: mockMessageService },
        { provide: AccountContextService, useValue: mockAccountContextService },
        { provide: BankErrorHandlerService, useValue: mockErrorHandler },
        provideRouter([])
      ]
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
    expect(compiled.querySelector('app-account-dashboard')).toBeTruthy();
  });
});
