import { Component, OnInit, OnDestroy } from '@angular/core'; 
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; 
import { Router, NavigationEnd } from '@angular/router'; 
import { Subscription, interval } from 'rxjs'; 
import { filter } from 'rxjs/operators';
import { JobService, JobResponseDTO } from '../../services/job/job.service';

@Component({
  selector: 'app-recruiter-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule], 
  templateUrl: './recruiter-dashboard.html',
  styleUrl: './recruiter-dashboard.scss'
})
export class RecruiterDashboardComponent implements OnInit, OnDestroy { 
  
  newJob: Partial<JobResponseDTO> = {
    title: '',
    location: '',
    description: ''
  };

  incomingApplications: any[] = [];
  myPostedJobs: JobResponseDTO[] = [];
  currentRecruiterId!: number;

  private routerSubscription!: Subscription;
  private pollingSubscription!: Subscription;

  constructor(private jobService: JobService, private router: Router) {}

  ngOnInit(): void {
    // 1. Extract session parameters from browser memory
    const savedId = sessionStorage.getItem('userId');
    const savedRole = sessionStorage.getItem('role');

    // 2. Security Check: Block unauthorized users or unauthenticated state requests
    if (!savedId || savedRole !== 'RECRUITER') {
      alert('Access Denied: This dashboard is reserved strictly for authenticated Recruiter profiles.');
      this.router.navigate(['/login']);
      return;
    }

    // 3. Bind the active recruiter identity dynamically
    this.currentRecruiterId = parseInt(savedId, 10);

    // 4. Initial multi-tenant data stream retrieval
    this.loadAllDashboardData();

    // 5. INTERNAL ROUTING RE-SYNC ENGINE
    this.routerSubscription = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.loadAllDashboardData();
    });

    // 6. CROSS-TAB AUTOMATIC SYNC: Runs background pings every 5 seconds 
    this.pollingSubscription = interval(5000).subscribe(() => {
      this.loadIncomingApplications();
    });
  }

  /**
   * Consolidated data pull helper execution channel
   */
  private loadAllDashboardData(): void {
    this.loadIncomingApplications();
    this.loadMyPostedJobs();
  }

  /**
   * 📥 Fetches all applications across ALL jobs posted by this specific recruiter
   */
  loadIncomingApplications(): void {
    this.jobService.getApplicationsByRecruiter(this.currentRecruiterId).subscribe({
      next: (data) => {
        this.incomingApplications = data;
        console.log('Successfully fetched recruiter applications across all jobs:', data);
      },
      error: (err) => {
        console.error('Failed to load incoming applications:', err);
      }
    });
  }

  /**
   * 🎯 Fetches all job posts hosted by this specific recruiter profile session
   */
  loadMyPostedJobs(): void {
    this.jobService.getJobsByRecruiter(this.currentRecruiterId).subscribe({
      next: (data) => {
        this.myPostedJobs = data;
        console.log('Successfully loaded recruiter specific job posts:', data);
      },
      error: (err) => {
        console.error('Failed to load recruiter job vacancies:', err);
      }
    });
  }

  /**
   * 📢 Submits a new job opening form payload to your backend database
   */
  onCreateJob(): void {
    if (!this.newJob.title || !this.newJob.location || !this.newJob.description) {
      alert('Please fill out all job posting fields.');
      return;
    }

    this.jobService.postJob(this.newJob, this.currentRecruiterId).subscribe({
      next: (createdJob) => {
        alert(`Job "${createdJob.title}" published successfully!`);
        this.newJob = { title: '', location: '', description: '' };
        this.loadMyPostedJobs(); 
      },
      error: (err) => {
        console.error('Failed to publish vacancy listing:', err);
        alert('Failed to post job. Check backend server console.');
      }
    });
  }

  /**
   * ⚡ Triggers the state update machine (Accept / Reject)
   */
  onUpdateStatus(applicationId: number, status: string): void {
    this.jobService.updateApplicationStatus(applicationId, status, this.currentRecruiterId).subscribe({
      next: (updatedRecord) => {
        alert(`Application status successfully updated to: ${status}`);
        this.loadIncomingApplications();
      },
      error: (err) => {
        console.error('Status modification process failed:', err);
        alert('Failed to modify application status state.');
      }
    });
  }

  // ✅ FIXED: Explicit lifecycle teardown execution
  ngOnDestroy(): void {
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
      console.log('Router event listeners cleanly torn down.');
    }
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
      console.log('Recruiter background polling successfully terminated.');
    }
  }
}