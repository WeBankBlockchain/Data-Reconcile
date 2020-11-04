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
package com.webank.blockchain.data.reconcile.handler.finish;

import com.webank.blockchain.data.reconcile.entity.ReconcileResult;
import com.webank.blockchain.data.reconcile.exporter.result.ResultDataExportService;
import com.webank.blockchain.data.reconcile.filetransfer.FileTransferService;
import com.webank.blockchain.data.reconcile.utils.FileUtils;
import com.webank.blockchain.data.reconcile.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

/**
 * The default implementation of the abstract class of account checking results contains two modules:
 * export {@link FileTransferService} of the result data and
 * transfer {@link ResultDataExportService} of the reconciliation result file.
 *
 * @author wesleywang
 * @Description: DefaultReconcileResultHandler
 * @date 2020/6/23
 */
@Service
@Slf4j
@Order(4)
public class DefaultReconcileResultHandler extends ReconcileResultHandler{

    @Autowired
    private FileTransferService fileTransferService;
    @Autowired
    private ResultDataExportService resultDataExportService;
    @Override
    File exportData(List<Future<ReconcileResult>> reconcileResults) throws Exception {
        String businessFileName = getReconcileContext().getBusinessFileName();
        if (businessFileName == null){
            return null;
        }
        String filePath = FileUtils.RESULT_RECONCILE_FILEPATH + "result_" +  businessFileName;
        return resultDataExportService.exportResultData(filePath, reconcileResults);
    }

    @Override
    void transferResultFile(File file){
        // async send result file
        ThreadUtils.transfer.execute(() -> {
            try {
                fileTransferService.sendFile(file);
            } catch (Exception e) {
                log.error("fileTransferService.sendFile failed, filename is " + file.getName() + "reason :", e);
            }
        });
    }
}
