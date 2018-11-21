import { MainModule } from './main.module';

describe('AppRoutingModule', () => {
  let appRoutingModule: MainModule;

  beforeEach(() => {
    appRoutingModule = new MainModule();
  });

  it('should create an instance', () => {
    expect(appRoutingModule).toBeTruthy();
  });
});
