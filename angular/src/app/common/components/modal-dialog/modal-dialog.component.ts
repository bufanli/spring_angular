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
  // event notifier
  @Output() notifier: EventEmitter<string> = new EventEmitter<string>();
  constructor(private activeModal: NgbActiveModal) { }

  ngOnInit() {
  }

  // close modal dialog
  public close(): void {
    this.activeModal.close();
    this.notifier.emit('close');
  }

}
