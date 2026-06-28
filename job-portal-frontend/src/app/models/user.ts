export interface User {
  id?: number;
  email: string;
  role: 'CANDIDATE' | 'EMPLOYER' | 'ADMIN'; // Using strict literal types matching your backend enum
  password?: string; // Optional because we don't return passwords back from the database
}

export interface AuthResponse {
  token: string;
  email: string;
  role: string;
}