import { HttpResponse } from 'src/app/common/entities/http-response';

export interface SaveColumnDictionaryCallback {
  callbackOnSaveEnd(httpReponse: HttpResponse): void;
}
