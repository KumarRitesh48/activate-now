import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ActivationRequest, ActivationResponse, DashboardResponse } from '../models/dashboard.model';

@Injectable({ providedIn: 'root' })
export class StudentService {
  private readonly baseUrl = `${environment.apiBaseUrl}/api/students`;

  constructor(private http: HttpClient) {}

  getDashboard(studentId: number): Observable<DashboardResponse> {
    return this.http.get<DashboardResponse>(`${this.baseUrl}/${studentId}/dashboard`);
  }

  activate(studentId: number, request: ActivationRequest): Observable<ActivationResponse> {
    return this.http.post<ActivationResponse>(`${this.baseUrl}/${studentId}/activate`, request);
  }
}
