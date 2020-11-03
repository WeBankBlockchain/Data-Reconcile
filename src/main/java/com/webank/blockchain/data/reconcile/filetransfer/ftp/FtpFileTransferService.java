/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.blockchain.data.reconcile.filetransfer.ftp;

import com.webank.blockchain.data.reconcile.config.FTPConfig;
import com.webank.blockchain.data.reconcile.filetransfer.FileTransferService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/6/16
 */
@ConditionalOnProperty(value = "ftp.enabled", havingValue = "true")
@Component
@Slf4j
public class FtpFileTransferService implements FileTransferService {

    @Autowired
    FTPConfig config;
    @Autowired
    FtpPool pool;


    /**
     * Upload files to the FTP server
     *
     * @param file Upload files to an FTP server
     * @return Returns file name successfully, otherwise returns NULL
     */
    public String upload(File file) throws Exception {
        FTPClient ftpClient = pool.getFTPClient();
        //开始进行文件上传
        String fileName = file.getName();
        try (FileInputStream input = new FileInputStream(file)) {
            boolean result = ftpClient.storeFile(fileName, input);//执行文件传输
            if (!result) {
                throw new RuntimeException("Upload failed");
            }
        } catch (Exception e) {
            log.error("file upload failed , reason : ", e);
            return null;
        } finally {
            log.info("Start returning the connection");
            pool.returnFTPClient(ftpClient);//Return the resources
        }
        return fileName;
    }

    /**
     * Download files from the FTP server
     *
     * @param fileName File name in the FTP server
     * @param localFilePath Local save path
     */
    public File downLoad(String fileName, String localFilePath) throws IOException {
        FTPClient ftpClient = pool.getFTPClient();
        File file = new File(localFilePath);
        OutputStream out = new FileOutputStream(file);
        if(ftpClient.retrieveFile(fileName, out)){
            out.flush();
            out.close();
        }else {
            log.error("origin file downLoad failed, fileName = " + fileName);
        }
        pool.returnFTPClient(ftpClient);
        return file;
    }


    /**
     * Query directory file
     * @param ftpDirectory
     */
    public FTPFile[] getFileList(String ftpDirectory) throws IOException {
        FTPFile[] ftpFiles = null;
        FTPClient ftpClient = pool.getFTPClient();
        try {
            ftpFiles = ftpClient.listFiles(ftpDirectory);
        }
        finally {
            log.info("Start returning the connection");
            pool.returnFTPClient(ftpClient);
        }
        return ftpFiles;
    }

    /**
     * Delete files on FTP
     * @param ftpDirAndFileName
     */
    public boolean deleteFile(String ftpDirAndFileName) throws IOException {
        FTPClient ftpClient = pool.getFTPClient();
        try {
            ftpClient.deleteFile(ftpDirAndFileName);
        } finally {
            log.info("Start returning the connection");
            pool.returnFTPClient(ftpClient);
        }
        return true;
    }

    /**
     * Delete FTP directory
     * @param ftpDirectory
     */
    public boolean deleteDirectory(String ftpDirectory) throws IOException {
        FTPClient ftpClient = pool.getFTPClient();
        try {
            ftpClient.removeDirectory(ftpDirectory);
        } finally {
            log.info("Start returning the connection");
            pool.returnFTPClient(ftpClient);
        }
        return true;
    }

    @Override
    public String sendFile(File file) throws Exception {
        return upload(file);
    }

    @Override
    public File obtainFile(String fileName, String localFilePath) throws IOException {
        return downLoad(fileName,localFilePath);
    }
}
