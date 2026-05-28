import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Customer, CustomerService } from '../../../core/api/customer.service';

@Component({
  selector: 'app-customer-form',
  imports: [FormsModule, RouterLink],
  templateUrl: './customer-form.html',
  styleUrl: './customer-form.css',
})
export class CustomerForm implements OnInit {
  private readonly customerService = inject(CustomerService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  customer: Customer = { name: '', email: '' };
  customerId?: number;
  loading = false;
  errorMessage = '';

  get isEditMode(): boolean {
    return this.customerId !== undefined;
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.customerId = Number(id);
      this.loadCustomer(this.customerId);
    }
  }

  submit(): void {
    this.loading = true;
    this.errorMessage = '';
    const request = this.isEditMode && this.customerId
      ? this.customerService.update(this.customerId, this.customer)
      : this.customerService.create(this.customer);

    request.subscribe({
      next: () => this.router.navigateByUrl('/customers'),
      error: () => {
        this.errorMessage = 'Enregistrement impossible';
        this.loading = false;
      },
    });
  }

  private loadCustomer(id: number): void {
    this.loading = true;
    this.customerService.get(id).subscribe({
      next: (customer) => {
        this.customer = customer;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Client introuvable';
        this.loading = false;
      },
    });
  }
}
