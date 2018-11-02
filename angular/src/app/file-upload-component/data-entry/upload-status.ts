export class UploadStatus {
  // 上传失败/上传成功
  public status: string;
  // 上传失败原因
  public reason: string;
  // constructor
  constructor(status: string, reason: string) {
    this.status = status;
    this.reason = reason;
  }
}
