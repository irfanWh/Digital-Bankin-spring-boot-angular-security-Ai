import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AccountOperation, AccountService, BankAccount } from '../../../core/api/account.service';

@Component({
  selector: 'app-account-details',
  imports: [FormsModule, RouterLink],
  templateUrl: './account-details.html',
  styleUrl: './account-details.css',
})
export class AccountDetails implements OnInit {
  private readonly accountService = inject(AccountService);
  private readonly route = inject(ActivatedRoute);

  account?: BankAccount;
  operations: AccountOperation[] = [];
  amount = 0;
  description = '';
  errorMessage = '';

  ngOnInit(): void {
    this.loadAccount();
  }

  loadAccount(): void {
    const accountId = this.route.snapshot.paramMap.get('id');
    if (!accountId) {
      return;
    }
    this.accountService.get(accountId).subscribe({
      next: (account) => {
        this.account = account;
        this.loadOperations(account.id);
      },
      error: () => this.errorMessage = 'Compte introuvable',
    });
  }

  credit(): void {
    this.runOperation('credit');
  }

  debit(): void {
    this.runOperation('debit');
  }

  private runOperation(type: 'credit' | 'debit'): void {
    if (!this.account) {
      return;
    }
    const request = {
      accountId: this.account.id,
      amount: this.amount,
      description: this.description,
    };
    const operation = type === 'credit'
      ? this.accountService.credit(request)
      : this.accountService.debit(request);

    operation.subscribe({
      next: () => {
        this.amount = 0;
        this.description = '';
        this.loadAccount();
      },
      error: () => this.errorMessage = 'Opération impossible',
    });
  }

  private loadOperations(accountId: string): void {
    this.accountService.operations(accountId).subscribe({
      next: (operations) => this.operations = operations,
      error: () => this.errorMessage = 'Historique indisponible',
    });
  }
}
