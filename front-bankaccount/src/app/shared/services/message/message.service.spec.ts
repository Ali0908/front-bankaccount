import { TestBed } from '@angular/core/testing';
import { MatSnackBar, MatSnackBarRef } from '@angular/material/snack-bar';
import { MessageService } from './message.service';
import { GENERAL_CONSTANTS } from '../../static/constants/general.constants';

describe('MessageService', () => {
  let service: MessageService;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;
  let mockSnackBarRef: jasmine.SpyObj<MatSnackBarRef<any>>;

  beforeEach(() => {
    // Create spy for MatSnackBar
    snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);
    mockSnackBarRef = jasmine.createSpyObj('MatSnackBarRef', ['dismiss']);
    snackBarSpy.open.and.returnValue(mockSnackBarRef);

    TestBed.configureTestingModule({
      providers: [MessageService, { provide: MatSnackBar, useValue: snackBarSpy }],
    });

    service = TestBed.inject(MessageService);
  });

  describe('initialization', () => {
    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('should have GENERAL_CONSTANTS available', () => {
      expect(GENERAL_CONSTANTS.SNACKBAR).toBeDefined();
    });
  });

  describe('showSuccess', () => {
    it('should call snackBar.open with success message and default options', () => {
      const message = 'Operation successful!';

      service.showSuccess(message);

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        message,
        GENERAL_CONSTANTS.SNACKBAR.ACTIONS.SUCCESS_ICON,
        {
          duration: GENERAL_CONSTANTS.SNACKBAR.DURATION_MS.SUCCESS,
          horizontalPosition: GENERAL_CONSTANTS.SNACKBAR.POSITION.HORIZONTAL,
          verticalPosition: GENERAL_CONSTANTS.SNACKBAR.POSITION.VERTICAL,
          panelClass: GENERAL_CONSTANTS.SNACKBAR.PANEL_CLASS.SUCCESS,
        }
      );
    });

    it('should use custom action when provided', () => {
      const message = 'Success';
      const customAction = 'OK';

      service.showSuccess(message, { action: customAction });

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        message,
        customAction,
        jasmine.objectContaining({
          duration: GENERAL_CONSTANTS.SNACKBAR.DURATION_MS.SUCCESS,
        })
      );
    });

    it('should use custom duration when provided', () => {
      const message = 'Success';
      const customDuration = 5000;

      service.showSuccess(message, { duration: customDuration });

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        message,
        GENERAL_CONSTANTS.SNACKBAR.ACTIONS.SUCCESS_ICON,
        jasmine.objectContaining({
          duration: customDuration,
        })
      );
    });

    it('should use custom horizontalPosition when provided', () => {
      const message = 'Success';
      const customPosition = 'start' as const;

      service.showSuccess(message, { horizontalPosition: customPosition });

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        message,
        GENERAL_CONSTANTS.SNACKBAR.ACTIONS.SUCCESS_ICON,
        jasmine.objectContaining({
          horizontalPosition: customPosition,
        })
      );
    });

    it('should use custom verticalPosition when provided', () => {
      const message = 'Success';
      const customPosition = 'top' as const;

      service.showSuccess(message, { verticalPosition: customPosition });

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        message,
        GENERAL_CONSTANTS.SNACKBAR.ACTIONS.SUCCESS_ICON,
        jasmine.objectContaining({
          verticalPosition: customPosition,
        })
      );
    });

    it('should use custom panelClass when provided as string', () => {
      const message = 'Success';
      const customClass = 'custom-success-class';

      service.showSuccess(message, { panelClass: customClass });

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        message,
        GENERAL_CONSTANTS.SNACKBAR.ACTIONS.SUCCESS_ICON,
        jasmine.objectContaining({
          panelClass: customClass,
        })
      );
    });

    it('should use custom panelClass when provided as array', () => {
      const message = 'Success';
      const customClasses = ['class1', 'class2'];

      service.showSuccess(message, { panelClass: customClasses });

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        message,
        GENERAL_CONSTANTS.SNACKBAR.ACTIONS.SUCCESS_ICON,
        jasmine.objectContaining({
          panelClass: customClasses,
        })
      );
    });

    it('should use all custom options when provided', () => {
      const message = 'Success';
      const options = {
        action: 'DONE',
        duration: 6000,
        horizontalPosition: 'end' as const,
        verticalPosition: 'top' as const,
        panelClass: 'custom-class',
      };

      service.showSuccess(message, options);

      expect(snackBarSpy.open).toHaveBeenCalledWith(message, 'DONE', {
        duration: 6000,
        horizontalPosition: 'end',
        verticalPosition: 'top',
        panelClass: 'custom-class',
      });
    });

    it('should handle empty options object', () => {
      const message = 'Success';

      service.showSuccess(message, {});

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        message,
        GENERAL_CONSTANTS.SNACKBAR.ACTIONS.SUCCESS_ICON,
        jasmine.objectContaining({
          duration: GENERAL_CONSTANTS.SNACKBAR.DURATION_MS.SUCCESS,
        })
      );
    });

    it('should be called only once per invocation', () => {
      service.showSuccess('Test');

      expect(snackBarSpy.open).toHaveBeenCalledTimes(1);
    });
  });

  describe('showError', () => {
    it('should call snackBar.open with error message and default options', () => {
      const message = 'An error occurred!';

      service.showError(message);

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        message,
        GENERAL_CONSTANTS.SNACKBAR.ACTIONS.ERROR_ICON,
        {
          duration: GENERAL_CONSTANTS.SNACKBAR.DURATION_MS.ERROR,
          horizontalPosition: GENERAL_CONSTANTS.SNACKBAR.POSITION.HORIZONTAL,
          verticalPosition: GENERAL_CONSTANTS.SNACKBAR.POSITION.VERTICAL,
          panelClass: GENERAL_CONSTANTS.SNACKBAR.PANEL_CLASS.ERROR,
        }
      );
    });

    it('should use custom action when provided', () => {
      const message = 'Error';
      const customAction = 'CLOSE';

      service.showError(message, { action: customAction });

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        message,
        customAction,
        jasmine.objectContaining({
          duration: GENERAL_CONSTANTS.SNACKBAR.DURATION_MS.ERROR,
        })
      );
    });

    it('should use custom duration when provided', () => {
      const message = 'Error';
      const customDuration = 8000;

      service.showError(message, { duration: customDuration });

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        message,
        GENERAL_CONSTANTS.SNACKBAR.ACTIONS.ERROR_ICON,
        jasmine.objectContaining({
          duration: customDuration,
        })
      );
    });

    it('should use custom horizontalPosition when provided', () => {
      const message = 'Error';
      const customPosition = 'end' as const;

      service.showError(message, { horizontalPosition: customPosition });

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        message,
        GENERAL_CONSTANTS.SNACKBAR.ACTIONS.ERROR_ICON,
        jasmine.objectContaining({
          horizontalPosition: customPosition,
        })
      );
    });

    it('should use custom verticalPosition when provided', () => {
      const message = 'Error';
      const customPosition = 'bottom' as const;

      service.showError(message, { verticalPosition: customPosition });

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        message,
        GENERAL_CONSTANTS.SNACKBAR.ACTIONS.ERROR_ICON,
        jasmine.objectContaining({
          verticalPosition: customPosition,
        })
      );
    });

    it('should use custom panelClass when provided as string', () => {
      const message = 'Error';
      const customClass = 'custom-error-class';

      service.showError(message, { panelClass: customClass });

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        message,
        GENERAL_CONSTANTS.SNACKBAR.ACTIONS.ERROR_ICON,
        jasmine.objectContaining({
          panelClass: customClass,
        })
      );
    });

    it('should use custom panelClass when provided as array', () => {
      const message = 'Error';
      const customClasses = ['error-class1', 'error-class2'];

      service.showError(message, { panelClass: customClasses });

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        message,
        GENERAL_CONSTANTS.SNACKBAR.ACTIONS.ERROR_ICON,
        jasmine.objectContaining({
          panelClass: customClasses,
        })
      );
    });

    it('should use all custom options when provided', () => {
      const message = 'Error';
      const options = {
        action: 'DISMISS',
        duration: 10000,
        horizontalPosition: 'start' as const,
        verticalPosition: 'bottom' as const,
        panelClass: ['error-class'],
      };

      service.showError(message, options);

      expect(snackBarSpy.open).toHaveBeenCalledWith(message, 'DISMISS', {
        duration: 10000,
        horizontalPosition: 'start',
        verticalPosition: 'bottom',
        panelClass: ['error-class'],
      });
    });

    it('should handle empty options object', () => {
      const message = 'Error';

      service.showError(message, {});

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        message,
        GENERAL_CONSTANTS.SNACKBAR.ACTIONS.ERROR_ICON,
        jasmine.objectContaining({
          duration: GENERAL_CONSTANTS.SNACKBAR.DURATION_MS.ERROR,
        })
      );
    });

    it('should be called only once per invocation', () => {
      service.showError('Test error');

      expect(snackBarSpy.open).toHaveBeenCalledTimes(1);
    });
  });

  describe('showSuccess vs showError comparison', () => {
    it('should use different defaults for success and error', () => {
      service.showSuccess('Success message');
      service.showError('Error message');

      expect(snackBarSpy.open).toHaveBeenCalledTimes(2);

      const successCall = snackBarSpy.open.calls.argsFor(0);
      const errorCall = snackBarSpy.open.calls.argsFor(1);

      // Différent action par défaut
      expect(successCall[1]).toBe(GENERAL_CONSTANTS.SNACKBAR.ACTIONS.SUCCESS_ICON);
      expect(errorCall[1]).toBe(GENERAL_CONSTANTS.SNACKBAR.ACTIONS.ERROR_ICON);

      // Différente durée par défaut
      expect((successCall[2] as any).duration).toBe(GENERAL_CONSTANTS.SNACKBAR.DURATION_MS.SUCCESS);
      expect((errorCall[2] as any).duration).toBe(GENERAL_CONSTANTS.SNACKBAR.DURATION_MS.ERROR);

      // Différente classe de panel par défaut
      expect((successCall[2] as any).panelClass).toBe(
        GENERAL_CONSTANTS.SNACKBAR.PANEL_CLASS.SUCCESS
      );
      expect((errorCall[2] as any).panelClass).toBe(GENERAL_CONSTANTS.SNACKBAR.PANEL_CLASS.ERROR);
    });
  });

  describe('edge cases', () => {
    it('should handle empty string message for success', () => {
      service.showSuccess('');

      expect(snackBarSpy.open).toHaveBeenCalledWith('', jasmine.any(String), jasmine.any(Object));
    });

    it('should handle empty string message for error', () => {
      service.showError('');

      expect(snackBarSpy.open).toHaveBeenCalledWith('', jasmine.any(String), jasmine.any(Object));
    });

    it('should handle very long message for success', () => {
      const longMessage = 'A'.repeat(1000);

      service.showSuccess(longMessage);

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        longMessage,
        jasmine.any(String),
        jasmine.any(Object)
      );
    });

    it('should handle zero duration', () => {
      service.showSuccess('Test', { duration: 0 });

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        'Test',
        jasmine.any(String),
        jasmine.objectContaining({ duration: 0 })
      );
    });

    it('should handle negative duration (edge case)', () => {
      service.showError('Test', { duration: -1 });

      expect(snackBarSpy.open).toHaveBeenCalledWith(
        'Test',
        jasmine.any(String),
        jasmine.objectContaining({ duration: -1 })
      );
    });
  });
});
