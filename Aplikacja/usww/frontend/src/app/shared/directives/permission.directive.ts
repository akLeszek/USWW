import {Directive, Input, OnInit, TemplateRef, ViewContainerRef} from '@angular/core';
import {AuthService} from '../../auth/services/auth.service';

@Directive({
  selector: '[appHasPermission]',
  standalone: true
})
export class HasPermissionDirective implements OnInit {
  private hasView = false;

  @Input() set appHasPermission(permission: { resource: string, action: string }) {
    const hasPermission = this.authService.hasPermission(permission.resource, permission.action);

    if (hasPermission && !this.hasView) {
      this.viewContainer.createEmbeddedView(this.templateRef);
      this.hasView = true;
    } else if (!hasPermission && this.hasView) {
      this.viewContainer.clear();
      this.hasView = false;
    }
  }

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private authService: AuthService
  ) {
  }

  ngOnInit() {
  }
}

@Directive({
  selector: '[appHasRole]',
  standalone: true
})
export class HasRoleDirective implements OnInit {
  private hasView = false;

  @Input() set appHasRole(role: string | string[]) {
    const roles = Array.isArray(role) ? role : [role];
    const hasRole = roles.some(r =>
      (r === 'ADMIN' && this.authService.isAdmin()) ||
      (r === 'OPERATOR' && this.authService.isOperator()) ||
      (r === 'STUDENT' && this.authService.isStudent())
    );

    if (hasRole && !this.hasView) {
      this.viewContainer.createEmbeddedView(this.templateRef);
      this.hasView = true;
    } else if (!hasRole && this.hasView) {
      this.viewContainer.clear();
      this.hasView = false;
    }
  }

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private authService: AuthService
  ) {
  }

  ngOnInit() {
  }
}
