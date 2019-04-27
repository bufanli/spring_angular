export class CategorySelections {
  private catetory: string = null;
  private selections: string[] = null;
  public setCatetory(catetory: string) {
    this.catetory = catetory;
  }
  public getCategory(): string {
    return this.catetory;
  }
  public setSelections(selections: string[]): void {
    this.selections = this.selections;
  }
  public getSelections(): string[] {
    return this.selections;
  }
  public pushSelection(selection: string): void {
    this.selections.push(selection);
  }
  public constructor(catetory: string) {
    this.catetory = catetory;
    this.selections = [];
  }
}
