import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router'; 
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required])
    });
  }

  onLogin(): void {
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value).subscribe({
        next: (response: any) => {
          console.log('Spring Boot API Auth Handshake Complete:', response);
          
          // ✅ PERSISTENCE: Save only the missing user ID (Token/Role are automatically handled by AuthService)
          sessionStorage.setItem('userId', response.id.toString());

          // ✅ DYNAMIC ROUTING: Navigate based on backend role payload
          if (response.role === 'RECRUITER') {
            this.router.navigate(['/recruiter']); 
          } else if (response.role === 'CANDIDATE') {
            this.router.navigate(['/jobs']); 
          } else {
            console.error('Unknown user role authorization state:', response.role);
            this.router.navigate(['/']);
          }
        },
        error: (err: any) => {
          console.error('Authentication Handshake Failed:', err);
          alert('Invalid credentials. Please verify your email and password.');
        }
      });
    }
  }
}