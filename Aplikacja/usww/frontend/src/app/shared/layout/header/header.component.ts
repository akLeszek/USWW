import { Component, EventEmitter, Output, OnInit, OnDestroy, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../auth/services/auth.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { UserProfileComponent } from '../../user-profile/user-profile.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
    UserProfileComponent
  ],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit, OnDestroy {
  @Output() toggleSidebar = new EventEmitter<void>();

  userLogin: string | null = null;
  showProfilePanel = false;
  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.userLogin = user ? user.username : null;
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event): void {
    const target = event.target as HTMLElement;
    const isProfileButton = target.closest('.user-button');
    const isProfilePanel = target.closest('.user-profile-panel');

    if (!isProfileButton && !isProfilePanel && this.showProfilePanel) {
      this.showProfilePanel = false;
    }
  }

  onToggleSidebar(): void {
    this.toggleSidebar.emit();
  }

  toggleProfilePanel(): void {
    this.showProfilePanel = !this.showProfilePanel;
  }

  closeProfilePanel(): void {
    this.showProfilePanel = false;
  }

  logout(): void {
    this.showProfilePanel = false;
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: (error) => {
        console.error('Logout error:', error);
        this.router.navigate(['/login']);
      }
    });
  }
}
