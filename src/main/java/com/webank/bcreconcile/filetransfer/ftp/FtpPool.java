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
package com.webank.bcreconcile.filetransfer.ftp;

import com.webank.bcreconcile.config.FTPConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/6/16
 */
@ConditionalOnProperty(value = "ftp.enabled", havingValue = "true")
@Component
@Slf4j
public class FtpPool {

    FtpClientFactory factory;
    private final GenericObjectPool<FTPClient> internalPool;

    //Initializes the connection pool
    public FtpPool(@Autowired FtpClientFactory factory) {
        this.factory = factory;
        FTPConfig config = factory.getConfig();
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(config.getMaxTotal());
        poolConfig.setMinIdle(config.getMinIdel());
        poolConfig.setMaxIdle(config.getMaxIdle());
        poolConfig.setMaxWaitMillis(config.getMaxWaitMillis());
        this.internalPool = new GenericObjectPool<FTPClient>(factory, poolConfig);
    }

    //Fetches a connection from the connection pool
    public FTPClient getFTPClient() {
        try {
            return internalPool.borrowObject();
        } catch (Exception e) {
            log.error("internalPool.borrowObject failed, reason : ", e);
            return null;
        }
    }

    //Return the link to the connection pool
    public void returnFTPClient(FTPClient ftpClient) {
        try {
            internalPool.returnObject(ftpClient);
        } catch (Exception e) {
            log.error("internalPool.returnObject failed, reason : ", e);
        }
    }

    //Destruction of the pool
    public void destroy() {
        try {
            internalPool.close();
        } catch (Exception e) {
            log.error("internalPool.close failed, reason : ", e);
        }
    }
}
