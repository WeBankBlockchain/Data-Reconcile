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
package com.webank.blockchain.data.reconcile.parser;


import com.webank.blockchain.data.reconcile.config.ReconcileConfig;
import com.webank.blockchain.data.reconcile.entity.ReconcileExecuteData;
import com.webank.blockchain.data.reconcile.entity.ReconcileInfo;
import com.webank.blockchain.data.reconcile.exception.ReconcileException;
import com.webank.blockchain.data.reconcile.reconcile.ReconcileTransfer;
import com.webank.blockchain.data.reconcile.utils.BufferedRandomAccessFile;
import com.webank.blockchain.data.reconcile.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * txt file analysis, implementation principle:
 * First of all, take one of the files in memory by reading generate index on the map < String, Long >, then, according
 * to the line read another file, parse the only key value in each row, according to the value of these index map to
 * find the corresponding position of the data line, reads the specified line, and then parse, analytical results
 * in {@link ReconcileExecuteData} object, give it to the executor for processing.
 *
 * @author wesleywang
 * @Description:
 * @date 2020/6/12
 */
@ConditionalOnExpression("'txt'.equals('${reconcile.file.type}') && ${reconcile.general.enabled:true}")
@Component
@Slf4j
public class TxtFileDataParser implements FileParser {

    @Autowired
    private ReconcileConfig reconcileConfig;

    @Override
    public void parseAndTransfer(File businessReconFile, File bcReconFile, ReconcileTransfer transfer)
            throws ReconcileException {
        if (businessReconFile.length() <= 0){
            throw new ReconcileException("businessReconFile is empty, fileName = " + businessReconFile.getName());
        }
        if (bcReconFile.length() <= 0){
            throw new ReconcileException("bcReconFile is empty, fileName = " + bcReconFile.getName());
        }

        BufferedRandomAccessFile bcReader = null;
        BufferedRandomAccessFile busReader = null;
        try {
            bcReader = new BufferedRandomAccessFile(bcReconFile, "r");
            busReader = new BufferedRandomAccessFile(businessReconFile, "r");
            String bcHead = bcReader.readLine();
            String busHead = busReader.readLine();

            Integer bcUniqueIndex = findUniqueColumnIndex(bcHead,reconcileConfig.getBcUniqueColumn());
            if (bcUniqueIndex == null){
                throw new ReconcileException("TxtFileDataParser parseAndMatching failed, can not find BcUniqueColumn");
            }

            Integer busUniqueIndex = findUniqueColumnIndex(busHead,reconcileConfig.getBusinessUniqueColumn());
            if (busUniqueIndex == null){
                throw new ReconcileException("TxtFileDataParser parseAndMatching failed, can not find BcUniqueColumn");
            }
            Map<String, Long> bcIndexMap = new HashMap<>();
            String[] bcKeys = bcHead.split(FileUtils.TXT_FILE_SPLIT);
            long position;
            while (true) {
                position = bcReader.getFilePointer();
                String find = bcReader.readLine();
                if (find == null) {
                    break;
                }
                String[] strings = find.split(FileUtils.TXT_FILE_SPLIT);
                bcIndexMap.put(strings[bcUniqueIndex], position);
            }

            String[] busKeys = busHead.split(FileUtils.TXT_FILE_SPLIT);
            while (true) {
                String find = busReader.readLine();
                if (find == null) {
                    break;
                }
                String[] busValues = find.split(FileUtils.TXT_FILE_SPLIT);
                String uniqueId = busValues[busUniqueIndex];
                ReconcileExecuteData executeData = new ReconcileExecuteData();
                executeData.setId(uniqueId);
                if (bcIndexMap.containsKey(uniqueId)) {
                    bcReader.seek(bcIndexMap.get(uniqueId));
                    String bcStr = bcReader.readLine();
                    String[] bcValues = bcStr.split(FileUtils.TXT_FILE_SPLIT);
                    executeData.setBusOrg(buildReconInfo(uniqueId, busValues, busKeys));
                    executeData.setBcOrg(buildReconInfo(uniqueId, bcValues, bcKeys));
                    executeData.setMatchById(true);
                }
                //reconcile data transfer to executor
                transfer.transferToExecutor(executeData);
            }
        } catch (IOException e) {
            log.error("TxtFileDataParser parseAndMatching failed, reason : ", e);
            throw new ReconcileException("TxtFileDataParser parseAndMatching failed",e);
        } finally {
            IOUtils.closeQuietly(bcReader);
            IOUtils.closeQuietly(busReader);
        }
    }


    private ReconcileInfo buildReconInfo(String uniqueId, String[] values, String[] keys) {
        ReconcileInfo reconcileInfo = new ReconcileInfo();
        Map<String, String> fieldMap = new HashMap<>();
        reconcileInfo.setId(uniqueId);
        reconcileInfo.setFieldMap(fieldMap);
        for (int i = 1; i < values.length; i++) {
            fieldMap.put(keys[i], values[i]);
        }
        return reconcileInfo;
    }

    public Integer findUniqueColumnIndex(String str,String uniqueColumn) {
        Integer uniqueIndex = null;
        String[] strings = str.split(FileUtils.TXT_FILE_SPLIT);
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].equals(uniqueColumn)) {
                uniqueIndex = i;
                break;
            }
        }
        return uniqueIndex;
    }

}