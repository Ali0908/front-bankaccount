import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

import { TransactionModal } from './transaction-modal';
import { TransactionDialogData } from '../../static/models/transaction-dialog-data.model';

describe('TransactionModal', () => {
  let component: TransactionModal;
  let fixture: ComponentFixture<TransactionModal>;
  let mockDialogRef: jasmine.SpyObj<MatDialogRef<TransactionModal>>;

  const mockDialogData: TransactionDialogData = {
    type: 'deposit',
    accountNumber: 'ACC001'
  };

  beforeEach(async () => {
    mockDialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);

    await TestBed.configureTestingModule({
      imports: [
        TransactionModal,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: mockDialogRef },
        { provide: MAT_DIALOG_DATA, useValue: mockDialogData },
        provideAnimationsAsync('noop')
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TransactionModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Form initialization', () => {
    it('should initialize form with amount field', () => {
      expect(component.form.get('amount')).toBeTruthy();
    });

    it('should have amount field as required', () => {
      const amountControl = component.form.get('amount');
      amountControl?.setValue('');
      expect(amountControl?.hasError('required')).toBeTruthy();
    });

    it('should validate minimum amount (0.01)', () => {
      const amountControl = component.form.get('amount');
      amountControl?.setValue(0);
      expect(amountControl?.hasError('min')).toBeTruthy();

      amountControl?.setValue(0.01);
      expect(amountControl?.valid).toBeTruthy();
    });

    it('should accept valid amount', () => {
      const amountControl = component.form.get('amount');
      amountControl?.setValue(100.50);
      expect(amountControl?.valid).toBeTruthy();
    });
  });

  describe('getTitle', () => {
    it('should return "Dépôt d\'argent" for deposit type', () => {
      component.data = { type: 'deposit', accountNumber: 'ACC001' };
      expect(component.getTitle()).toBe('Dépôt d\'argent');
    });

    it('should return "Retrait d\'argent" for withdrawal type', () => {
      component.data = { type: 'withdrawal', accountNumber: 'ACC001' };
      expect(component.getTitle()).toBe('Retrait d\'argent');
    });
  });

  describe('getButtonLabel', () => {
    it('should return "Déposer" for deposit type', () => {
      component.data = { type: 'deposit', accountNumber: 'ACC001' };
      expect(component.getButtonLabel()).toBe('Déposer');
    });

    it('should return "Retirer" for withdrawal type', () => {
      component.data = { type: 'withdrawal', accountNumber: 'ACC001' };
      expect(component.getButtonLabel()).toBe('Retirer');
    });
  });

  describe('submit', () => {
    it('should close dialog with correct data when form is valid', () => {
      component.form.patchValue({ amount: 500 });
      component.submit();

      expect(mockDialogRef.close).toHaveBeenCalledWith({
        type: 'deposit',
        accountNumber: 'ACC001',
        amount: 500
      });
    });

    it('should not close dialog when form is invalid', () => {
      component.form.patchValue({ amount: '' });
      component.submit();

      expect(mockDialogRef.close).not.toHaveBeenCalled();
    });

    it('should not close dialog when amount is below minimum', () => {
      component.form.patchValue({ amount: -10 });
      component.submit();

      expect(mockDialogRef.close).not.toHaveBeenCalled();
    });
  });

  describe('cancel', () => {
    it('should close dialog without data', () => {
      component.cancel();
      expect(mockDialogRef.close).toHaveBeenCalledWith();
    });
  });
});
