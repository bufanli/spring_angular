import { Component, OnInit, ViewChild } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { User } from '../../entities/user';
import { UserAccessAuthorities} from '../../entities/user-access-authorities';
import { UserAccessAuthoritiesComponent} from '../user-access-authorities/user-access-authorities.component';
import { UserBasicInfoComponent } from '../user-basic-info/user-basic-info.component';
import { UserPermissionService } from '../../services/user-permission.service';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.css']
})
export class UserEditComponent implements OnInit {

  public currentUserAccessAuthorities: UserAccessAuthorities = null;
  public currentUser: User = null;

  @ViewChild('userPermission')
  private userAccessAuthoritiesComponent: UserAccessAuthoritiesComponent;

  @ViewChild('userBasicInfo')
  private userBasicInfoComponent: UserBasicInfoComponent;

  constructor(private activeModal: NgbActiveModal,
    private permissionService: UserPermissionService) {
  }

  ngOnInit() {
    this.permissionService.getUserDetailedInfo(this, this.currentUser['userID']);
  }

  close() {
    this.activeModal.close();
  }

  // set user permission after user edit component getting user permission
  public setUserAccessAuthorities(userAccessAuthorities: UserAccessAuthorities) {
    this.currentUserAccessAuthorities = userAccessAuthorities;
    this.userAccessAuthoritiesComponent.setCurrentUserAccessAuthorities(userAccessAuthorities);
  }
  // update user info(basic info, permissions, attributes)
  public updateUserInfo() {

  }
}
