import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RecruiterDashboardComponent } from './recruiter-dashboard';
import { JobService } from '../../services/job/job.service';
import { Router } from '@angular/router';
import { of } from 'rxjs';

describe('RecruiterDashboardComponent', () => {
  let component: RecruiterDashboardComponent;
  let fixture: ComponentFixture<RecruiterDashboardComponent>;

  const mockJobService = {
    getApplicationsByRecruiter: () => of([]),
    getJobsByRecruiter: () => of([])
  };

  // ✅ FIX: Use a basic functional mock object instead of native jasmine spies
  const mockRouter = {
    events: of([]),
    navigate: (commands: any[]) => {} 
  };

  beforeEach(async () => {
    // ✅ FIX: Overwrite the method directly on the prototype to bypass global spyOn tracking constraints
    Storage.prototype.getItem = (key: string): string | null => {
      if (key === 'userId') return '1';
      if (key === 'role') return 'RECRUITER';
      return null;
    };

    await TestBed.configureTestingModule({
      imports: [RecruiterDashboardComponent],
      providers: [
        { provide: JobService, useValue: mockJobService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RecruiterDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});