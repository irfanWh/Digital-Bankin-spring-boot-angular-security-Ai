import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AccountService, BankAccount } from '../../../core/api/account.service';

@Component({
  selector: 'app-account-list',
  imports: [RouterLink],
  templateUrl: './account-list.html',
  styleUrl: './account-list.css',
})
export class AccountList implements OnInit {
  private readonly accountService = inject(AccountService);

  accounts: BankAccount[] = [];
  loading = false;
  errorMessage = '';

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.loading = true;
    this.errorMessage = '';
    this.accountService.list().subscribe({
      next: (accounts) => {
        this.accounts = accounts;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Chargement des comptes impossible';
        this.loading = false;
      },
    });
  }
}
