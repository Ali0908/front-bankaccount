import { TestBed } from '@angular/core/testing';
import { AccountContextService } from './account-context.service';
import { Account } from '../../static/models/account';

describe('AccountContextService', () => {
  let service: AccountContextService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AccountContextService],
    });
    service = TestBed.inject(AccountContextService);
  });

  describe('initialization', () => {
    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('should initialize currentAccount$ with null', (done) => {
      service.currentAccount$.subscribe((account) => {
        expect(account).toBeNull();
        done();
      });
    });

    it('should initialize getCurrentAccount() with null', () => {
      expect(service.getCurrentAccount()).toBeNull();
    });
  });

  describe('setCurrentAccount', () => {
    it('should update currentAccount$ when setting a new account', (done) => {
      const mockAccount: Account = {
        number: 'ACC123',
        balance: 1000,
        currency: 'EUR',
      };

      service.setCurrentAccount(mockAccount);

      service.currentAccount$.subscribe((account) => {
        expect(account).toEqual(mockAccount);
        done();
      });
    });

    it('should update getCurrentAccount() when setting a new account', () => {
      const mockAccount: Account = {
        number: 'ACC456',
        balance: 5000,
        currency: 'EUR',
      };

      service.setCurrentAccount(mockAccount);

      expect(service.getCurrentAccount()).toEqual(mockAccount);
    });

    it('should allow setting account to null', (done) => {
      const mockAccount: Account = {
        number: 'ACC789',
        balance: 2000,
        currency: 'EUR',
      };

      service.setCurrentAccount(mockAccount);
      service.setCurrentAccount(null);

      service.currentAccount$.subscribe((account) => {
        expect(account).toBeNull();
        done();
      });
    });

    it('should replace previous account with new one', (done) => {
      const firstAccount: Account = {
        number: 'ACC001',
        balance: 1000,
        currency: 'EUR',
      };

      const secondAccount: Account = {
        number: 'ACC002',
        balance: 2000,
        currency: 'EUR',
      };

      service.setCurrentAccount(firstAccount);
      service.setCurrentAccount(secondAccount);

      service.currentAccount$.subscribe((account) => {
        expect(account).toEqual(secondAccount);
        expect(account?.number).toBe('ACC002');
        done();
      });
    });
  });

  describe('getCurrentAccount', () => {
    it('should return null when no account is set', () => {
      expect(service.getCurrentAccount()).toBeNull();
    });

    it('should return the current account synchronously', () => {
      const mockAccount: Account = {
        number: 'ACC999',
        balance: 7500,
        currency: 'EUR',
      };

      service.setCurrentAccount(mockAccount);

      expect(service.getCurrentAccount()).toEqual(mockAccount);
      expect(service.getCurrentAccount()?.number).toBe('ACC999');
    });

    it('should return the latest account after multiple updates', () => {
      const accounts: Account[] = [
        {
          number: 'ACC111',
          balance: 1000,
          currency: 'EUR',
        },
        {
          number: 'ACC222',
          balance: 2000,
          currency: 'EUR',
        },
        {
          number: 'ACC333',
          balance: 3000,
          currency: 'EUR',
        },
      ];

      accounts.forEach((account) => service.setCurrentAccount(account));

      expect(service.getCurrentAccount()).toEqual(accounts[2]);
    });
  });

  describe('currentAccount$ observable', () => {
    it('should emit multiple values when account is updated', (done) => {
      const emittedValues: (Account | null)[] = [];

      const account1: Account = {
        number: 'ACC101',
        balance: 1500,
        currency: 'EUR',
      };

      const account2: Account = {
        number: 'ACC102',
        balance: 3000,
        currency: 'EUR',
      };

      const subscription = service.currentAccount$.subscribe((account) => {
        emittedValues.push(account);
      });

      service.setCurrentAccount(account1);
      service.setCurrentAccount(account2);
      service.setCurrentAccount(null);

      setTimeout(() => {
        expect(emittedValues.length).toBe(4); // initial null + 3 updates
        expect(emittedValues[0]).toBeNull();
        expect(emittedValues[1]).toEqual(account1);
        expect(emittedValues[2]).toEqual(account2);
        expect(emittedValues[3]).toBeNull();
        subscription.unsubscribe();
        done();
      }, 100);
    });

    it('should allow multiple subscribers to receive updates', (done) => {
      const mockAccount: Account = {
        number: 'ACC555',
        balance: 4000,
        currency: 'EUR',
      };

      let subscriber1Called = false;
      let subscriber2Called = false;

      const sub1 = service.currentAccount$.subscribe((account) => {
        if (account?.number === 'ACC555') {
          subscriber1Called = true;
        }
      });

      const sub2 = service.currentAccount$.subscribe((account) => {
        if (account?.number === 'ACC555') {
          subscriber2Called = true;
        }
      });

      service.setCurrentAccount(mockAccount);

      setTimeout(() => {
        expect(subscriber1Called).toBe(true);
        expect(subscriber2Called).toBe(true);
        sub1.unsubscribe();
        sub2.unsubscribe();
        done();
      }, 100);
    });

    it('should share the same observable instance', () => {
      const observable1 = service.currentAccount$;
      const observable2 = service.currentAccount$;

      expect(observable1).toBe(observable2);
    });
  });

  describe('synchronous vs asynchronous access', () => {
    it('should allow both sync and async access patterns', (done) => {
      const mockAccount: Account = {
        number: 'ACC777',
        balance: 6000,
        currency: 'EUR',
      };

      service.setCurrentAccount(mockAccount);

      // Synchronous access
      const syncAccount = service.getCurrentAccount();
      expect(syncAccount?.number).toBe('ACC777');

      // Asynchronous access
      service.currentAccount$.subscribe((account) => {
        expect(account?.number).toBe('ACC777');
        expect(account).toEqual(syncAccount);
        done();
      });
    });
  });
});
