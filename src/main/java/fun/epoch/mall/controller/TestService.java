package fun.epoch.mall.controller;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import fun.epoch.utils.ftp.FTPUploader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class TestService extends FTPUploader {
    public String upload(MultipartFile file) {
        try {
            File localFile = Files.createTempFile("", "-" + file.getOriginalFilename()).toFile();
            file.transferTo(localFile);
            if (upload(localFile)) {
                return String.format("<img src='http://file.epoch.fun/mall/%s'><br>文件上传成功：%s", localFile.getName(),localFile.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.format("文件[%s]上传失败", file.getOriginalFilename());
    }
}
