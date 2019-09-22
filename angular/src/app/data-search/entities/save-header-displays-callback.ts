import { HttpResponse } from 'src/app/common/entities/http-response';

export interface SaveHeaderDisplaysCallback {
  callbackOnEndSaveHeaderDisplays(httpResponse: HttpResponse);
}
