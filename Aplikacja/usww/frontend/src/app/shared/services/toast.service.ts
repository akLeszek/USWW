import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private toasts: { message: string, type: 'success' | 'error' | 'info' | 'warning' }[] = [];

  showSuccess(message: string): void {
    this.toasts.push({message, type: 'success'});
    this.showAlert(message, 'success');
  }

  showError(message: string): void {
    this.toasts.push({message, type: 'error'});
    this.showAlert(message, 'error');
  }

  showInfo(message: string): void {
    this.toasts.push({message, type: 'info'});
    this.showAlert(message, 'info');
  }

  showWarning(message: string): void {
    this.toasts.push({message, type: 'warning'});
    this.showAlert(message, 'warning');
  }

  private showAlert(message: string, type: string): void {
    const alertClass = this.getAlertClass(type);
    const alertDiv = document.createElement('div');
    alertDiv.className = `toast-notification ${alertClass}`;
    alertDiv.innerText = message;

    document.body.appendChild(alertDiv);

    setTimeout(() => {
      alertDiv.classList.add('showing');
      setTimeout(() => {
        alertDiv.classList.remove('showing');
        setTimeout(() => {
          document.body.removeChild(alertDiv);
        }, 300);
      }, 3000);
    }, 100);
  }

  private getAlertClass(type: string): string {
    switch (type) {
      case 'success':
        return 'bg-success text-white';
      case 'error':
        return 'bg-danger text-white';
      case 'warning':
        return 'bg-warning text-dark';
      case 'info':
      default:
        return 'bg-info text-white';
    }
  }
}
