import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { OverdraftService } from './overdraft.service';
import { environment } from '../../../../environments/environment.development';
import { Paths } from '../../static/path';

describe('OverdraftService', () => {
  let service: OverdraftService;
  let httpMock: HttpTestingController;
  const apiUrl = environment.apiUrl + Paths.PATH_BANK_ACCOUNT;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), OverdraftService],
    });
    service = TestBed.inject(OverdraftService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('setOverdraftLimit', () => {
    it('should set overdraft limit successfully', (done) => {
      const accountNumber = 'ACC001';
      const overdraftLimit = 300;
      const mockResponse = {
        id: 1,
        accountNumber,
        balance: 1000,
        overdraftLimit: 300,
      };

      service.setOverdraftLimit(accountNumber, overdraftLimit).subscribe({
        next: (response) => {
          expect(response.overdraftLimit).toBe(300);
          done();
        },
        error: () => fail('should have succeeded'),
      });

      const req = httpMock.expectOne(`${apiUrl}/overdraft`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({
        accountNumber,
        overdraftLimit,
      });
      req.flush(mockResponse);
    });

    it('should handle error when setting overdraft limit', (done) => {
      const accountNumber = 'ACC001';
      const overdraftLimit = 500; // Exceeds max 300

      service.setOverdraftLimit(accountNumber, overdraftLimit).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(400);
          done();
        },
      });

      const req = httpMock.expectOne(`${apiUrl}/overdraft`);
      req.flush('Overdraft limit must be between 0 and 300', {
        status: 400,
        statusText: 'Bad Request',
      });
    });
  });
});
