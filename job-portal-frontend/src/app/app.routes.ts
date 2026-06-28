import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login';
import { RegisterComponent } from './components/register/register';
import { JobListComponent } from './components/job-list/job-list';
import { RecruiterDashboardComponent } from './components/recruiter-dashboard/recruiter-dashboard';
import { CandidateDashboardComponent } from './components/candidate-dashboard/candidate-dashboard';
import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
  // Default route fallback safely to login
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // Public Gateway Feature Routes (No Guards Needed)
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  
  // 🔒 Candidate Exploration Route - Protected
  { path: 'jobs', component: JobListComponent, canActivate: [authGuard] },

  // 🔒 Recruiter Control Center Module Route - Protected 
  { path: 'recruiter', component: RecruiterDashboardComponent, canActivate: [authGuard] },

  // 🔒 🎯 FIXED: Added /:id path variable context and attached the secure authGuard gatekeeper
  { path: 'candidate/:id', component: CandidateDashboardComponent, canActivate: [authGuard] },

  // ⚠️ CRITICAL POSITIONING: Wildcard Fallback must ALWAYS stay at the very bottom
  { path: '**', redirectTo: 'login' }
];