import { GENERAL_CONSTANTS } from './general.constants';

export const TRANSACTION_TYPES = {
  DEPOSIT: 'deposit' as const,
  WITHDRAWAL: 'withdrawal' as const
};

export const TRANSACTION_MODAL_CONSTANTS = {
  FORM_FIELDS: {
    AMOUNT: 'amount'
  },
  FORM_FIELD: {
    LABEL: `Montant (${GENERAL_CONSTANTS.CURRENCY.SYMBOL})`,
    PLACEHOLDER: '0.00',
    MIN_VALUE: GENERAL_CONSTANTS.CURRENCY.MIN_TRANSACTION_AMOUNT
  },
  VALIDATION_ERRORS: {
    AMOUNT_REQUIRED: GENERAL_CONSTANTS.VALIDATION.REQUIRED_FIELD,
    AMOUNT_MIN: `Le montant doit être supérieur à ${GENERAL_CONSTANTS.CURRENCY.MIN_TRANSACTION_AMOUNT} ${GENERAL_CONSTANTS.CURRENCY.SYMBOL}`
  },
  TITLES: {
    [TRANSACTION_TYPES.DEPOSIT]: 'Dépôt d\'argent',
    [TRANSACTION_TYPES.WITHDRAWAL]: 'Retrait d\'argent'
  },
  BUTTONS: {
    [TRANSACTION_TYPES.DEPOSIT]: 'Déposer',
    [TRANSACTION_TYPES.WITHDRAWAL]: 'Retirer',
    CANCEL: GENERAL_CONSTANTS.BUTTONS.CANCEL
  },
  FEEDBACK: {
    SUCCESS: {
      [TRANSACTION_TYPES.DEPOSIT]: 'Dépôt effectué avec succès',
      [TRANSACTION_TYPES.WITHDRAWAL]: 'Retrait effectué avec succès'
    },
    ERROR: {
      [TRANSACTION_TYPES.DEPOSIT]: 'Erreur lors du dépôt',
      [TRANSACTION_TYPES.WITHDRAWAL]: 'Erreur lors du retrait'
    },
    ACTIONS: {
      SUCCESS_ICON: '✓',
      ERROR_ICON: '✕'
    },
    DURATION_MS: {
      SUCCESS: 3000,
      ERROR: 5000
    }
  }
};
