import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { Login } from './features/auth/login/login';
import { Register } from './features/auth/register/register';
import { CustomerForm } from './features/customers/customer-form/customer-form';
import { CustomerList } from './features/customers/customer-list/customer-list';
import { Dashboard } from './features/dashboard/dashboard';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'dashboard', component: Dashboard, canActivate: [authGuard] },
  { path: 'customers', component: CustomerList, canActivate: [authGuard] },
  { path: 'customers/new', component: CustomerForm, canActivate: [authGuard] },
  { path: 'customers/:id/edit', component: CustomerForm, canActivate: [authGuard] },
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: '**', redirectTo: 'dashboard' },
];
