import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { UserListComponent } from '../user-list/user-list.component';
import { UserAddComponent } from '../user-add/user-add.component';

@Component({
  selector: 'app-user-conf',
  templateUrl: './user-conf.component.html',
  styleUrls: ['./user-conf.component.css']
})
export class UserConfComponent implements OnInit {

  constructor(private activatedRoute: ActivatedRoute) { }

  // user add active
  public isUserAddActive = false;

  @ViewChild('userList')
  userListComponent: UserListComponent;
  @ViewChild('userAdd')
  userAddComponent: UserAddComponent;

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      const auth = params['auth'];
      if (auth === 'ok') {
        // show user add component
        this.isUserAddActive = true;
        // pass params to user add component
        const openID = params['openid'];
        this.userAddComponent.wechatOK(openID);
      } else if (auth === 'ng') {
        // show user add component
        this.isUserAddActive = true;
        // pass params to user add component
        const reason = params['reason'];
        this.userAddComponent.wechatNG(reason);
      } else {
        // show user list component
        this.isUserAddActive = false;
      }
    });
    // show user list compnent at first
    this.isUserAddActive = false;
  }
}
