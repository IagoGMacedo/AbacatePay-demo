import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./components/payment-form/payment-form')
        .then(m => m.PaymentFormComponent)
  },
  {
    path: '**',
    redirectTo: ''
  }
];
