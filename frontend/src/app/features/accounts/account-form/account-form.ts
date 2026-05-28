import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AccountService } from '../../../core/api/account.service';
import { Customer, CustomerService } from '../../../core/api/customer.service';

@Component({
  selector: 'app-account-form',
  imports: [FormsModule, RouterLink],
  templateUrl: './account-form.html',
  styleUrl: './account-form.css',
})
export class AccountForm implements OnInit {
  private readonly accountService = inject(AccountService);
  private readonly customerService = inject(CustomerService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  accountType: 'current' | 'saving' = 'current';
  customers: Customer[] = [];
  customerId?: number;
  initialBalance = 0;
  overDraft = 0;
  interestRate = 0;
  loading = false;
  errorMessage = '';

  ngOnInit(): void {
    this.accountType = this.route.snapshot.data['type'] || 'current';
    this.customerService.list().subscribe({
      next: (customers) => this.customers = customers,
      error: () => this.errorMessage = 'Chargement des clients impossible',
    });
  }

  submit(): void {
    if (!this.customerId) {
      this.errorMessage = 'Client obligatoire';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    const request = this.accountType === 'current'
      ? this.accountService.createCurrent({
        initialBalance: this.initialBalance,
        overDraft: this.overDraft,
        customerId: this.customerId,
      })
      : this.accountService.createSaving({
        initialBalance: this.initialBalance,
        interestRate: this.interestRate,
        customerId: this.customerId,
      });

    request.subscribe({
      next: () => this.router.navigateByUrl('/accounts'),
      error: () => {
        this.errorMessage = 'Création du compte impossible';
        this.loading = false;
      },
    });
  }
}
