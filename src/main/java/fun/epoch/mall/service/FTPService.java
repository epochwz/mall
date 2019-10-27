package fun.epoch.mall.service;

import fun.epoch.mall.utils.ftp.FTPUploader;
import fun.epoch.mall.utils.response.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static fun.epoch.mall.common.Constant.SettingKeys.IMAGE_HOST;
import static fun.epoch.mall.common.Constant.settings;
import static fun.epoch.mall.utils.response.ResponseCode.INTERNAL_SERVER_ERROR;

@Service
@Slf4j
public class FTPService extends FTPUploader {
    public String imageHost = settings.get(IMAGE_HOST);

    public ServerResponse<String> upload(String remotePath, MultipartFile file) {
        File localFile = createTempFile(file);
        if (localFile != null) {
            if (upload(remotePath, localFile)) {
                String url = String.format("%s%s/%s", imageHost, remotePath, localFile.getName());
                return ServerResponse.success(url);
            } else {
                return ServerResponse.error(INTERNAL_SERVER_ERROR, "文件上传失败");
            }
        }
        return ServerResponse.error(INTERNAL_SERVER_ERROR, "创建临时文件失败");
    }

    public File createTempFile(MultipartFile file) {
        try {
            File localFile = Files.createTempFile("", "-" + file.getOriginalFilename()).toFile();
            file.transferTo(localFile);
            return localFile;
        } catch (IOException e) {
            log.error("从网络文件[{}]中创建临时文件失败", file.getOriginalFilename(), e);
        }
        return null;
    }

    public String uploadWithUrl(String remotePath, File file) {
        if (upload(remotePath, file)) {
            return imageHost + remotePath + "/" + file.getName();
        }
        return null;
    }
}
