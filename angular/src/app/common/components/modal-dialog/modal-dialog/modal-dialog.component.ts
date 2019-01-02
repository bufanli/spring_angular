import { Component, OnInit, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-modal-dialog',
  templateUrl: './modal-dialog.component.html',
  styleUrls: ['./modal-dialog.component.css']
})
export class ModalDialogComponent implements OnInit {

  public dialogTitle: string = null;
  public dialogBody: string = null;
  // event notifier
  @Output() notifyClose: EventEmitter<string> = new EventEmitter<string>();
  constructor() { }

  ngOnInit() {
  }

}
