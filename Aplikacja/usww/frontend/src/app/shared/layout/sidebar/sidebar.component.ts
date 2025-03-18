import {Component, Input, OnInit} from '@angular/core';
import {RouterModule} from '@angular/router';
import {AuthService} from '../../../auth/services/auth.service';
import {NgClass, NgForOf, NgIf} from '@angular/common';

interface MenuItem {
  title: string;
  icon: string;
  route?: string;
  children?: MenuItem[];
  expanded?: boolean;
  roles?: string[];
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterModule, NgClass, NgForOf, NgIf],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  @Input() collapsed = false;
  userRole: string = '';

  menuItems: MenuItem[] = [
    {
      title: 'Dashboard',
      icon: 'bi-speedometer2',
      route: '/dashboard'
    },
    {
      title: 'Zgłoszenia',
      icon: 'bi-ticket-perforated',
      children: [
        {
          title: 'Lista zgłoszeń',
          route: '/tickets',
          icon: ''
        },
        {
          title: 'Nowe zgłoszenie',
          route: '/tickets/new',
          icon: ''
        }
      ]
    },
    {
      title: 'Administracja',
      icon: 'bi-gear',
      roles: ['ADMIN'],
      children: [
        {
          title: 'Użytkownicy',
          route: '/admin/users',
          icon: ''
        },
        {
          title: 'Kategorie zgłoszeń',
          route: '/admin/categories',
          icon: ''
        },
        {
          title: 'Statusy zgłoszeń',
          route: '/admin/statuses',
          icon: ''
        }
      ]
    }
  ];

  constructor(private authService: AuthService) {
  }

  ngOnInit() {
    this.userRole = 'ADMIN';
  }

  toggleMenuItem(item: MenuItem) {
    item.expanded = !item.expanded;
  }

  isMenuItemVisible(item: MenuItem): boolean {
    if (!item.roles) return true;
    return item.roles.includes(this.userRole);
  }
}
