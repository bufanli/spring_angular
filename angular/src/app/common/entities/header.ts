export class Header {
  field: string;
  title: string;
  visible: boolean;
  formatter: Function;
  class: string;
  width: number;
  sortable: boolean;
  order: string;
  constructor(field: string, title: string, visible = true) {
    this.field = field;
    this.title = title;
    this.visible = visible;
  }
}
