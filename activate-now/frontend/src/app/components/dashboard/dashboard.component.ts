import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StudentService } from '../../services/student.service';
import { DashboardResponse } from '../../models/dashboard.model';
import { ActivateModalComponent } from '../activate-modal/activate-modal.component';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, ActivateModalComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  dashboard: DashboardResponse | null = null;
  loading = true;
  error = '';
  isModalOpen = false;

  constructor(private studentService: StudentService) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading = true;
    this.error = '';
    this.studentService.getDashboard(environment.studentId).subscribe({
      next: (data) => {
        this.dashboard = data;
        this.loading = false;
      },
      error: () => {
        this.error = 'Could not load dashboard. Please make sure the backend is running.';
        this.loading = false;
      }
    });
  }

  openModal(): void {
    if (this.dashboard?.activated) return;
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
  }

  onActivated(): void {
    this.isModalOpen = false;
    // Re-fetch from the API so the dashboard reflects the real persisted state,
    // rather than optimistically flipping a local flag.
    this.loadDashboard();
  }
}
