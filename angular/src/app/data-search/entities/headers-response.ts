import { Header } from '../../common/entities/header';

export class HeadersResponse {
  code: string;
  message: string;
  data: Header[];
}
