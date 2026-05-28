import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  username = '';
  password = '';
  errorMessage = '';
  loading = false;

  submit(): void {
    this.errorMessage = '';
    this.loading = true;
    this.authService.login({ username: this.username, password: this.password }).subscribe({
      next: () => this.router.navigateByUrl('/dashboard'),
      error: () => {
        this.errorMessage = 'Identifiants incorrects';
        this.loading = false;
      },
    });
  }
}
