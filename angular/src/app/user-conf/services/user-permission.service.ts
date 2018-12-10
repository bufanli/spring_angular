import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserEditComponent } from '../components/user-edit/user-edit.component';
import { Permission } from '../entities/permission';
import { HttpResponse } from '../../common/entities/http-response';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UserPermissionService {

  private getUserDetailedInfosUrl = 'getUserDetailedInfos';  // URL to get user's permission
  private userDetailedInfoComponent: UserEditComponent = null;

  constructor(private http: HttpClient) { }

  public getUserDetailedInfo(sourceComponent: UserEditComponent, userID: string): void {
    this.userDetailedInfoComponent = sourceComponent;
    this.getUserDetailedInfoImpl(userID).subscribe(
      httpResponse => this.getUserDetailedInfoNotification(httpResponse));
  }
  public getUserDetailedInfoImpl(userID: string): Observable<HttpResponse> {
    return this.http.get<HttpResponse>(`${this.getUserDetailedInfosUrl}/${userID}`);
  }

  public getUserDetailedInfoNotification(httpResponse: HttpResponse) {
    // set permission to component
    const permission: Permission = new Permission();
    // this.userDetailedInfoComponent.setUserPermission(permission);
  }
}
