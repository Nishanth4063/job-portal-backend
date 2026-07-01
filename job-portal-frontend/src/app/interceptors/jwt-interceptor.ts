import { HttpInterceptorFn } from '@angular/common/http';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  // Pull the active token out of the secure browser storage layer
  const token = sessionStorage.getItem('token');

  // If a token exists, clone the request and inject the Authorization Bearer header
  if (token) {
    const clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    // Hand off the updated request package to the execution line
    return next(clonedRequest);
  }

  // If no token is found (like during login/register requests), pass the raw request along unchanged
  return next(req);
};