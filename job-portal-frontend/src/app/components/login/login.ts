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
          console.log('Spring Boot API Auth Token Received:', response);
          
          // ✅ FIX: Save everything including the explicit unique User ID
          localStorage.setItem('token', response.token);
          localStorage.setItem('email', response.email);
          localStorage.setItem('role', response.role);
          localStorage.setItem('userId', response.id.toString()); // Captured to isolate recruiter data buckets

          // ✅ DYNAMIC ROUTING FIX: Route users dynamically based on their roles
          if (response.role === 'RECRUITER') {
            this.router.navigate(['/recruiter']); // Direct recruiter to management dashboard
          } else if (response.role === 'CANDIDATE') {
            this.router.navigate(['/jobs']); // Direct candidate to job seeker browse feed
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