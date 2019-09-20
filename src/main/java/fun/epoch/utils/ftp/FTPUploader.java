package fun.epoch.utils.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE;
import static fun.epoch.utils.TextUtils.isNotBlank;

public class FTPUploader extends FTPClientPlus {
    public FTPUploader() {
        super(new FTPSettings());
    }

    public FTPUploader(FTPSettings settings) {
        super(settings);
    }

    public FTPUploader(String ip, String username, String password) {
        super(ip, username, password);
    }

    public boolean upload(String fileName, File file) {
        return upload(null, fileName, file);
    }

    public boolean upload(String remotePath, String fileName, File file) {
        return upload(remotePath, Collections.singletonMap(fileName, file));
    }

    public boolean upload(File... files) {
        return upload(null, files);
    }

    public boolean upload(String remotePath, File... files) {
        if (files != null && files.length != 0) {
            Map<String, File> map = Arrays.stream(files).collect(Collectors.toMap(File::getName, file -> file, (a, b) -> b));
            return upload(remotePath, map);
        }
        return false;
    }

    public boolean upload(String remotePath, Map<String, File> files) {
        return loginToDo(client -> {
            try {
                client.setFileType(BINARY_FILE_TYPE);
            } catch (IOException e) {
                log.error("设置 FTP [{}:{}] 传输属性失败", ip, port);
                return false;
            }

            if (isNotBlank(remotePath)) {
                try {
                    client.makeDirectory(remotePath);
                    client.changeWorkingDirectory(remotePath);
                } catch (IOException e) {
                    log.error("切换上传路径 [{}] 失败", remotePath, e);
                }
            }

            return files.keySet().stream().map(key -> uploadFile(key, files.get(key))).allMatch(result -> result);
        });
    }

    private boolean uploadFile(String fileName, File file) {
        try (
                FileInputStream fis = new FileInputStream(file)
        ) {
            if (client.storeFile(fileName, fis)) {
                log.info("文件 [{}] 上传成功", file.getPath());
                return true;
            }
            log.debug("文件 [{}] 上传失败", file.getPath());
        } catch (FileNotFoundException e) {
            log.error("找不到文件 [{}]", file.getPath(), e);
        } catch (IOException e) {
            log.error("文件 [{}] 上传失败", file.getPath(), e);
        }
        return false;
    }
}
