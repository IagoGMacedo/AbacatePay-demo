import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface CreatePixPayload {
  name: string;
  email: string;
  phone: string;
  cpf: string;
  amount: number;
}

export interface CreatePixResponse {
  id: string;
  status: string;
  qrCode: string;
  expiresAt: string;
}

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  createPix(payload: CreatePixPayload): Observable<CreatePixResponse> {
    return this.http.post<CreatePixResponse>(
      `${this.apiUrl}/payment/pix`,
      payload
    );
  }

  getPaymentStatus(externalId: string): Observable<{ status: string }> {
    return this.http.get<{ status: string }>(
      `${this.apiUrl}/payment/pix/${externalId}/status`
    );
  }
}
