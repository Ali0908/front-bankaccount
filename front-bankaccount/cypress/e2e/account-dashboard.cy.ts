type ApiAccount = {
  accountNumber: string;
  balance: number;
  overdraftLimit: number;
  savingsBalance: number;
  savingsDepositLimit: number;
};

const createAccount = (): ApiAccount => ({
  accountNumber: 'ACC001',
  balance: 1000,
  overdraftLimit: 0,
  savingsBalance: 0,
  savingsDepositLimit: 22950,
});

const normalizeAmount = (raw: string): number => {
  const numeric = raw.replaceAll(/[^0-9,.-]/g, '').replaceAll(',', '.');
  return Number.parseFloat(numeric);
};

describe('Account dashboard flows', () => {
  const stubAccountLoad = (account: ApiAccount) => {
    cy.intercept('GET', '**/bank-accounts', (req) => {
      req.reply([account]);
    }).as('getAccounts');
  };

  it('performs a cash deposit on the current account', () => {
    const account = createAccount();
    stubAccountLoad(account);

    cy.intercept('POST', '**/cash-deposit', (req) => {
      account.balance += req.body.amount;
      req.reply({
        accountNumber: account.accountNumber,
        balance: account.balance,
        overdraftLimit: account.overdraftLimit,
        savingsBalance: account.savingsBalance,
        savingsDepositLimit: account.savingsDepositLimit,
      });
    }).as('deposit');

    cy.visit('/');
    cy.wait('@getAccounts');

    cy.contains('button.operation-item', "Dépôt d'argent").click();

    cy.get('mat-dialog-container', { timeout: 8000 })
      .should('be.visible')
      .within(() => {
        cy.get('input[type="number"]').clear().type('200');
        cy.contains('button', 'Déposer').click();
      });

    cy.wait('@deposit').its('request.body').should('deep.include', {
      accountNumber: 'ACC001',
      amount: 200,
    });

    cy.get('.balance-box .info-value')
      .first()
      .invoke('text')
      .then((text) => {
        expect(normalizeAmount(text)).to.eq(1200);
      });
  });

  it('performs a cash withdrawal', () => {
    const account = createAccount();
    stubAccountLoad(account);

    cy.intercept('POST', '**/cash-withdrawal', (req) => {
      account.balance -= req.body.amount;
      req.reply({
        accountNumber: account.accountNumber,
        balance: account.balance,
        overdraftLimit: account.overdraftLimit,
        savingsBalance: account.savingsBalance,
        savingsDepositLimit: account.savingsDepositLimit,
      });
    }).as('withdraw');

    cy.visit('/');
    cy.wait('@getAccounts');

    cy.contains('button.operation-item', "Retrait d'argent").click();

    cy.get('mat-dialog-container', { timeout: 8000 })
      .should('be.visible')
      .within(() => {
        cy.get('input[type="number"]').clear().type('300');
        cy.contains('button', 'Retirer').click();
      });

    cy.wait('@withdraw').its('request.body').should('deep.include', {
      accountNumber: 'ACC001',
      amount: 300,
    });

    // Wait for the reload after withdrawal
    cy.wait('@getAccounts');

    cy.get('.balance-box .info-value')
      .first()
      .invoke('text')
      .then((text) => {
        expect(normalizeAmount(text)).to.eq(700);
      });
  });

  it('enables overdraft from the dashboard toggle', () => {
    const account = createAccount();
    stubAccountLoad(account);

    cy.intercept('POST', '**/overdraft', (req) => {
      account.overdraftLimit = req.body.overdraftLimit;
      req.reply({ ...account });
    }).as('overdraft');

    cy.visit('/');
    cy.wait('@getAccounts');

    cy.get('mat-slide-toggle').should('exist').click();

    cy.wait('@overdraft').its('request.body').should('deep.equal', {
      accountNumber: 'ACC001',
      overdraftLimit: 300,
    });

    cy.get('mat-slide-toggle').should('have.class', 'mat-mdc-slide-toggle-checked');
  });

  it('deposits money into the savings account', () => {
    const account = createAccount();
    stubAccountLoad(account);

    cy.intercept('POST', '**/savings-deposit', (req) => {
      account.savingsBalance += req.body.amount;
      req.reply({
        accountNumber: account.accountNumber,
        balance: account.balance,
        overdraftLimit: account.overdraftLimit,
        savingsBalance: account.savingsBalance,
        savingsDepositLimit: account.savingsDepositLimit,
      });
    }).as('savingsDeposit');

    cy.visit('/');
    cy.wait('@getAccounts');

    cy.contains('button.operation-item', "Dépôt d'argent").click();

    cy.get('mat-dialog-container', { timeout: 8000 })
      .should('be.visible')
      .within(() => {
        cy.contains('mat-radio-button', "Livret d'épargne").click();
        cy.get('input[type="number"]').clear().type('500');
        cy.contains('button', 'Déposer').click();
      });

    cy.wait('@savingsDeposit').its('request.body').should('deep.include', {
      accountNumber: 'ACC001',
      amount: 500,
    });

    cy.get('.info-box.balance-box .info-value')
      .eq(1)
      .invoke('text')
      .then((text) => {
        expect(normalizeAmount(text)).to.eq(500);
      });
  });

  it('shows the statement with recent transactions', () => {
    const account = createAccount();
    stubAccountLoad(account);

    cy.intercept('GET', '**/bank-accounts/statement/ACC001', {
      accountNumber: 'ACC001',
      accountType: 'Compte courant',
      currentBalance: 1200,
      savingsBalance: 0,
      statementDate: '2024-01-01T10:00:00Z',
      transactions: [
        {
          date: '2024-01-01T09:00:00Z',
          type: 'Dépôt sur compte courant',
          amount: 200,
          balanceAfter: 1200,
        },
        {
          date: '2023-12-30T12:00:00Z',
          type: 'Retrait',
          amount: -50,
          balanceAfter: 1000,
        },
      ],
    }).as('statement');

    cy.visit('/');
    cy.wait('@getAccounts');

    cy.contains('button.operation-item', 'Relevé de Compte').click();
    cy.wait('@statement');

    cy.get('.statement-section').should('be.visible');
    cy.get('.transactions-table tbody tr').should('have.length', 2);
    cy.get('.transactions-table tbody tr')
      .first()
      .within(() => {
        cy.contains('Dépôt sur compte courant');
        cy.contains('200');
      });
  });
});
