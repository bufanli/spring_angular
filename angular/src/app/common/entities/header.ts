export class Header {
  field: string;
  title: string;
  visible: boolean;
  formatter: Function;
  class: string;
  width: number;
  sortable: boolean;
  order: string;
  align: string;
  constructor(field: string, title: string, visible = true, align = '') {
    this.field = field;
    this.title = title;
    this.visible = visible;
    this.align = align;
  }
}
