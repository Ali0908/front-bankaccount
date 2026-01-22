import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { of, throwError } from 'rxjs';

import { CashWithdrawal } from './cash-withdrawal';
import { BankAccountService } from '../../shared/services/bank-account/bank-account.service';
import { MessageService } from '../../shared/services/message/message.service';
import { ErrorHandlerService } from '../../shared/services/error-handler/error-handler.service';
import { AccountContextService } from '../../shared/services/account/account-context.service';

describe('CashWithdrawal', () => {
  let component: CashWithdrawal;
  let fixture: ComponentFixture<CashWithdrawal>;
  let mockDialog: jasmine.SpyObj<MatDialog>;
  let mockBankAccountService: jasmine.SpyObj<BankAccountService>;
  let mockMessageService: jasmine.SpyObj<MessageService>;
  let mockErrorHandler: jasmine.SpyObj<ErrorHandlerService>;
  let mockRouter: { navigate: jasmine.Spy };
  let mockAccountContextService: jasmine.SpyObj<AccountContextService>;

  beforeEach(async () => {
    mockDialog = jasmine.createSpyObj('MatDialog', ['open']);
    mockDialog.open.and.returnValue({ afterClosed: () => of(undefined) } as any);

    mockBankAccountService = jasmine.createSpyObj('BankAccountService', ['withdraw']);
    mockBankAccountService.withdraw.and.returnValue(
      of({
        id: 1,
        accountNumber: 'ACC001',
        balance: 900,
        overdraftLimit: 300,
        savingsBalance: 0,
        savingsDepositLimit: 22950,
      })
    );

    mockMessageService = jasmine.createSpyObj('MessageService', ['showSuccess', 'showError']);
    mockErrorHandler = jasmine.createSpyObj('ErrorHandlerService', ['getWithdrawalErrorMessage']);
    mockErrorHandler.getWithdrawalErrorMessage.and.returnValue('Erreur de retrait');

    mockRouter = { navigate: jasmine.createSpy('navigate') };
    mockAccountContextService = jasmine.createSpyObj('AccountContextService', [
      'getCurrentAccount',
    ]);
    mockAccountContextService.getCurrentAccount.and.returnValue({ number: 'ACC001' } as any);

    await TestBed.configureTestingModule({
      imports: [CashWithdrawal],
      providers: [
        { provide: MatDialog, useValue: mockDialog },
        { provide: BankAccountService, useValue: mockBankAccountService },
        { provide: MessageService, useValue: mockMessageService },
        { provide: ErrorHandlerService, useValue: mockErrorHandler },
        { provide: Router, useValue: mockRouter },
        { provide: AccountContextService, useValue: mockAccountContextService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CashWithdrawal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call withdraw when modal returns data', () => {
    mockDialog.open.and.returnValue({
      afterClosed: () => of({ accountNumber: 'ACC001', amount: 100 }),
    } as any);
    component.openWithdrawalModal();
    expect(mockBankAccountService.withdraw).toHaveBeenCalledWith('ACC001', 100);
  });

  it('should handle withdraw errors using error handler', () => {
    mockDialog.open.and.returnValue({
      afterClosed: () => of({ accountNumber: 'ACC001', amount: 100 }),
    } as any);
    mockBankAccountService.withdraw.and.returnValue(throwError(() => ({ status: 400 })));
    component.openWithdrawalModal();
    expect(mockErrorHandler.getWithdrawalErrorMessage).toHaveBeenCalled();
    expect(mockMessageService.showError).toHaveBeenCalled();
  });
});
