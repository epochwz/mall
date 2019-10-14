package fun.epoch.mall.utils.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static fun.epoch.mall.utils.TextUtils.isNotBlank;
import static org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE;

/**
 * 增强版 FTP 客户端：文件上传
 */
public class FTPUploader extends FTPClientPlus {
    public FTPUploader() {
        super();
    }

    public FTPUploader(FTPSettings settings) {
        super(settings);
    }

    public FTPUploader(String ip, String username, String password) {
        super(ip, username, password);
    }

    public boolean upload(String remotePath, String fileName, File file) {
        return upload(remotePath, Collections.singletonMap(fileName, file));
    }

    public boolean upload(File... files) {
        return upload(null, files);
    }

    public boolean upload(String remotePath, File... files) {
        if (files != null && files.length != 0) {
            Map<String, File> uploadFiles = Arrays.stream(files).collect(Collectors.toMap(File::getName, file -> file, (a, b) -> b));
            return upload(remotePath, uploadFiles);
        }
        return false;
    }

    public boolean upload(String remotePath, Map<String, File> files) {
        return loginToDo(client -> {
            try {
                client.setFileType(BINARY_FILE_TYPE);
            } catch (IOException e) {
                log.error("设置 FTP [{}:{}] 传输属性失败", ip, port, e);
                return false;
            }

            if (isNotBlank(remotePath)) {
                try {
                    client.makeDirectory(remotePath);
                    client.changeWorkingDirectory(remotePath);
                } catch (IOException e) {
                    log.error("设置 FTP [{}:{}] 上传路径 [{}] 失败", ip, port, remotePath, e);
                }
            }

            return files.keySet().stream().allMatch(key -> uploadFile(key, files.get(key)));
        });
    }

    private boolean uploadFile(String fileName, File file) {
        try (
                FileInputStream fis = new FileInputStream(file)
        ) {
            if (client.storeFile(fileName, fis)) {
                log.info("文件 [{}] 上传成功：文件名 [{}]", file.getPath(), fileName);
                return true;
            }
            log.error("文件 [{}] 上传失败：{}", file.getPath(), client.getReplyString());
        } catch (FileNotFoundException e) {
            log.error("文件 [{}] 上传失败：找不到文件！", file.getPath(), e);
        } catch (IOException e) {
            log.error("文件 [{}] 上传失败：{}", file.getPath(), e.getMessage(), e);
        }
        return false;
    }
}
