package fun.epoch.mall.service;

import fun.epoch.mall.entity.User;
import fun.epoch.mall.utils.response.ServerResponse;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public ServerResponse<Integer> register(User user) {
        return null;
    }

    public ServerResponse accountVerify(String account, String type) {
        return null;
    }
}
