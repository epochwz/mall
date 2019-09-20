package fun.epoch.mall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/manage/product")
public class TestController {
    @Autowired
    TestService service;

    @ResponseBody
    @RequestMapping("upload.do")
    public String test(MultipartFile file) {
        return service.upload(file);
    }
}
