import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { BANK_OPERATIONS_CONSTANTS } from '../../static/constants/bank-operations.constants';

@Injectable({ providedIn: 'root' })
export class ErrorHandlerService {
  private readonly ERROR_CODES = BANK_OPERATIONS_CONSTANTS.ERROR_CODES;

  getWithdrawalErrorMessage(error: HttpErrorResponse): string {
    const errorCode = error?.error?.code;
    const errorMessage = error?.error?.message || '';

    // Vérifier le code d'erreur structuré du backend
    if (errorCode === this.ERROR_CODES.INSUFFICIENT_BALANCE) {
      return BANK_OPERATIONS_CONSTANTS.WITHDRAWAL.ERRORS.INSUFFICIENT_BALANCE;
    }
    if (errorCode === this.ERROR_CODES.ACCOUNT_NOT_FOUND) {
      return BANK_OPERATIONS_CONSTANTS.WITHDRAWAL.ERRORS.ACCOUNT_NOT_FOUND;
    }

    // Fallback : vérifier le contenu du message
    if (errorMessage.includes('Insufficient balance')) {
      return BANK_OPERATIONS_CONSTANTS.WITHDRAWAL.ERRORS.INSUFFICIENT_BALANCE;
    }
    if (errorMessage.includes('not found')) {
      return BANK_OPERATIONS_CONSTANTS.WITHDRAWAL.ERRORS.ACCOUNT_NOT_FOUND;
    }

    return BANK_OPERATIONS_CONSTANTS.WITHDRAWAL.ERRORS.GENERIC_ERROR;
  }

  getDepositErrorMessage(error: HttpErrorResponse): string {
    const errorCode = error?.error?.code;

    if (errorCode === this.ERROR_CODES.ACCOUNT_NOT_FOUND) {
      return BANK_OPERATIONS_CONSTANTS.DEPOSIT.ERRORS.GENERIC_ERROR;
    }

    return BANK_OPERATIONS_CONSTANTS.DEPOSIT.ERRORS.GENERIC_ERROR;
  }
}
