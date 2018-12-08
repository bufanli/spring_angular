import { Component, OnInit , Input} from '@angular/core';
import { Permission } from '../../entities/permission';

@Component({
  selector: 'app-user-permission',
  templateUrl: './user-permission.component.html',
  styleUrls: ['./user-permission.component.css']
})
export class UserPermissionComponent implements OnInit {

  @Input() currentUserPermission: Permission;
  constructor() { }

  ngOnInit() {
  }

}
