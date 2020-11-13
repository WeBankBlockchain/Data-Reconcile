/**
 * Copyright 2020 Webank.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.blockchain.data.reconcile.filetransfer.ftp;

import com.webank.blockchain.data.reconcile.config.FTPConfig;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author wesleywang
 * @Description: FtpClientFactory
 * @date 2020/6/16
 */
@ConditionalOnProperty(value = "ftp.enabled", havingValue = "true")
@Component
public class FtpClientFactory implements PooledObjectFactory<FTPClient> {

    @Autowired
    FTPConfig config;

    //Create a connection to the pool
    @Override
    public PooledObject<FTPClient> makeObject() {
        FTPClient ftpClient = new FTPClient();
        return new DefaultPooledObject<>(ftpClient);
    }

    //Destroy the connection. This method is called to destroy the connection when the number of free connection pools reaches the limit
    @Override
    public void destroyObject(PooledObject<FTPClient> pooledObject)  {
        FTPClient ftpClient = pooledObject.getObject();
        try {
            ftpClient.logout();
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not disconnect from server.", e);
        }
    }

    //Link status check
    @Override
    public boolean validateObject(PooledObject<FTPClient> pooledObject) {
        FTPClient ftpClient = pooledObject.getObject();
        try {
            return ftpClient.sendNoOp();
        } catch (IOException e) {
            return false;
        }
    }

    //Initializing connection
    @Override
    public void activateObject(PooledObject<FTPClient> pooledObject) throws Exception {
        FTPClient ftpClient = pooledObject.getObject();
        ftpClient.connect(config.getHost(),config.getPort());
        ftpClient.login(config.getUserName(), config.getPassWord());
        ftpClient.setControlEncoding(config.getEncoding());
        ftpClient.changeWorkingDirectory(config.getWorkDir());
        ftpClient.enterLocalActiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);//设置上传文件类型为二进制，否则将无法打开文件
    }

    //Passivate the connection, making the link available
    @Override
    public void passivateObject(PooledObject<FTPClient> pooledObject) throws Exception {
        FTPClient ftpClient = pooledObject.getObject();
        try {
            ftpClient.logout();
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not disconnect from server.", e);
        }
    }
    
    //Used to get the Pool property from the connection pool
    public FTPConfig getConfig() {
        return config;
    }
}
