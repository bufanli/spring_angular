package com.example.eurasia.service.Data;

import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

//@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("UploadFileServiceImpl")
@Component
public class UploadFileServiceImpl implements IUploadFileService {

    @Autowired
    private ImportExcelByUserMode importExcelByUserMode;
    @Autowired
    private ImportExcelByEventMode importExcelByEventMode;

    @Override
    public ResponseResult batchUpload(File uploadDir, MultipartFile[] files) throws Exception {
        ResponseResult responseResult;
        String fileName = null;
        int fileNumber = files.length;
        StringBuffer responseMsg = new StringBuffer();
        StringBuffer responseOK = new StringBuffer();
        StringBuffer responseNG = new StringBuffer();
        int fileOKNum = 0;
        int fileNGNum = 0;
        Slf4jLogUtil.get().info("文件上传目录:{}",uploadDir.getPath());

        //遍历文件数组
        for (int i=0; i<files.length; i++) {
            //上传文件名
            fileName = files[i].getOriginalFilename();

            Slf4jLogUtil.get().info("第{}/{}个文件开始上传,文件名:{}",(i+1),fileNumber,fileName);

            try {
                //需要自定义文件名的情况
                //String suffix = files.getOriginalFilename().substring(files.getOriginalFilename().lastIndexOf("."));
                //String fileName = UUID.randomUUID() + suffix;
                //服务器端保存端文件对象
                File serverFile = new File(uploadDir.getPath() + "/" + fileName);
                if (!serverFile.exists()) {
                    Slf4jLogUtil.get().info("文件名:{}存在的话，则覆盖。",fileName);
                }
                //将上传的文件写入到服务器端的文件内
                files[i].transferTo(serverFile);
            } catch (IOException e) {
                e.printStackTrace();
                fileNGNum++;
                responseNG.append(fileName +":上传IO异常" + DataService.BR);
                Slf4jLogUtil.get().error("第{}/{}个文件上传IO异常,文件名:{}",(i+1),fileNumber,fileName);
                continue;
            } catch (IllegalStateException e) {
                e.printStackTrace();
                fileNGNum++;
                responseNG.append(fileName + ":上传IllegalState异常" + DataService.BR);
                Slf4jLogUtil.get().error("第{}/{}个文件上传IllegalState异常,文件名:{}",(i+1),fileNumber,fileName);
                continue;
            }

            fileOKNum++;
            Slf4jLogUtil.get().info("第{}/{}个文件上传OK结束,文件名:{}",(i+1),fileNumber,fileName);

            responseOK.append(fileName + DataService.BR);
        }

        if (fileNGNum == 0) {
            responseMsg.append(fileOKNum + "个文件导入成功。");
            responseOK.delete((responseOK.length() - DataService.BR.length()),responseOK.length());
            responseResult = new ResponseResultUtil().success(ResponseCodeEnum.UPLOAD_FILE_SUCCESS.getCode(), responseMsg.toString(), responseOK.toString());
        } else if (fileOKNum == 0 && fileNGNum != 0) {
            responseMsg.append(fileNGNum + "个文件导入失败。");
            responseNG.delete((responseNG.length() - DataService.BR.length()),responseNG.length());
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.UPLOAD_FILE_FAILED.getCode(), responseMsg.toString(), responseNG.toString());
        } else {
            responseMsg.append(fileOKNum + "个文件导入成功," + fileNGNum + "个文件导入失败。");
            responseOK.delete((responseOK.length() - DataService.BR.length()),responseOK.length());
            responseNG.delete((responseNG.length() - DataService.BR.length()),responseNG.length());

            String[] responseOKNGArr = new String[2];
            responseOKNGArr[0] = responseOK.toString();
            responseOKNGArr[1] = responseNG.toString();

            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.UPLOAD_FILE_FAILED.getCode(), responseMsg.toString(), responseOKNGArr);
        }
        return responseResult;
    }

    @Override
    public ResponseResult readFile(File fileDir) throws Exception {
        ResponseResult responseResult;
        String fileName = null;
        int fileNumber = 0;
        StringBuffer responseMsg = new StringBuffer();
        StringBuffer responseOK = new StringBuffer();
        StringBuffer responseNG = new StringBuffer();
        int fileOKNum = 0;
        int fileNGNum = 0;
        Slf4jLogUtil.get().info("文件读取目录:{}",fileDir);

        //遍历文件数组
        File[] files = fileDir.listFiles();
        fileNumber = files.length;
        for (int i=0; i<fileNumber; i++) {
            if (files[i].isDirectory()) {
                //Nothing to do
                Slf4jLogUtil.get().info("第{}/{}，是个目录,跳过。",(i+1),fileNumber);
                continue;
            }

            String responseRead = null;
            if (files[i].isFile()) {
                //读取文件名
                fileName = files[i].getName();

                Slf4jLogUtil.get().info("第{}/{}个文件开始读取,文件名:{}",(i+1),fileNumber,fileName);

                try {
                    if (ImportExcelUtils.isExcelFileValidata(files[i]) == true) {
                        //responseRead = importExcelByUserMode.readExcelFile(files[i]);
                        responseRead = importExcelByEventMode.readExcelFile(files[i]);
                    } else {
                        fileNGNum++;
                        responseNG.append(fileName +": 文件格式有问题" + DataService.BR);
                        Slf4jLogUtil.get().error("第{}/{}个文件格式有问题,文件名:{}",(i+1),fileNumber,fileName);
                        continue;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    fileNGNum++;
                    responseNG.append(fileName +": 读取IO异常" + DataService.BR);
                    Slf4jLogUtil.get().error("第{}/{}个文件读取IO异常,文件名:{}",(i+1),fileNumber,fileName);
                    continue;
                } catch (Exception e) {
                    e.printStackTrace();
                    fileNGNum++;
                    responseNG.append(fileName +": 读取或者保存到数据库异常" + DataService.BR);
                    Slf4jLogUtil.get().error("第{}/{}个文件读取异常,文件名:{}",(i+1),fileNumber,fileName);
                    continue;
                }

            }

            fileOKNum++;
            Slf4jLogUtil.get().info("第{}/{}个文件读取OK结束,文件名:{}",(i+1),fileNumber,fileName);

            responseOK.append(fileName + ":" + responseRead + DataService.BR);
        }

        if (fileNGNum == 0) {
            responseMsg.append(fileOKNum + "个文件读取成功。");
            responseOK.delete((responseOK.length() - DataService.BR.length()),responseOK.length());
            responseResult = new ResponseResultUtil().success(ResponseCodeEnum.READ_UPLOADED_FILE_SUCCESS.getCode(), responseMsg.toString(), responseOK.toString());
        } else if (fileOKNum == 0 && fileNGNum != 0) {
            responseMsg.append(fileNGNum + "个文件读取失败。");
            responseNG.delete((responseNG.length() - DataService.BR.length()),responseNG.length());
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.READ_UPLOADED_FILE_FAILED.getCode(), responseMsg.toString(), responseNG.toString());
        } else {
            responseMsg.append(fileOKNum + "个文件读取成功," + fileNGNum + "个文件读取失败。");
            responseOK.delete((responseOK.length() - DataService.BR.length()),responseOK.length());
            responseNG.delete((responseNG.length() - DataService.BR.length()),responseNG.length());

            String[] responseNGArr = new String[2];
            responseNGArr[0] = responseOK.toString();
            responseNGArr[1] = responseNG.toString();

            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.READ_UPLOADED_FILE_FAILED.getCode(), responseMsg.toString(), responseNGArr);
        }
        return responseResult;
    }

}
