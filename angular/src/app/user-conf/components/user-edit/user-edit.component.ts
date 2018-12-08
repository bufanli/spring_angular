import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from '../../entities/user';
import { Permission } from '../../entities/permission';
import { HttpResponse} from '../../../common/entities/http-response';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.css']
})
export class UserEditComponent implements OnInit {

  public currentUserPermission: Permission = null;
  public currentUser: User = null;
  private getUserDetailedInfosUrl = 'getUserDetailedInfos';  // URL to get user's permission

  constructor(private activeModal: NgbActiveModal,
              private http: HttpClient) {
  }

  ngOnInit() {
    this.getUserPermission().subscribe(httpResponse =>
      this.getUserPermissionNotification(httpResponse));
  }

  getUserPermission(): Observable<HttpResponse> {
    return this.http.get<HttpResponse>(this.getUserDetailedInfosUrl);
  }
  getUserPermissionNotification(httpResponse: HttpResponse): void {
    // get user permission

  }

  close() {
    this.activeModal.close();
  }

}
