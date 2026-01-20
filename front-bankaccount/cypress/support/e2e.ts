// Runs before every e2e spec. Extend if custom commands are needed.
import './commands';

// Prevent failing tests on unexpected app errors during UI stubbing.
Cypress.on('uncaught:exception', () => false);
