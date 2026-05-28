import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { CurrentUser, LoginRequest, LoginResponse, RegisterRequest } from './auth.models';

const TOKEN_KEY = 'digital_banking_token';
const USERNAME_KEY = 'digital_banking_username';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly apiUrl = 'http://localhost:8080/api/auth';

  readonly username = signal<string | null>(localStorage.getItem(USERNAME_KEY));

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, request).pipe(
      tap((response) => this.saveSession(response)),
    );
  }

  register(request: RegisterRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/register`, request).pipe(
      tap((response) => this.saveSession(response)),
    );
  }

  me(): Observable<CurrentUser> {
    return this.http.get<CurrentUser>(`${this.apiUrl}/me`);
  }

  token(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    return !!this.token();
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USERNAME_KEY);
    this.username.set(null);
    this.router.navigateByUrl('/login');
  }

  private saveSession(response: LoginResponse): void {
    localStorage.setItem(TOKEN_KEY, response.token);
    localStorage.setItem(USERNAME_KEY, response.username);
    this.username.set(response.username);
  }
}
