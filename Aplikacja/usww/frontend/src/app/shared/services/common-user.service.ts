import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface UserBasicInfo {
  id: number;
  login: string;
  forename: string;
  surname: string;
}

@Injectable({
  providedIn: 'root'
})
export class CommonUserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) { }

  getUserBasicInfo(userId: number): Observable<UserBasicInfo> {
    return this.http.get<UserBasicInfo>(`${this.apiUrl}/${userId}/basic-info`);
  }
}
