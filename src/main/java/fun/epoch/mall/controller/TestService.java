package fun.epoch.mall.controller;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TestService {
    public String upload(MultipartFile file) {
        return "文件由 service 上传成功：" + file.getOriginalFilename();
    }
}
