import { Component, OnInit } from '@angular/core';
import { AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AuthService } from '../auth/services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterModule, NgClass, NgFor, NgIf, NgbModule, AsyncPipe],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  user: any = null;
  loading = true;
  statistics = {
    activeTickets: 0,
    closedTickets: 0,
    pendingTickets: 0,
    totalTickets: 0
  };

  recentTickets: any[] = [];

  constructor(private authService: AuthService) {}

  ngOnInit() {
    // Tutaj w rzeczywistej aplikacji pobierałbyś dane z API
    this.user = this.authService.currentUserValue;

    // Symulacja opóźnienia ładowania danych
    setTimeout(() => {
      this.statistics = {
        activeTickets: 5,
        closedTickets: 10,
        pendingTickets: 2,
        totalTickets: 17
      };

      this.recentTickets = [
        { id: 1, title: 'Problem z logowaniem', status: 'W trakcie', category: 'Logowanie', created: '2024-02-20' },
        { id: 2, title: 'Brak dostępu do kursu', status: 'Nowe', category: 'Kursy', created: '2024-02-19' },
        { id: 3, title: 'Zmiana danych osobowych', status: 'Zamknięte', category: 'Dane osobowe', created: '2024-02-18' }
      ];

      this.loading = false;
    }, 1000);
  }
}
