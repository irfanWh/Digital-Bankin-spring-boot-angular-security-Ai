import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Customer } from './customer.service';

export interface BankAccount {
  id: string;
  balance: number;
  createdAt: string;
  status: 'CREATED' | 'ACTIVATED' | 'SUSPENDED';
  customerDTO?: Customer;
  type: 'CURRENT' | 'SAVING';
  overDraft?: number;
  interestRate?: number;
  createdBy?: string;
  updatedBy?: string;
}

export interface AccountOperation {
  id: number;
  operationDate: string;
  amount: number;
  type: 'DEBIT' | 'CREDIT';
  bankAccountId: string;
  description: string;
  createdBy?: string;
}

export interface CurrentAccountRequest {
  initialBalance: number;
  overDraft: number;
  customerId: number;
}

export interface SavingAccountRequest {
  initialBalance: number;
  interestRate: number;
  customerId: number;
}

export interface OperationRequest {
  accountId: string;
  amount: number;
  description: string;
}

export interface TransferRequest {
  accountSource: string;
  accountDestination: string;
  amount: number;
  description: string;
}

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/accounts';

  list(): Observable<BankAccount[]> {
    return this.http.get<BankAccount[]>(this.apiUrl);
  }

  get(accountId: string): Observable<BankAccount> {
    return this.http.get<BankAccount>(`${this.apiUrl}/${accountId}`);
  }

  createCurrent(request: CurrentAccountRequest): Observable<BankAccount> {
    return this.http.post<BankAccount>(`${this.apiUrl}/current`, request);
  }

  createSaving(request: SavingAccountRequest): Observable<BankAccount> {
    return this.http.post<BankAccount>(`${this.apiUrl}/saving`, request);
  }

  operations(accountId: string): Observable<AccountOperation[]> {
    return this.http.get<AccountOperation[]>(`${this.apiUrl}/${accountId}/operations`);
  }

  debit(request: OperationRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/debit`, request);
  }

  credit(request: OperationRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/credit`, request);
  }

  transfer(request: TransferRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/transfer`, request);
  }
}
