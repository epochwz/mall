package fun.epoch.utils.ftp;

import org.apache.commons.net.ftp.FTP;
import fun.epoch.utils.Settings;

public class FTPSettings extends Settings {
    private static final String DEFAULT_RESOURCE = "ftp.properties";
    private String ip;
    private int port;
    private String username;
    private String password;

    public FTPSettings() {
        this(DEFAULT_RESOURCE);
    }

    public FTPSettings(String resource) {
        this.load(resource);

        this.ip = get("ip");
        this.port = getInt("port", FTP.DEFAULT_PORT);
        this.username = get("username");
        this.password = get("password");
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
