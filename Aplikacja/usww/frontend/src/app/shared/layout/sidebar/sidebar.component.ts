import {Component, Input, OnDestroy, OnInit, Output, EventEmitter} from '@angular/core';
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
  @Input() mobileOpen = false;
  @Output() mobileToggle = new EventEmitter<void>();

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
        expanded: true,
        children: [
          {
            title: 'Lista zgłoszeń',
            route: '/tickets',
            visible: true
          },
          {
            title: 'Nowe zgłoszenie',
            route: '/tickets/new',
            visible: this.authService.isStudent()
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
        expanded: true,
        children: [
          {
            title: 'Użytkownicy',
            route: '/admin/users',
            visible: this.authService.isAdmin()
          },
          {
            title: 'Kategorie zgłoszeń',
            route: '/admin/dictionaries/categories',
            visible: this.authService.isAdmin()
          },
          {
            title: 'Statusy zgłoszeń',
            route: '/admin/dictionaries/statuses',
            visible: this.authService.isAdmin()
          },
          {
            title: 'Priorytety zgłoszeń',
            route: '/admin/dictionaries/priorities',
            visible: this.authService.isAdmin()
          }
        ]
      }
    ];
  }

  toggleMenuItem(item: MenuItem): void {
  }

  isMenuItemVisible(item: MenuItem): boolean {
    return item.visible !== false;
  }

  hasVisibleChildren(item: MenuItem): boolean {
    return item.children?.some(child => child.visible !== false) ?? false;
  }

  onMenuItemClick(): void {
    if (window.innerWidth <= 768) {
      this.mobileToggle.emit();
    }
  }
}
