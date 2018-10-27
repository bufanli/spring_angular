export class Header {
  field: string;
  title: string;
  visible: boolean;
  constructor(field: string, title: string, visible = true) {
    this.field = field;
    this.title = title;
    this.visible = visible;
  }
}
