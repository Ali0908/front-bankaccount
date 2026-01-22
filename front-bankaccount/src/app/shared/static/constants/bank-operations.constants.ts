export const BANK_OPERATIONS_CONSTANTS = {
  ERROR_CODES: {
    INSUFFICIENT_BALANCE: 'INSUFFICIENT_BALANCE',
    ACCOUNT_NOT_FOUND: 'ACCOUNT_NOT_FOUND',
    INTERNAL_ERROR: 'INTERNAL_ERROR',
  },
  WITHDRAWAL: {
    TITLE: "Retrait d'argent",
    SUCCESS_MESSAGE: 'Retrait effectué avec succès',
    ERRORS: {
      INSUFFICIENT_BALANCE: 'Solde insuffisant pour effectuer ce retrait',
      ACCOUNT_NOT_FOUND: 'Compte bancaire introuvable',
      INVALID_AMOUNT: 'Montant invalide',
      GENERIC_ERROR: 'Une erreur est survenue lors du retrait',
    },
  },
  DEPOSIT: {
    TITLE: "Dépôt d'argent",
    SUCCESS_MESSAGE: 'Dépôt effectué avec succès',
    ERRORS: {
      GENERIC_ERROR: 'Une erreur est survenue lors du dépôt',
    },
  },
  OVERDRAFT: {
    LABEL: 'Découvert autorisé',
    MAX_LIMIT: 'Max: 300€',
    MESSAGES: {
      ACCOUNT_NUMBER_NOT_FOUND: 'Account number not found',
      ENABLED: 'Découvert activé (max 300€)',
      DISABLED: 'Découvert désactivé',
      UPDATE_ERROR: 'Erreur lors de la modification du découvert',
    },
  },
  SAVINGS_ACCOUNT: {
    LABEL: "Livret d'épargne",
    DEPOSIT_LIMIT: 'Plafond: 22 950€',
    ERRORS: {
      GENERIC_ERROR: 'Une erreur est survenue lors du dépôt sur le livret',
    },
  },
  ACCOUNT_TYPES: {
    CURRENT: 'Compte courant',
    SAVINGS: "Livret d'épargne",
  },
};
