import { Component, inject, signal, OnDestroy } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import QRCode from 'qrcode';
import { PaymentService, CreatePixResponse } from '../../services/payment.service';

const POLL_INTERVAL_MS = 5000;

@Component({
  selector: 'app-payment-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './payment-form.html'
})
export class PaymentFormComponent implements OnDestroy {
  private readonly paymentService = inject(PaymentService);
  private countdownInterval: ReturnType<typeof setInterval> | null = null;
  private pollingInterval: ReturnType<typeof setInterval> | null = null;

  readonly isLoading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly pixResult = signal<CreatePixResponse | null>(null);
  readonly qrCodeDataUrl = signal<string | null>(null);
  readonly countdown = signal<string | null>(null);
  readonly expired = signal(false);
  readonly paid = signal(false);
  readonly paidAmount = signal<string | null>(null);

  readonly form = new FormGroup({
    name:   new FormControl('', [Validators.required, Validators.minLength(2)]),
    email:  new FormControl('', [Validators.required, Validators.email]),
    phone:  new FormControl('', [Validators.required]),
    cpf:    new FormControl('', [Validators.required, Validators.pattern(/^\d{11}$/)]),
    amount: new FormControl('', [Validators.required, Validators.pattern(/^\d+([.,]\d{1,2})?$/)]),
  });

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const rawAmount = this.form.getRawValue().amount!.replace(',', '.');
    const amountInCents = Math.round(parseFloat(rawAmount) * 100);

    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.pixResult.set(null);
    this.qrCodeDataUrl.set(null);
    this.paid.set(false);
    this.paidAmount.set(null);
    this.stopCountdown();
    this.stopPolling();

    const { name, email, phone, cpf } = this.form.getRawValue();
    this.paidAmount.set(this.form.getRawValue().amount!);

    this.paymentService.createPix({ name: name!, email: email!, phone: phone!, cpf: cpf!, amount: amountInCents }).subscribe({
      next: async (response) => {
        this.pixResult.set(response);
        try {
          const dataUrl = await QRCode.toDataURL(response.qrCode, { width: 256, margin: 2 });
          this.qrCodeDataUrl.set(dataUrl);
        } catch {
          // QR code generation failed silently — copia e cola still works
        }
        this.startCountdown(response.expiresAt);
        this.startPolling(response.id);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.errorMessage.set(err?.error?.message ?? 'Erro ao criar pagamento PIX. Verifique os dados e tente novamente.');
        this.isLoading.set(false);
      }
    });
  }

  private startPolling(externalId: string): void {
    this.pollingInterval = setInterval(() => {
      this.paymentService.getPaymentStatus(externalId).subscribe({
        next: (response: { status: string }) => {
          if (response.status === 'PAID') {
            this.paid.set(true);
            this.stopPolling();
            this.stopCountdown();
          }
        },
        error: () => { /* ignora erros de polling silenciosamente */ }
      });
    }, POLL_INTERVAL_MS);
  }

  private stopPolling(): void {
    if (this.pollingInterval !== null) {
      clearInterval(this.pollingInterval);
      this.pollingInterval = null;
    }
  }

  private startCountdown(expiresAt: string): void {
    this.expired.set(false);
    const expiryTime = new Date(expiresAt).getTime();

    const tick = () => {
      const remaining = expiryTime - Date.now();
      if (remaining <= 0) {
        this.countdown.set('00:00');
        this.expired.set(true);
        this.stopCountdown();
        this.stopPolling();
        return;
      }
      const totalSeconds = Math.floor(remaining / 1000);
      const minutes = Math.floor(totalSeconds / 60).toString().padStart(2, '0');
      const seconds = (totalSeconds % 60).toString().padStart(2, '0');
      this.countdown.set(`${minutes}:${seconds}`);
    };

    tick();
    this.countdownInterval = setInterval(tick, 1000);
  }

  private stopCountdown(): void {
    if (this.countdownInterval !== null) {
      clearInterval(this.countdownInterval);
      this.countdownInterval = null;
    }
  }

  resetForm(): void {
    this.pixResult.set(null);
    this.qrCodeDataUrl.set(null);
    this.errorMessage.set(null);
    this.countdown.set(null);
    this.expired.set(false);
    this.paid.set(false);
    this.paidAmount.set(null);
    this.stopCountdown();
    this.stopPolling();
    this.form.reset();
  }

  ngOnDestroy(): void {
    this.stopCountdown();
    this.stopPolling();
  }
}
