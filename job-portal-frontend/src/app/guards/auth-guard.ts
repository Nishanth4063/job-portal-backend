import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);

  // 1. Recover active identity tokens directly from persistent browser cache slots
  const token = sessionStorage.getItem('token');
  const role = (sessionStorage.getItem('role') || '').toUpperCase().trim();
  const userId = sessionStorage.getItem('userId');

  // 🛡️ REFRESH RESILIENCY LOGIC BOUNDARY CHECK
  // Relying strictly on stored session tokens so hard reloads do not destroy the routing context
  if (!token || !role || !userId) {
    console.warn('Access Blocked: Persistent authorization markers missing from storage.');
    sessionStorage.clear(); // Clear any corrupted structural remnants safely
    router.navigate(['/login']);
    return false;
  }

  const targetUrl = state.url;
  const routeParamId = route.paramMap.get('id');

  // 2. Strict Role-Based Access Control (RBAC) Routing Constraints
  if (targetUrl.includes('/recruiter') && role !== 'RECRUITER') {
    router.navigate(['/jobs']);
    return false;
  }

  if (targetUrl.includes('/jobs') && role !== 'CANDIDATE') {
    router.navigate(['/recruiter']);
    return false;
  }

  // 3. Dynamic Multi-Tenancy Parametric Isolation Rule Check
  if (targetUrl.includes('/candidate')) {
    // Blocks address bar data-peeking exploits across cross-tenant candidate dashboards
    if (role !== 'CANDIDATE' || (routeParamId && routeParamId !== userId)) {
      console.error('Security Violation: Token payload identity mismatch.');
      router.navigate(['/jobs']);
      return false;
    }
  }

  return true; // Execution path safely validated
};