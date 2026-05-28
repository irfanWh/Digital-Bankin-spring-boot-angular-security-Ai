import { Component, OnInit, inject } from '@angular/core';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { DashboardService, DashboardStats } from '../../core/api/dashboard.service';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-dashboard',
  imports: [BaseChartDirective],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  readonly authService = inject(AuthService);
  private readonly dashboardService = inject(DashboardService);

  stats?: DashboardStats;
  errorMessage = '';

  readonly chartOptions: ChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom',
      },
    },
  };

  operationChartData: ChartConfiguration<'doughnut'>['data'] = {
    labels: [],
    datasets: [{ data: [], backgroundColor: ['#0f766e', '#b91c1c'] }],
  };

  statusChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [{ data: [], label: 'Comptes', backgroundColor: '#2563eb' }],
  };

  typeChartData: ChartConfiguration<'pie'>['data'] = {
    labels: [],
    datasets: [{ data: [], backgroundColor: ['#f59e0b', '#14b8a6', '#64748b'] }],
  };

  ngOnInit(): void {
    this.dashboardService.stats().subscribe({
      next: (stats) => {
        this.stats = stats;
        this.operationChartData = this.toDoughnutData(stats.operationsByType, ['#0f766e', '#b91c1c']);
        this.statusChartData = this.toBarData(stats.accountsByStatus);
        this.typeChartData = this.toPieData(stats.accountsByType);
      },
      error: () => this.errorMessage = 'Statistiques indisponibles',
    });
  }

  private toDoughnutData(values: Record<string, number>, colors: string[]): ChartConfiguration<'doughnut'>['data'] {
    return {
      labels: Object.keys(values),
      datasets: [{ data: Object.values(values), backgroundColor: colors }],
    };
  }

  private toPieData(values: Record<string, number>): ChartConfiguration<'pie'>['data'] {
    return {
      labels: Object.keys(values),
      datasets: [{ data: Object.values(values), backgroundColor: ['#f59e0b', '#14b8a6', '#64748b'] }],
    };
  }

  private toBarData(values: Record<string, number>): ChartConfiguration<'bar'>['data'] {
    return {
      labels: Object.keys(values),
      datasets: [{ data: Object.values(values), label: 'Comptes', backgroundColor: '#2563eb' }],
    };
  }
}
