import { UserConfModule } from './user-conf.module';

describe('UserConfModule', () => {
  let userConfModule: UserConfModule;

  beforeEach(() => {
    userConfModule = new UserConfModule();
  });

  it('should create an instance', () => {
    expect(userConfModule).toBeTruthy();
  });
});
