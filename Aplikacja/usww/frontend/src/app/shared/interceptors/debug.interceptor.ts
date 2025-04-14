import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable()
export class DebugInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log(`HTTP Request: ${req.method} ${req.url}`);

    return next.handle(req).pipe(
      tap(
        event => {
          if (event.type === 0) { // HttpEventType.Sent
            console.log(`HTTP Response: ${req.method} ${req.url}`);
          }
        },
        error => {
          console.error(`HTTP Error for ${req.url}:`, error);
        }
      )
    );
  }
}
