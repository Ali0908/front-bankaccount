export const BANK_OPERATIONS_CONSTANTS = {
  ERROR_CODES: {
    INSUFFICIENT_BALANCE: 'INSUFFICIENT_BALANCE',
    ACCOUNT_NOT_FOUND: 'ACCOUNT_NOT_FOUND',
    INTERNAL_ERROR: 'INTERNAL_ERROR'
  },
  WITHDRAWAL: {
    TITLE: 'Retrait d\'argent',
    SUCCESS_MESSAGE: 'Retrait effectué avec succès',
    ERRORS: {
      INSUFFICIENT_BALANCE: 'Solde insuffisant pour effectuer ce retrait',
      ACCOUNT_NOT_FOUND: 'Compte bancaire introuvable',
      INVALID_AMOUNT: 'Montant invalide',
      GENERIC_ERROR: 'Une erreur est survenue lors du retrait'
    }
  },
  DEPOSIT: {
    TITLE: 'Dépôt d\'argent',
    SUCCESS_MESSAGE: 'Dépôt effectué avec succès',
    ERRORS: {
      GENERIC_ERROR: 'Une erreur est survenue lors du dépôt'
    }
  }
};
