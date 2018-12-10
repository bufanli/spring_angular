import { Component, OnInit, ViewChild } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from '../../entities/user';
import { Permission } from '../../entities/permission';
import { UserPermissionComponent } from '../user-permission/user-permission.component';
import { UserBasicInfoComponent } from '../user-basic-info/user-basic-info.component';
import { UserPermissionService } from '../../services/user-permission.service';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.css']
})
export class UserEditComponent implements OnInit {

  public currentUserPermission: Permission = null;
  public currentUser: User = null;

  @ViewChild('userPermission')
  private userPermission: UserPermissionComponent;

  @ViewChild('userBasicInfo')
  private userBasicInfo: UserBasicInfoComponent;

  constructor(private activeModal: NgbActiveModal,
    private permissionService: UserPermissionService) {
  }

  ngOnInit() {
    this.permissionService.getUserDetailedInfo(this, this.currentUser['userID']);
  }

  close() {
    this.activeModal.close();
  }

  setUserPermission(userPermission: Permission) {
    this.currentUserPermission = userPermission;
  }
}
