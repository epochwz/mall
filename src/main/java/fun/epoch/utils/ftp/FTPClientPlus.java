package fun.epoch.utils.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.apache.commons.net.ftp.FTP.DEFAULT_PORT;
import static fun.epoch.utils.TextUtils.isBlank;

public class FTPClientPlus {
    public static final String ANONYMOUS_USERNAME = "ftp";
    public static final String ANONYMOUS_PASSWORD = "";
    public static final String FILE_ENCODING = "UTF-8";
    public static final int BUFF_SIZE = 2048;

    protected Logger log = LoggerFactory.getLogger(getClass());

    protected String ip;
    protected int port;
    protected String username;
    protected String password;

    protected FTPClient client;

    public FTPClientPlus(FTPSettings settings) {
        this(settings.getIp(), settings.getPort(), settings.getUsername(), settings.getPassword());
    }

    public FTPClientPlus(String ip) {
        this(ip, DEFAULT_PORT);
    }

    public FTPClientPlus(String ip, int port) {
        this(ip, port, ANONYMOUS_USERNAME, ANONYMOUS_PASSWORD);
    }

    public FTPClientPlus(String ip, String username, String password) {
        this(ip, DEFAULT_PORT, username, password);
    }

    public FTPClientPlus(String ip, int port, String username, String password) {
        if (isBlank(ip)) throw new IllegalArgumentException("FTP: ip cannot be empty!");
        if (isBlank(username)) throw new IllegalArgumentException("FTP: username cannot be empty!");
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public boolean loginToDo(FTPTask task) {
        if (client == null) {
            client = new FTPClient();
            client.enterLocalPassiveMode();
            client.setBufferSize(BUFF_SIZE);
            client.setControlEncoding(FILE_ENCODING);
        }
        try {
            return reLogin() && task.execute(client);
        } finally {
            logout();
        }
    }

    public interface FTPTask {
        boolean execute(FTPClient client);
    }

    private boolean reLogin() {
        return logout() && connect() && login();
    }

    private boolean login() {
        if (client.isConnected()) {
            try {
                if (client.login(username, password)) {
                    log.info("[{}] 登录 FTP 服务器 [{}:{}] 成功", username, ip, port);
                    return true;
                }
                log.error("[{}] 登录 FTP 服务器 [{}:{}] 失败", username, ip, port);
            } catch (IOException e) {
                log.error("[{}] 登录 FTP 服务器 [{}:{}] 失败", username, ip, port, e);
            }
        }
        return false;
    }

    private boolean connect() {
        try {
            client.connect(ip, port);
            log.info("[{}] 连接 FTP 服务器 [{}:{}] 成功", username, ip, port);
        } catch (IOException e) {
            log.error("[{}] 连接 FTP 服务器 [{}:{}] 失败", username, ip, port, e);
            return false;
        }
        return true;
    }

    private boolean logout() {
        if (client.isConnected()) {
            try {
                client.disconnect();
            } catch (IOException e) {
                log.error("[{}] 断开 FTP 服务器 [{}:{}] 失败", username, ip, port, e);
                return false;
            }
        }
        return true;
    }
}
