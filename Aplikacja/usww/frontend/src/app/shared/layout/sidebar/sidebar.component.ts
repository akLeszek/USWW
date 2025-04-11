import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {RouterModule} from '@angular/router';
import {AuthService} from '../../../auth/services/auth.service';
import {NgClass, NgForOf, NgIf} from '@angular/common';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';

interface MenuItem {
  title: string;
  icon?: string;
  route?: string;
  children?: MenuItem[];
  expanded?: boolean;
  visible?: boolean;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterModule, NgClass, NgForOf, NgIf],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit, OnDestroy {
  @Input() collapsed = false;
  menuItems: MenuItem[] = [];
  private destroy$ = new Subject<void>();

  constructor(private authService: AuthService) {
  }

  ngOnInit(): void {
    this.updateMenuVisibility();

    this.authService.currentUser
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.updateMenuVisibility();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  updateMenuVisibility(): void {
    this.menuItems = [
      {
        title: 'Dashboard',
        icon: 'bi-speedometer2',
        route: '/dashboard',
        visible: true
      },
      {
        title: 'Zgłoszenia',
        icon: 'bi-ticket-perforated',
        visible: true,
        children: [
          {
            title: 'Lista zgłoszeń',
            route: '/tickets',
            visible: true
          },
          {
            title: 'Nowe zgłoszenie',
            route: '/tickets/new',
            visible: this.authService.hasPermission('Ticket', 'CREATE')
          },
          {
            title: 'Nieprzypisane zgłoszenia',
            route: '/tickets/unassigned',
            visible: this.authService.isAdmin() || this.authService.isOperator()
          }
        ]
      },
      {
        title: 'Administracja',
        icon: 'bi-gear',
        visible: this.authService.isAdmin(),
        children: [
          {
            title: 'Użytkownicy',
            route: '/admin/users',
            visible: this.authService.isAdmin()
          },
          {
            title: 'Kategorie zgłoszeń',
            route: '/admin/categories',
            visible: this.authService.isAdmin()
          },
          {
            title: 'Statusy zgłoszeń',
            route: '/admin/statuses',
            visible: this.authService.isAdmin()
          },
          {
            title: 'Priorytety zgłoszeń',
            route: '/admin/priorities',
            visible: this.authService.isAdmin()
          }
        ]
      },
      {
        title: 'Statystyki',
        icon: 'bi-bar-chart',
        visible: this.authService.isAdmin() || this.authService.isOperator(),
        children: [
          {
            title: 'Raporty ogólne',
            route: '/statistics/general',
            visible: this.authService.isAdmin()
          },
          {
            title: 'Raporty wydajności',
            route: '/statistics/performance',
            visible: this.authService.isAdmin() || this.authService.isOperator()
          }
        ]
      },
      {
        title: 'Ustawienia',
        icon: 'bi-gear-fill',
        route: '/settings',
        visible: true
      }
    ];
  }

  toggleMenuItem(item: MenuItem): void {
    item.expanded = !item.expanded;
  }

  isMenuItemVisible(item: MenuItem): boolean {
    return item.visible !== false;
  }

  hasVisibleChildren(item: MenuItem): boolean {
    return item.children?.some(child => child.visible !== false) ?? false;
  }
}
