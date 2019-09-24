import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-modal-dialog',
  templateUrl: './modal-dialog.component.html',
  styleUrls: ['./modal-dialog.component.css']
})
export class ModalDialogComponent implements OnInit {

  public dialogTitle: string = null;
  public dialogBody: string = null;
  public dialogType: string = null;
  // if same component call multiple show dialog function,
  // callbac method must know who is the caller , otherwise
  // it can not behave correctly.
  private sourceID: string = null;
  // event notifier
  @Output() notifier: EventEmitter<string> = new EventEmitter<string>();
  constructor(private activeModal: NgbActiveModal) { }

  public setTitle(title: string): void {
    this.dialogTitle = title;
  }
  public setBody(body: string): void {
    this.dialogBody = body;
  }
  // type include success, danger,info,warning
  public setType(type: string): void {
    this.dialogType = type;
  }
  // set source id
  public setSourceID(sourceID: string): void {
    this.sourceID = sourceID;
  }

  ngOnInit() {
  }

  // press ok button
  public onOK(): void {
    this.activeModal.close();
    this.notifier.emit(this.sourceID);
  }
  public close(): void {
    this.activeModal.close();
  }
}
