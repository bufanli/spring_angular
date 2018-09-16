import { Component, AfterViewInit , ElementRef, Inject} from '@angular/core';

declare var $: any;
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements AfterViewInit {
  title = 'angular-tour-of-heroes';
  el: any;
  greeting(): void {
    $('body').append('<h3>test</h3>');
  }

  constructor(@Inject(ElementRef) elref) {
    this.el = elref.nativeElement;
  }
  ngAfterViewInit() {
  }
}
