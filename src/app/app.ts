import { Component, signal } from '@angular/core';
import { AccountDashboard } from "./features/account-dashboard/account-dashboard";

@Component({
  selector: 'app-root',
  imports: [AccountDashboard],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('front-bankaccount');
}
