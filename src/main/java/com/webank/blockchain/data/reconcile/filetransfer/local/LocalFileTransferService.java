package com.webank.blockchain.data.reconcile.filetransfer.local;

import cn.hutool.core.io.FileUtil;
import com.webank.blockchain.data.reconcile.filetransfer.FileTransferService;
import com.webank.blockchain.data.reconcile.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/12/15
 */
@ConditionalOnProperty(value = "local.enabled", havingValue = "true")
@Component
@Slf4j
public class LocalFileTransferService implements FileTransferService {

    @Override
    public String sendFile(File file) throws Exception {
        return null;
    }

    @Override
    public File obtainFile(String fileName, String localFilePath) throws IOException {
        File localFile = Paths.get("./", fileName).toFile();
        return FileUtil.copyFile(localFile, FileUtils.newFile(localFilePath), StandardCopyOption.REPLACE_EXISTING);
    }
}
