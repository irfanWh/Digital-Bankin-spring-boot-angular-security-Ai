import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { Login } from './features/auth/login/login';
import { Register } from './features/auth/register/register';
import { AccountDetails } from './features/accounts/account-details/account-details';
import { AccountForm } from './features/accounts/account-form/account-form';
import { AccountList } from './features/accounts/account-list/account-list';
import { CustomerForm } from './features/customers/customer-form/customer-form';
import { CustomerList } from './features/customers/customer-list/customer-list';
import { Dashboard } from './features/dashboard/dashboard';
import { TransferForm } from './features/operations/transfer-form/transfer-form';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'dashboard', component: Dashboard, canActivate: [authGuard] },
  { path: 'customers', component: CustomerList, canActivate: [authGuard] },
  { path: 'customers/new', component: CustomerForm, canActivate: [authGuard] },
  { path: 'customers/:id/edit', component: CustomerForm, canActivate: [authGuard] },
  { path: 'accounts', component: AccountList, canActivate: [authGuard] },
  { path: 'accounts/new/current', component: AccountForm, data: { type: 'current' }, canActivate: [authGuard] },
  { path: 'accounts/new/saving', component: AccountForm, data: { type: 'saving' }, canActivate: [authGuard] },
  { path: 'accounts/:id', component: AccountDetails, canActivate: [authGuard] },
  { path: 'operations/transfer', component: TransferForm, canActivate: [authGuard] },
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: '**', redirectTo: 'dashboard' },
];
