import { Routes } from '@angular/router';
import { CashDeposit } from './features/cash-deposit/cash-deposit';
import { CashWithdrawal } from './features/cash-withdrawal/cash-withdrawal';

export const routes: Routes = [
  {
    path: 'cash-deposit',
    component: CashDeposit,
    data: { title: "Dépôt d'argent" },
  },
  {
    path: 'cash-withdrawal',
    component: CashWithdrawal,
    data: { title: "Retrait d'argent" },
  },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./features/account-dashboard/account-dashboard').then((m) => m.AccountDashboard),
    children: [
      {
        path: 'cash-deposit',
        component: CashDeposit,
        data: { title: "Dépôt d'argent" },
      },
      {
        path: 'cash-withdrawal',
        component: CashWithdrawal,
        data: { title: "Retrait d'argent" },
      },
    ],
  },
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full',
  },
];
