import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Application } from '../../models/application';

@Injectable({
  providedIn: 'root'
})
export class ApplicationService {
  
  private apiUrl = 'http://localhost:8080/api/applications';

  constructor(private http: HttpClient) { }

  /**
   * 🎯 REFACTORED FOR RESUME UPLOAD
   * 1. POST: Apply to a job -> /api/applications/apply/{userId}/{jobId}
   */
  applyToJob(userId: number, jobId: number, file: File): Observable<Application> {
    const formData = new FormData();
    formData.append('file', file, file.name); // Maps to backend MultiPart file interceptor

    return this.http.post<Application>(`${this.apiUrl}/apply/${userId}/${jobId}`, formData);
  }

  // 2. GET: Retrieve applications by candidate -> /api/applications/candidate/{userId}
  getApplicationsByCandidate(userId: number): Observable<Application[]> {
    return this.http.get<Application[]>(`${this.apiUrl}/candidate/${userId}`);
  }

  // 3. GET: Retrieve applications by job -> /api/applications/job/{jobId}
  getApplicationsByJob(jobId: number): Observable<Application[]> {
    return this.http.get<Application[]>(`${this.apiUrl}/job/${jobId}`);
  }

  // 4. PUT: Update status -> /api/applications/{applicationId}/status?status=ACCEPTED&employerId=1
  updateApplicationStatus(applicationId: number, status: string, employerId: number): Observable<Application> {
    const params = new HttpParams()
      .set('status', status)
      .set('employerId', employerId.toString());

    return this.http.put<Application>(`${this.apiUrl}/${applicationId}/status`, {}, { params });
  }
}