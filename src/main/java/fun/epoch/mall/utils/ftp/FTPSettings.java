package fun.epoch.mall.utils.ftp;

import fun.epoch.mall.utils.Settings;
import lombok.Getter;
import org.apache.commons.net.ftp.FTP;

/**
 * FTP 配置对象
 */
@Getter
public class FTPSettings extends Settings {
    private String ip;
    private int port;
    private String username;
    private String password;

    public FTPSettings() {
        this("ftp.properties");
    }

    public FTPSettings(String resource) {
        if (this.load(resource)) {
            this.ip = get("ip");
            this.port = getInt("port", FTP.DEFAULT_PORT);
            this.username = get("username");
            this.password = get("password");
        } else {
            throw new RuntimeException(String.format("FTP 配置文件 [%s] 加载失败！", resource));
        }
    }
}
