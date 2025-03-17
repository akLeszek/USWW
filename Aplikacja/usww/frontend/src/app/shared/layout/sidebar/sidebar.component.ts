import {Component, Input, OnInit} from '@angular/core';
import {RouterModule} from '@angular/router';
import {AuthService} from '../../../auth/services/auth.service';
import {NgClass} from '@angular/common';

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
  imports: [RouterModule, NgClass],
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
      // children: [
      //   {
      //     title: 'Nowe zgłoszenie',
      //     route: '/tickets/new'
      //   },
      //   {
      //     title: 'Aktywne zgłoszenia',
      //     route: '/tickets/active'
      //   },
      //   {
      //     title: 'Historia zgłoszeń',
      //     route: '/tickets/history'
      //   }
      // ]
    },
    {
      title: 'Administracja',
      icon: 'bi-gear',
      roles: ['ADMIN'],
      // children: [
      //   {
      //     title: 'Użytkownicy',
      //     route: '/admin/users'
      //   },
      //   {
      //     title: 'Kategorie zgłoszeń',
      //     route: '/admin/categories'
      //   },
      //   {
      //     title: 'Statusy zgłoszeń',
      //     route: '/admin/statuses'
      //   }
      // ]
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
