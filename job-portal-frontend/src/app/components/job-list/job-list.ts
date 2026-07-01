import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; 
import { RouterLink, Router } from '@angular/router'; // 🎯 UPDATED: Added Router for clean session ejection
import { JobService, JobResponseDTO } from '../../services/job/job.service';

@Component({
  selector: 'app-job-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink], 
  templateUrl: './job-list.html',
  styleUrl: './job-list.scss'
})
export class JobListComponent implements OnInit {
  jobs: JobResponseDTO[] = [];
  errorMessage: string = '';
  successMessage: string = ''; 

  searchTitle: string = '';
  searchLocation: string = '';

  // Tracks selected raw file attachments across individual Job Card IDs
  selectedFiles: { [jobId: number]: File } = {};

  // 🎯 UPDATED: Injected Router dependency
  constructor(private jobService: JobService, private router: Router) {}

  ngOnInit(): void {
    this.loadJobs();
  }

  /**
   * Dynamic session accessor method providing the context ID to your HTML template
   */
  getLoggedUserId(): string {
    return sessionStorage.getItem('userId') || '0';
  }

  /**
   * Fetches all active job postings from the secure database backend.
   */
  loadJobs(): void {
    this.jobService.fetchAllJobs().subscribe({
      next: (data: JobResponseDTO[]) => {
        this.jobs = data;
        console.log('Successfully fetched live database jobs:', this.jobs);
      },
      error: (err: any) => {
        this.errorMessage = 'Failed to load job listings. Secure handshake issue.';
        console.error('Error fetching jobs via API:', err);
      }
    });
  }

  /**
   * Executes dynamic filtered search requests against title and location coordinates.
   */
  onSearch(): void {
    this.jobService.searchJobs(this.searchTitle, this.searchLocation).subscribe({
      next: (data: JobResponseDTO[]) => {
        this.jobs = data;
        console.log('Filtered search results from API:', this.jobs);
      },
      error: (err: any) => {
        this.errorMessage = 'Search parameters rejected by the remote API.';
        console.error('Error executing dynamic search:', err);
      }
    });
  }

  /**
   * Intercepts browser file choices and enforces standard PDF content type constraints
   */
  onFileSelected(event: any, jobId: number): void {
    const file: File = event.target.files[0];
    if (file) {
      if (file.type !== 'application/pdf') {
        alert('Validation Error: Only valid documents in PDF format are supported.');
        event.target.value = ''; // Resets browser field elements
        return;
      }
      this.selectedFiles[jobId] = file;
    }
  }

  /**
   * Handles the click event from the green "Apply Now" button.
   */
  onApply(jobId: number): void {
    this.successMessage = '';
    this.errorMessage = '';
    
    const file = this.selectedFiles[jobId];
    if (!file) {
      this.errorMessage = 'Action Required: Please browse and attach a PDF copy of your resume.';
      return;
    }

    const savedId = sessionStorage.getItem('userId');
    
    // 🎯 SECURITY OPTIMIZATION: If the session ID is missing or evaluates to fallback parameters, eject immediately
    if (!savedId || savedId === '0') {
      this.errorMessage = 'Session expired or invalid. Please log in again to apply.';
      sessionStorage.clear();
      this.router.navigate(['/login']);
      return;
    }

    const loggedInUserId = parseInt(savedId, 10); 

    this.jobService.applyForJob(jobId, loggedInUserId, file).subscribe({
      next: (response: any) => {
        this.successMessage = `Application successful! Initial status is: ${response.status || 'PENDING'}`;
        delete this.selectedFiles[jobId]; 
        setTimeout(() => this.successMessage = '', 4000);
      },
      error: (err: any) => {
        this.errorMessage = 'Application failed. You may have already applied to this position!';
        console.error('Application Error Stream:', err);
      }
    });
  }
}