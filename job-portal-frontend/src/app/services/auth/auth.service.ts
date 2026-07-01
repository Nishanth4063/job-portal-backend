import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { User, AuthResponse } from '../../models/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  // PRODUCTION FIX: Route relatively through your Nginx docker proxy layer
  private apiUrl = '/api/auth';

  constructor(private http: HttpClient) { }

  // 1. POST: Register new user profile -> /api/auth/register
  register(userData: User): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/register`, userData);
  }

  // 2. POST: Authenticate user -> /api/auth/login
  login(credentials: Pick<User, 'email' | 'password'>): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((response: AuthResponse) => {
        // ✅ FIXED: Using sessionStorage to isolate session tokens per browser tab
        sessionStorage.setItem('token', response.token);
        sessionStorage.setItem('email', response.email);
        sessionStorage.setItem('role', response.role);
      })
    );
  }

  // Helper session management utilities
  getToken(): string | null {
    // ✅ FIXED: Read token from sessionStorage
    return sessionStorage.getItem('token');
  }

  getRole(): string | null {
    // ✅ FIXED: Read role from sessionStorage
    return sessionStorage.getItem('role');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    // ✅ FIXED: Clear current tab's sessionStorage session cleanly
    sessionStorage.clear();
  }
}