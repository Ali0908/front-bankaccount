export const GENERAL_CONSTANTS = {
  CURRENCY: {
    SYMBOL: '€',
    MIN_TRANSACTION_AMOUNT: 0.01,
    DECIMAL_PLACES: 2,
    EURO_CODE: 'EUR',
  },
  VALIDATION: {
    REQUIRED_FIELD: 'Ce champ est requis',
    INVALID_AMOUNT: 'Montant invalide',
    MIN_AMOUNT: 'Le montant doit être supérieur à {min} €',
  },
  BUTTONS: {
    CANCEL: 'Annuler',
    CONFIRM: 'Confirmer',
    CLOSE: 'Fermer',
    SUBMIT: 'Valider',
  },
  LABELS: {
    ACCOUNT_NUMBER: 'Numéro de compte',
    ACCOUNT_BALANCE: 'Compte Courant',
  },
  MESSAGES: {
    SUCCESS: 'Opération réussie',
    ERROR: 'Une erreur est survenue',
    LOADING: 'Chargement en cours...',
    LOAD_ACCOUNT_ERROR: 'Erreur lors du chargement du compte',
    SELECT_ACCOUNT_ERROR: 'Aucun compte sélectionné',
    LOAD_STATEMENT_ERROR: 'Impossible de charger le relevé de compte',
  },
  SNACKBAR: {
    ACTIONS: {
      SUCCESS_ICON: '✓',
      ERROR_ICON: '✕',
    },
    DURATION_MS: {
      SUCCESS: 3000,
      ERROR: 5000,
    },
    POSITION: {
      HORIZONTAL: 'end' as const,
      VERTICAL: 'top' as const,
    },
    PANEL_CLASS: {
      SUCCESS: ['success-snackbar'],
      ERROR: ['error-snackbar'],
    },
  },
};
