import { TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorHandlerService } from './error-handler.service';
import { BANK_OPERATIONS_CONSTANTS } from '../../static/constants/bank-operations.constants';

describe('ErrorHandlerService', () => {
  let service: ErrorHandlerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ErrorHandlerService],
    });
    service = TestBed.inject(ErrorHandlerService);
  });

  // ============ INITIALIZATION ============
  it('should create service', () => {
    expect(service).toBeTruthy();
  });

  it('should have ERROR_CODES available', () => {
    expect(BANK_OPERATIONS_CONSTANTS.ERROR_CODES).toBeDefined();
  });

  // ============ WITHDRAWAL - ERROR CODE TESTS ============
  it('should return INSUFFICIENT_BALANCE on matching error code', () => {
    const error = createHttpError(
      BANK_OPERATIONS_CONSTANTS.ERROR_CODES.INSUFFICIENT_BALANCE,
      'Not enough funds',
      400
    );
    const result = service.getWithdrawalErrorMessage(error);
    expect(result).toBe(BANK_OPERATIONS_CONSTANTS.WITHDRAWAL.ERRORS.INSUFFICIENT_BALANCE);
  });

  it('should return ACCOUNT_NOT_FOUND on matching error code', () => {
    const error = createHttpError(
      BANK_OPERATIONS_CONSTANTS.ERROR_CODES.ACCOUNT_NOT_FOUND,
      'Account does not exist',
      404
    );
    const result = service.getWithdrawalErrorMessage(error);
    expect(result).toBe(BANK_OPERATIONS_CONSTANTS.WITHDRAWAL.ERRORS.ACCOUNT_NOT_FOUND);
  });

  it('should return GENERIC_ERROR for unknown error code', () => {
    const error = createHttpError('UNKNOWN_CODE', 'Something went wrong', 500);
    const result = service.getWithdrawalErrorMessage(error);
    expect(result).toBe(BANK_OPERATIONS_CONSTANTS.WITHDRAWAL.ERRORS.GENERIC_ERROR);
  });

  // ============ WITHDRAWAL - MESSAGE FALLBACK TESTS ============
  it('should match INSUFFICIENT_BALANCE from message content', () => {
    const error = createHttpError(undefined, 'Insufficient balance for withdrawal', 400);
    const result = service.getWithdrawalErrorMessage(error);
    expect(result).toBe(BANK_OPERATIONS_CONSTANTS.WITHDRAWAL.ERRORS.INSUFFICIENT_BALANCE);
  });

  it('should match ACCOUNT_NOT_FOUND from message content', () => {
    const error = createHttpError(undefined, 'Account not found in database', 404);
    const result = service.getWithdrawalErrorMessage(error);
    expect(result).toBe(BANK_OPERATIONS_CONSTANTS.WITHDRAWAL.ERRORS.ACCOUNT_NOT_FOUND);
  });

  it('should return GENERIC_ERROR when message does not match', () => {
    const error = createHttpError(undefined, 'Unknown error occurred', 500);
    const result = service.getWithdrawalErrorMessage(error);
    expect(result).toBe(BANK_OPERATIONS_CONSTANTS.WITHDRAWAL.ERRORS.GENERIC_ERROR);
  });

  // ============ WITHDRAWAL - NULL/UNDEFINED TESTS ============
  it('should handle null error gracefully', () => {
    const error = new HttpErrorResponse({ error: null, status: 500 });
    const result = service.getWithdrawalErrorMessage(error);
    expect(result).toBe(BANK_OPERATIONS_CONSTANTS.WITHDRAWAL.ERRORS.GENERIC_ERROR);
  });

  it('should handle error with undefined message', () => {
    const error = new HttpErrorResponse({
      error: { code: undefined, message: undefined },
      status: 400,
    });
    const result = service.getWithdrawalErrorMessage(error);
    expect(result).toBe(BANK_OPERATIONS_CONSTANTS.WITHDRAWAL.ERRORS.GENERIC_ERROR);
  });

  it('should handle missing error object', () => {
    const error = new HttpErrorResponse({ status: 500 });
    const result = service.getWithdrawalErrorMessage(error);
    expect(result).toBe(BANK_OPERATIONS_CONSTANTS.WITHDRAWAL.ERRORS.GENERIC_ERROR);
  });

  // ============ WITHDRAWAL - PRIORITY TEST ============
  it('should prioritize error code over message content', () => {
    const error = new HttpErrorResponse({
      error: {
        code: BANK_OPERATIONS_CONSTANTS.ERROR_CODES.INSUFFICIENT_BALANCE,
        message: 'Some other message',
      },
      status: 400,
    });
    const result = service.getWithdrawalErrorMessage(error);
    expect(result).toBe(BANK_OPERATIONS_CONSTANTS.WITHDRAWAL.ERRORS.INSUFFICIENT_BALANCE);
  });

  // ============ DEPOSIT - ERROR CODE TESTS ============
  it('should return GENERIC_ERROR for deposit with ACCOUNT_NOT_FOUND', () => {
    const error = createHttpError(
      BANK_OPERATIONS_CONSTANTS.ERROR_CODES.ACCOUNT_NOT_FOUND,
      'Account does not exist',
      404
    );
    const result = service.getDepositErrorMessage(error);
    expect(result).toBe(BANK_OPERATIONS_CONSTANTS.DEPOSIT.ERRORS.GENERIC_ERROR);
  });

  it('should return GENERIC_ERROR for deposit with unknown code', () => {
    const error = createHttpError('UNKNOWN_CODE', 'Something went wrong', 500);
    const result = service.getDepositErrorMessage(error);
    expect(result).toBe(BANK_OPERATIONS_CONSTANTS.DEPOSIT.ERRORS.GENERIC_ERROR);
  });

  // ============ DEPOSIT - NULL/UNDEFINED TESTS ============
  it('should handle null error in deposit', () => {
    const error = new HttpErrorResponse({ error: null, status: 500 });
    const result = service.getDepositErrorMessage(error);
    expect(result).toBe(BANK_OPERATIONS_CONSTANTS.DEPOSIT.ERRORS.GENERIC_ERROR);
  });

  it('should handle missing error object in deposit', () => {
    const error = new HttpErrorResponse({ status: 500 });
    const result = service.getDepositErrorMessage(error);
    expect(result).toBe(BANK_OPERATIONS_CONSTANTS.DEPOSIT.ERRORS.GENERIC_ERROR);
  });

  // ============ DEPOSIT - EDGE CASES ============
  it('should always return GENERIC_ERROR for all deposit errors', () => {
    const testErrors = [
      createHttpError('ANY_CODE', 'Any message', 400),
      createHttpError(undefined, 'Just a message', 500),
      new HttpErrorResponse({ error: null, status: 503 }),
    ];

    testErrors.forEach((error) => {
      const result = service.getDepositErrorMessage(error);
      expect(result).toBe(BANK_OPERATIONS_CONSTANTS.DEPOSIT.ERRORS.GENERIC_ERROR);
    });
  });

  // ============ INTEGRATION TEST ============
  it('should handle withdrawal and deposit errors differently', () => {
    const error = createHttpError(
      BANK_OPERATIONS_CONSTANTS.ERROR_CODES.INSUFFICIENT_BALANCE,
      'Not enough balance',
      400
    );

    const withdrawalError = service.getWithdrawalErrorMessage(error);
    const depositError = service.getDepositErrorMessage(error);

    expect(withdrawalError).toBe(BANK_OPERATIONS_CONSTANTS.WITHDRAWAL.ERRORS.INSUFFICIENT_BALANCE);
    expect(depositError).toBe(BANK_OPERATIONS_CONSTANTS.DEPOSIT.ERRORS.GENERIC_ERROR);
    expect(withdrawalError).not.toBe(depositError);
  });
});

// ============ HELPER FUNCTIONS ============
function createHttpError(
  code: string | undefined,
  message: string,
  status: number
): HttpErrorResponse {
  return new HttpErrorResponse({
    error: { code, message },
    status,
    statusText: getStatusText(status),
  });
}

function getStatusText(status: number): string {
  const statusMap: { [key: number]: string } = {
    400: 'Bad Request',
    404: 'Not Found',
    500: 'Internal Server Error',
    503: 'Service Unavailable',
  };
  return statusMap[status] || 'Unknown Error';
}
