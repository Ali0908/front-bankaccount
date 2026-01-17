import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { of, throwError } from 'rxjs';

import { CashDeposit } from './cash-deposit';
import { BankAccountService } from '../../shared/services/bank-account/bank-account.service';
import { MessageService } from '../../shared/services/message/message.service';
import { AccountContextService } from '../../shared/services/account/account-context.service';
import { TransactionModal } from '../../shared/components/transaction-modal/transaction-modal';
import { TransactionResult } from '../../shared/static/models/transaction-result.model';
import { provideRouter } from '@angular/router';

describe('CashDeposit', () => {
  let component: CashDeposit;
  let fixture: ComponentFixture<CashDeposit>;
  let mockDialog: jasmine.SpyObj<MatDialog>;
  let mockDialogRef: jasmine.SpyObj<MatDialogRef<TransactionModal>>;
  let mockBankAccountService: jasmine.SpyObj<BankAccountService>;
  let mockMessageService: jasmine.SpyObj<MessageService>;
  let mockAccountContextService: jasmine.SpyObj<AccountContextService>;

  const mockBankAccount = {
    id: 1,
    accountNumber: 'ACC001',
    balance: 1500
  };

  const mockCurrentAccount = {
    number: 'ACC001',
    balance: 1000,
    currency: 'EUR'
  };

  const mockDepositResult: TransactionResult = {
    type: 'deposit',
    accountNumber: 'ACC001',
    amount: 500
  };

  beforeEach(async () => {
    mockDialog = jasmine.createSpyObj('MatDialog', ['open']);
    mockDialogRef = jasmine.createSpyObj('MatDialogRef', [], {
      afterClosed: () => of(mockDepositResult)
    });
    mockBankAccountService = jasmine.createSpyObj('BankAccountService', [
      'deposit',
      'getAllBankAccounts'
    ]);
    mockMessageService = jasmine.createSpyObj('MessageService', ['showSuccess', 'showError']);
    mockAccountContextService = jasmine.createSpyObj('AccountContextService', ['getCurrentAccount']);

    mockDialog.open.and.returnValue(mockDialogRef);
    mockBankAccountService.deposit.and.returnValue(of(mockBankAccount));
    mockAccountContextService.getCurrentAccount.and.returnValue(mockCurrentAccount);

    await TestBed.configureTestingModule({
      imports: [CashDeposit],
      providers: [
        { provide: MatDialog, useValue: mockDialog },
        { provide: BankAccountService, useValue: mockBankAccountService },
        { provide: MessageService, useValue: mockMessageService },
        { provide: AccountContextService, useValue: mockAccountContextService },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CashDeposit);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('openDepositModal', () => {
    it('should open transaction modal with deposit type', () => {
      component.openDepositModal();

      expect(mockAccountContextService.getCurrentAccount).toHaveBeenCalled();
      expect(mockDialog.open).toHaveBeenCalledWith(TransactionModal, {
        data: { type: 'deposit', accountNumber: 'ACC001' }
      });
    });

    it('should call deposit service when modal returns valid result', (done) => {
      component.openDepositModal();

      setTimeout(() => {
        expect(mockBankAccountService.deposit).toHaveBeenCalledWith('ACC001', 500);
        done();
      }, 100);
    });

    it('should not call deposit service when modal is cancelled', (done) => {
      const cancelledDialogRef = jasmine.createSpyObj('MatDialogRef', [], {
        afterClosed: () => of(undefined)
      });
      mockDialog.open.and.returnValue(cancelledDialogRef);

      component.openDepositModal();

      setTimeout(() => {
        expect(mockBankAccountService.deposit).not.toHaveBeenCalled();
        done();
      }, 100);
    });

    it('should handle deposit service error', (done) => {
      const error = { status: 400, message: 'Insufficient balance' };
      mockBankAccountService.deposit.and.returnValue(throwError(() => error));

      component.openDepositModal();

      setTimeout(() => {
        expect(mockMessageService.showError).toHaveBeenCalledWith(
          jasmine.any(String),
          jasmine.objectContaining({
            action: '✕',
            duration: 5000
          })
        );
        done();
      }, 100);
    });

    it('should display success message on successful deposit', (done) => {
      component.openDepositModal();

      setTimeout(() => {
        expect(mockMessageService.showSuccess).toHaveBeenCalledWith(
          'Dépôt effectué avec succès',
          jasmine.objectContaining({
            action: '✓',
            duration: 3000
          })
        );
        done();
      }, 100);
    });
  });
});
