// Mirrors backend DashboardResponse DTO
export interface DashboardResponse {
  studentId: number;
  schoolName: string;
  studentName: string;
  classSection: string;
  profilePhotoUrl: string;
  annualFee: number;
  interestRatePercent: number;
  activated: boolean;
}

// Mirrors backend ActivationRequest DTO
export interface ActivationRequest {
  phoneNumber: string;
  panNumber: string;
  nameAsInPan: string;
  email: string;
}

// Mirrors backend ActivationResponse DTO
export interface ActivationResponse {
  success: boolean;
  message: string;
  activated: boolean;
}

// Shape of the backend's field-level validation error response
export interface ApiValidationError {
  timestamp: string;
  success: boolean;
  message: string;
  errors?: { [field: string]: string };
}
