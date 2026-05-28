import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AccountService, BankAccount } from '../../../core/api/account.service';

@Component({
  selector: 'app-transfer-form',
  imports: [FormsModule, RouterLink],
  templateUrl: './transfer-form.html',
  styleUrl: './transfer-form.css',
})
export class TransferForm implements OnInit {
  private readonly accountService = inject(AccountService);
  private readonly router = inject(Router);

  accounts: BankAccount[] = [];
  accountSource = '';
  accountDestination = '';
  amount = 0;
  description = '';
  errorMessage = '';
  loading = false;

  ngOnInit(): void {
    this.accountService.list().subscribe({
      next: (accounts) => this.accounts = accounts,
      error: () => this.errorMessage = 'Chargement des comptes impossible',
    });
  }

  submit(): void {
    this.loading = true;
    this.errorMessage = '';
    this.accountService.transfer({
      accountSource: this.accountSource,
      accountDestination: this.accountDestination,
      amount: this.amount,
      description: this.description,
    }).subscribe({
      next: () => this.router.navigateByUrl('/accounts'),
      error: () => {
        this.errorMessage = 'Transfert impossible';
        this.loading = false;
      },
    });
  }
}
