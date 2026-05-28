import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

export interface DashboardStats {
  totalCustomers: number;
  totalAccounts: number;
  totalBalance: number;
  operationsByType: Record<string, number>;
  accountsByStatus: Record<string, number>;
  accountsByType: Record<string, number>;
}

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/dashboard/stats';

  stats(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(this.apiUrl);
  }
}
