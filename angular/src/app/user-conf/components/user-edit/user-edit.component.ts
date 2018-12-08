import { Component, OnInit, ViewChild } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from '../../entities/user';
import { Permission } from '../../entities/permission';
import { HttpResponse} from '../../../common/entities/http-response';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserPermissionComponent } from '../user-permission/user-permission.component';
import { UserBasicInfoComponent } from '../user-basic-info/user-basic-info.component';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.css']
})
export class UserEditComponent implements OnInit {

  public currentUserPermission: Permission = null;
  public currentUser: User = null;
  private getUserDetailedInfosUrl = 'getUserDetailedInfos';  // URL to get user's permission

  @ViewChild('userPermission')
  userPermission: UserPermissionComponent;

  @ViewChild('userBasicInfo')
  userBasicInfo: UserBasicInfoComponent;

  constructor(private activeModal: NgbActiveModal,
              private http: HttpClient) {
  }

  ngOnInit() {
    this.getUserPermission().subscribe(httpResponse =>
      this.getUserPermissionNotification(httpResponse));
  }

  getUserPermission(): Observable<HttpResponse> {
    const url = `${this.getUserDetailedInfosUrl}/${this.currentUser['userID']}`;
    return this.http.get<HttpResponse>(url);
  }
  getUserPermissionNotification(httpResponse: HttpResponse): void {
    // get user permission
  }

  close() {
    this.activeModal.close();
  }

  updateUserInfo(): void {
    //
  }
}
