import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Customer, CustomerService } from '../../../core/api/customer.service';

@Component({
  selector: 'app-customer-list',
  imports: [FormsModule, RouterLink],
  templateUrl: './customer-list.html',
  styleUrl: './customer-list.css',
})
export class CustomerList implements OnInit {
  private readonly customerService = inject(CustomerService);

  customers: Customer[] = [];
  keyword = '';
  loading = false;
  errorMessage = '';

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.loading = true;
    this.errorMessage = '';
    this.customerService.list().subscribe({
      next: (customers) => {
        this.customers = customers;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Chargement des clients impossible';
        this.loading = false;
      },
    });
  }

  search(): void {
    const keyword = this.keyword.trim();
    if (!keyword) {
      this.loadCustomers();
      return;
    }
    this.loading = true;
    this.customerService.search(keyword).subscribe({
      next: (customers) => {
        this.customers = customers;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Recherche impossible';
        this.loading = false;
      },
    });
  }

  deleteCustomer(customer: Customer): void {
    if (!customer.id) {
      return;
    }
    this.customerService.delete(customer.id).subscribe({
      next: () => this.customers = this.customers.filter((item) => item.id !== customer.id),
      error: () => this.errorMessage = 'Suppression impossible',
    });
  }
}
