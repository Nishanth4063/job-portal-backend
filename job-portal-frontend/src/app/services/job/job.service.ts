import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface JobResponseDTO {
  id: number;
  title: string;
  description: string;
  location: string;
  salary: number; 
  postedDate: string;
  postedByName: string;
}

@Injectable({
  providedIn: 'root'
})
export class JobService {

  // ✅ CORRECTED: Changed from hardcoded localhost to relative URLs for Nginx proxy routing
  private apiUrl = '/api/jobs';
  private appUrl = '/api/applications'; 

  constructor(private http: HttpClient) { }

  // 1. POST: Create a new job -> /api/jobs/create/{userId}
  postJob(jobData: Partial<JobResponseDTO>, userId: number): Observable<JobResponseDTO> {
    return this.http.post<JobResponseDTO>(`${this.apiUrl}/create/${userId}`, jobData);
  }

  // 2. GET: Fetch all available jobs -> /api/jobs/all
  fetchAllJobs(): Observable<JobResponseDTO[]> {
    return this.http.get<JobResponseDTO[]>(`${this.apiUrl}/all`);
  }

  // 3. GET: Search jobs dynamically -> /api/jobs/search?title=Java&location=Bengaluru
  searchJobs(title?: string, location?: string): Observable<JobResponseDTO[]> {
    let params = new HttpParams();
    if (title) params = params.set('title', title);
    if (location) params = params.set('location', location);
    return this.http.get<JobResponseDTO[]>(`${this.apiUrl}/search`, { params });
  }

  /**
   * 🎯 REFACTORED FOR RESUME UPLOAD
   * 4. POST: Apply for a job -> /api/applications/apply/{userId}/{jobId}
   */
  applyForJob(jobId: number, userId: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file, file.name); // Maps to Spring Boot @RequestParam("file")

    return this.http.post<any>(`${this.appUrl}/apply/${userId}/${jobId}`, formData);
  }

  // 5. GET: Fetch incoming applications for a job posting -> /api/applications/job/{jobId}
  getApplicationsByJob(jobId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.appUrl}/job/${jobId}`);
  }

  // 6. MULTI-TENANCY VIEW: Fetch applications across all jobs belonging to a recruiter -> /api/applications/recruiter/{recruiterId}
  getApplicationsByRecruiter(recruiterId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.appUrl}/recruiter/${recruiterId}`);
  }

  // 7. NEW METHOD: Fetch all jobs posted exclusively by a specific recruiter -> /api/jobs/recruiter/{recruiterId}
  getJobsByRecruiter(recruiterId: number): Observable<JobResponseDTO[]> {
    return this.http.get<JobResponseDTO[]>(`${this.apiUrl}/recruiter/${recruiterId}`);
  }

  // 8. PUT: Update Application status -> /api/applications/{applicationId}/status?status=ACCEPTED&employerId=1
  updateApplicationStatus(applicationId: number, status: string, employerId: number): Observable<any> {
    let params = new HttpParams()
      .set('status', status)
      .set('employerId', employerId.toString());

    return this.http.put<any>(`${this.appUrl}/${applicationId}/status`, null, { params });
  }

  // 🎯 9. NEW METHOD: Fetch application history submitted strictly by this candidate -> /api/applications/candidate/{userId}
  getApplicationsByCandidate(candidateId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.appUrl}/candidate/${candidateId}`);
  }
}