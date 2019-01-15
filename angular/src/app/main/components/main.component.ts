import { Component, OnInit } from '@angular/core';
import { CurrentUserContainerService } from 'src/app/common/services/current-user-container.service';
import { UserAccessAuthorities } from 'src/app/user-conf/entities/user-access-authorities';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {
  // show data upload menu or not
  public isShowUploadMenu = false;
  public isShowUserConfMenu = false;
  constructor(private currentUserContainer: CurrentUserContainerService) {
  }
  // init access authorities
  private initAccessAuthorities() {
    const currentUserAccessAuthorities: UserAccessAuthorities
      = this.currentUserContainer.getCurrentUserAccessAuthorities();
    this.isShowUploadMenu = currentUserAccessAuthorities['上传可否'];
    if (this.currentUserContainer.getOpenID() === 'sinoshuju_admin') {
      this.isShowUserConfMenu = true;
    } else {
      this.isShowUserConfMenu = false;
    }
  }
  ngOnInit() {
    // init access authorities
    this.initAccessAuthorities();
  }
}
