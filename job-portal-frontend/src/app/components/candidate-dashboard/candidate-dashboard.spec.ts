import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CandidateDashboardComponent } from './candidate-dashboard';
import { JobService } from '../../services/job/job.service';
import { Router, ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

describe('CandidateDashboardComponent', () => {
  let component: CandidateDashboardComponent;
  let fixture: ComponentFixture<CandidateDashboardComponent>;

  const mockJobService = {
    getApplicationsByCandidate: () => of([])
  };

  const mockRouter = {
    navigate: (commands: any[]) => {}
  };

  // ✅ Mock the route params snapshot context dynamically
  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: (key: string) => '1' // Returns a matching placeholder ID string
      }
    }
  };

  beforeEach(async () => {
    Storage.prototype.getItem = (key: string): string | null => {
      if (key === 'userId') return '1';
      if (key === 'role') return 'CANDIDATE';
      return null;
    };

    await TestBed.configureTestingModule({
      imports: [CandidateDashboardComponent],
      providers: [
        { provide: JobService, useValue: mockJobService },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CandidateDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});