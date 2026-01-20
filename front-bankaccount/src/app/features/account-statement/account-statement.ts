import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { AccountStatementService } from './../../shared/services/account-statement/account-statement.service';
import { AccountStatement } from './../../shared/static/models/account-statement';

@Component({
  selector: 'app-account-statement',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatCardModule, MatButtonModule, MatIconModule],
  templateUrl: './account-statement.html',
  styleUrl: './account-statement.scss',
})
export class AccountStatementComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly statementService = inject(AccountStatementService);

  statement = signal<AccountStatement | null>(null);
  loading = signal(false);
  error = signal<string | null>(null);
  dataSource = signal<MatTableDataSource<any>>(new MatTableDataSource<any>());

  displayedColumns: string[] = ['date', 'type', 'amount', 'balanceAfter'];

  ngOnInit() {
    console.log('AccountStatementComponent initialized');
    console.log('History state:', history.state);

    const accountNumber = this.getAccountNumberFromRoute();
    console.log('Account number:', accountNumber);

    if (accountNumber) {
      this.loadStatement(accountNumber);
    } else {
      this.error.set('Numéro de compte manquant');
      console.error('No account number found in navigation state');
    }
  }

  private getAccountNumberFromRoute(): string | null {
    // Récupérer depuis l'état du router (history.state)
    return history.state?.['accountNumber'] || null;
  }

  private loadStatement(accountNumber: string) {
    console.log('Loading statement for account:', accountNumber);
    this.loading.set(true);
    this.error.set(null);

    this.statementService.getStatement(accountNumber).subscribe({
      next: (statement) => {
        console.log('Statement loaded:', statement);
        this.statement.set(statement);
        // Trier les transactions en antéchronologique
        const sortedTransactions = [...statement.transactions].sort(
          (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()
        );
        this.dataSource.set(new MatTableDataSource(sortedTransactions));
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Impossible de charger le relevé de compte');
        this.loading.set(false);
        console.error('Error loading statement:', err);
      },
    });
  }

  goBack() {
    this.router.navigate(['/']);
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  formatAmount(amount: number): string {
    return amount.toFixed(2) + ' €';
  }
}
