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
package com.webank.blockchain.data.reconcile.exporter.result;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.blockchain.data.reconcile.entity.ReconcileResult;
import com.webank.blockchain.data.reconcile.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/7/1
 */
@ConditionalOnProperty(value = "reconcile.file.type", havingValue = "json")
@Service
@Slf4j
public class ResultDataExportServiceJsonImpl implements ResultDataExportService{

    @Override
    public File exportResultData(String filePath, List<Future<ReconcileResult>> reconcileResults) throws Exception {
        File file = FileUtils.newFile(filePath);
        log.info("reconcile result string collect begins, time :" + LocalTime.now().toString());
        JsonFactory factory = new JsonFactory();
        JsonGenerator generator = factory.createGenerator(file, JsonEncoding.UTF8);
        ObjectMapper objectMapper = new ObjectMapper();
        List<ReconcileResult> reconcileResultList = new ArrayList<>();
        for(Future<ReconcileResult> future : reconcileResults){
            ReconcileResult reconcileResult = future.get();
            reconcileResultList.add(reconcileResult);
        }
        objectMapper.writeValue(generator,reconcileResultList);
        generator.close();
        log.info("reconcile result string collect begins, time :" + LocalTime.now().toString());
        return file;
    }
}
