import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { StudentService } from '../../services/student.service';
import { ApiValidationError } from '../../models/dashboard.model';

// Phone: +91 followed by exactly 10 digits, e.g. +918329230390
const PHONE_PATTERN = /^\+91[0-9]{10}$/;

// Email: standard address ending specifically in .com, per the challenge's stated rule
const EMAIL_PATTERN = /^[\w.+-]+@[\w-]+(\.[\w-]+)*\.com$/;

// Standard Indian PAN format: 5 letters, 4 digits, 1 letter (e.g. EEAPS6789R)
const PAN_PATTERN = /^[A-Z]{5}[0-9]{4}[A-Z]$/;

@Component({
  selector: 'app-activate-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './activate-modal.component.html',
  styleUrl: './activate-modal.component.scss'
})
export class ActivateModalComponent {
  @Input() studentId!: number;
  @Input() schoolName = '';
  @Input() studentName = '';
  @Input() classSection = '';
  @Input() profilePhotoUrl = '';

  @Output() cancelled = new EventEmitter<void>();
  @Output() activated = new EventEmitter<void>();

  form: FormGroup;
  submitting = false;
  submitError = '';

  constructor(private fb: FormBuilder, private studentService: StudentService) {
    this.form = this.fb.group({
      phoneNumber: ['', [Validators.required, Validators.pattern(PHONE_PATTERN)]],
      panNumber: ['', [Validators.required, Validators.pattern(PAN_PATTERN)]],
      nameAsInPan: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.pattern(EMAIL_PATTERN)]]
    });

    // The PAN field is styled uppercase via CSS, but that's cosmetic only -
    // it doesn't change the actual stored value. Without this, typing lowercase
    // "aaaaa9999a" would LOOK uppercase but fail validation against the pattern,
    // which only matches A-Z. This keeps the real form value in sync with what
    // the user visually sees, so the green tick and the regex agree.
    this.form.get('panNumber')?.valueChanges.subscribe((value: string) => {
      const upper = (value || '').toUpperCase();
      if (value !== upper) {
        this.form.get('panNumber')?.setValue(upper, { emitEvent: false });
      }
    });
  }

  // Helper for the template: true only once a field is both valid and non-empty,
  // used to show the green tick exactly like the Figma (Phone Number, Email).
  isFieldValid(controlName: string): boolean {
    const control = this.form.get(controlName);
    return !!control && control.valid && control.value?.length > 0;
  }

  isFieldInvalid(controlName: string): boolean {
    const control = this.form.get(controlName);
    return !!control && control.invalid && control.touched;
  }

  getErrorMessage(controlName: string): string {
    const control = this.form.get(controlName);
    if (!control || !control.errors) return '';

    if (control.errors['required']) return 'This field is required';
    if (control.errors['pattern']) {
      switch (controlName) {
        case 'phoneNumber':
          return 'Enter a valid phone number: +91 followed by 10 digits';
        case 'email':
          return 'Enter a valid email address (e.g. name@example.com)';
        case 'panNumber':
          return 'Enter a valid PAN (format: AAAAA9999A)';
        default:
          return 'Invalid format';
      }
    }
    if (control.errors['minlength']) return 'Name is too short';
    return 'Invalid value';
  }

  onCancel(): void {
    this.cancelled.emit();
  }

  onSubmit(): void {
    this.submitError = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting = true;
    const payload = {
      phoneNumber: this.form.value.phoneNumber,
      panNumber: this.form.value.panNumber,
      nameAsInPan: this.form.value.nameAsInPan,
      email: this.form.value.email
    };

    this.studentService.activate(this.studentId, payload).subscribe({
      next: () => {
        this.submitting = false;
        this.activated.emit();
      },
      error: (err) => {
        this.submitting = false;
        const apiError: ApiValidationError = err?.error;
        this.submitError = apiError?.message || 'Activation failed. Please try again.';
      }
    });
  }
}
