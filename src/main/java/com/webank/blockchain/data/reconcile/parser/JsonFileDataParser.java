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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.webank.blockchain.data.reconcile.config.ReconcileConfig;
import com.webank.blockchain.data.reconcile.entity.ReconcileExecuteData;
import com.webank.blockchain.data.reconcile.entity.ReconcileInfo;
import com.webank.blockchain.data.reconcile.exception.ReconcileException;
import com.webank.blockchain.data.reconcile.reconcile.ReconcileTransfer;
import com.webank.blockchain.data.reconcile.utils.BufferedRandomAccessFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Json file parsing, the same principle as TXT
 *
 * @author wesleywang
 * @Description:
 * @date 2020/6/12
 */
@ConditionalOnExpression("'json'.equals('${reconcile.file.type}') && ${reconcile.general.enabled:true}")
@Service
@Slf4j
public class JsonFileDataParser implements FileParser {

    @Autowired
    private ReconcileConfig reconcileConfig;

    @Override
    public void parseAndTransfer(File businessReconFile, File bcReconFile, ReconcileTransfer transfer)
            throws ReconcileException, IOException {
        if (businessReconFile.length() <= 0) {
            throw new ReconcileException("businessReconFile is empty");
        }
        if (bcReconFile.length() <= 0) {
            throw new ReconcileException("bcReconFile is empty");
        }

        BufferedRandomAccessFile bcReader = null;
        JsonFactory f = new MappingJsonFactory();
        JsonParser jp = null;
        try {
            bcReader = new BufferedRandomAccessFile(bcReconFile, "r");
            ObjectMapper objectMapper = new ObjectMapper();
            MapType mapType = objectMapper.getTypeFactory().constructMapType(HashMap.class,String.class,String.class);
            jp = f.createParser(businessReconFile);

            Map<String, Long> bcIndexMap = new HashMap<>();
            String bcUniqueColumn = reconcileConfig.getBcUniqueColumn();
            String businessUniqueColumn = reconcileConfig.getBusinessUniqueColumn();
            long position;
            while (true) {
                position = bcReader.getFilePointer();
                String find = bcReader.readLine();
                if (find == null){
                    break;
                }
                if (find.contains("[")){
                    continue;
                }
                if (find.contains("]")) {
                    break;
                }
                if (find.lastIndexOf(",") == find.length() - 1){
                    find = find.substring(0, find.length() - 1);
                }
                Map<String,String> bcDataMap = objectMapper.readValue(find,mapType);
                if (bcDataMap.containsKey(bcUniqueColumn)) {
                    bcIndexMap.put(bcDataMap.get(bcUniqueColumn), position);
                }
            }
            JsonToken current = jp.nextToken();
            if (current != JsonToken.START_ARRAY) {
                throw new ReconcileException("Error: root should be START_ARRAY: quiting.");
            }
            while (jp.nextToken() != JsonToken.END_ARRAY) {
                Map<String, String> busFieldMap = new HashMap<>();
                ReconcileInfo busReconcileInfo;
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    String fieldName = jp.getCurrentName();
                    jp.nextToken();
                    String value = jp.getText();
                    busFieldMap.put(fieldName, value);
                }
                ReconcileExecuteData executeData = new ReconcileExecuteData();
                executeData.setId(busFieldMap.get(businessUniqueColumn));
                if (busFieldMap.containsKey(businessUniqueColumn) &&
                        bcIndexMap.containsKey(busFieldMap.get(businessUniqueColumn))) {
                    busReconcileInfo = new ReconcileInfo();
                    String uniqueId = busFieldMap.get(businessUniqueColumn);
                    busReconcileInfo.setId(uniqueId);

                    bcReader.seek(bcIndexMap.get(busReconcileInfo.getId()));
                    String find = bcReader.readLine();
                    if (find.lastIndexOf(",") == find.length() - 1){
                        find = find.substring(0, find.length() - 1);
                    }
                    Map<String,String> bcDataMap = objectMapper.readValue(find,mapType);

                    ReconcileInfo bcReconcileInfo = new ReconcileInfo();
                    bcReconcileInfo.setId(uniqueId);
                    bcReconcileInfo.setFieldMap(bcDataMap);
                    busReconcileInfo.setFieldMap(busFieldMap);
                    executeData.setBcOrg(bcReconcileInfo);
                    executeData.setBusOrg(busReconcileInfo);
                    executeData.setMatchById(true);
                }
                //reconcile data transfer to executor
                transfer.transferToExecutor(executeData);

            }
        } catch (IOException e) {
            log.error("JsonFileDataParser parseAndMatching failed, reason : ", e);
            throw new ReconcileException("JsonFileDataParser parseAndMatching failed", e);
        } finally {
            IOUtils.closeQuietly(bcReader);
            if (jp != null) {
                jp.close();
            }
        }
    }
}
