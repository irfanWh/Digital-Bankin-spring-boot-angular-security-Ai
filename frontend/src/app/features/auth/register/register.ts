import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  username = '';
  email = '';
  password = '';
  errorMessage = '';
  loading = false;

  submit(): void {
    this.errorMessage = '';
    this.loading = true;
    this.authService.register({
      username: this.username,
      email: this.email,
      password: this.password,
    }).subscribe({
      next: () => this.router.navigateByUrl('/dashboard'),
      error: () => {
        this.errorMessage = 'Inscription impossible';
        this.loading = false;
      },
    });
  }
}
