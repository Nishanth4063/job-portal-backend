import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router'; // 🎯 FIXED: Imported RouterLink
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterLink], // 🎯 FIXED: Added RouterLink back to compilation boundaries
  templateUrl: './register.html',
  styleUrl: './register.scss'
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;

  // Dependency injection of backend communication service and routing engine
  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.registerForm = new FormGroup({
      // Added 'name' tracking control to prevent Spring Boot @NotBlank validation failures
      name: new FormControl('', [Validators.required]),
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required, Validators.minLength(6)]),
      
      // Changed default value from 'APPLICANT' to 'CANDIDATE' to align with backend Java Role Enum keys
      role: new FormControl('CANDIDATE', [Validators.required])
    });
  }

  onRegister(): void {
    if (this.registerForm.valid) {
      // Dispatches the strongly-typed form value payload directly to the Spring Boot REST API
      this.authService.register(this.registerForm.value).subscribe({
        next: (response: any) => {
          console.log('User Registered successfully in DB:', response);
          // Standard successful lifecycle redirect down to the login component layout
          this.router.navigate(['/login']);
        },
        error: (err: any) => {
          console.error('Registration API Handshake Failed:', err);
        }
      });
    } else {
      // Highlights empty fields with CSS errors if user triggers submittal prematurely
      this.registerForm.markAllAsTouched();
    }
  }
}