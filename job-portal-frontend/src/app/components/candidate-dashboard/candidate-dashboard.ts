import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, ActivatedRoute } from '@angular/router'; 
import { JobService } from '../../services/job/job.service';

@Component({
  selector: 'app-candidate-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink], 
  templateUrl: './candidate-dashboard.html',
  styleUrl: './candidate-dashboard.scss',
})
export class CandidateDashboardComponent implements OnInit {

  // Core tracking state arrays
  myApplications: any[] = [];
  
  // Isolated dynamic metrics counters
  totalAppliedCount: number = 0;
  totalResponsesCount: number = 0;
  
  currentCandidateId!: number;

  // 🎯 UPDATED: Injected ActivatedRoute context provider
  constructor(
    private jobService: JobService, 
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // 1. Recover active identity tokens from browser session state memory
    const savedId = localStorage.getItem('userId');
    const savedRole = localStorage.getItem('role');
    
    // 🎯 2. DYNAMIC READ: Extract the explicit ID parameter string out of the address path tree
    const routeParamId = this.route.snapshot.paramMap.get('id');

    // 🔒 3. ADVANCED RBAC & ANTI-TAMPER SECURITY BOUNDARY Check
    if (!savedId || savedRole !== 'CANDIDATE' || savedId !== routeParamId) {
      alert('Access Denied: Security validation mismatch. Ejecting session context.');
      localStorage.clear();
      this.router.navigate(['/login']);
      return;
    }

    this.currentCandidateId = parseInt(savedId, 10);
    
    // 4. Fire relational database stream lookup scoped strictly to verified identity
    this.loadCandidateApplicationHistory();
  }

  /**
   * 📥 Hits the REST API to pull all applications submitted by this candidate
   */
  loadCandidateApplicationHistory(): void {
    this.jobService.getApplicationsByCandidate(this.currentCandidateId).subscribe({
      next: (data) => {
        this.myApplications = data;
        
        // 📊 METRICS COMPUTATION ENGINE
        this.totalAppliedCount = data.length;
        
        // Filter out records that are no longer 'PENDING' to calculate the true responses count
        this.totalResponsesCount = data.filter(
          (app: any) => app.status === 'ACCEPTED' || app.status === 'REJECTED'
        ).length;

        console.log('Candidate application grid data telemetry updated:', data);
      },
      error: (err) => {
        console.error('Failed to load candidate application timeline profiles:', err);
      }
    });
  }
}