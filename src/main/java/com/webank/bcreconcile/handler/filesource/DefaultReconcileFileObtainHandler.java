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
package com.webank.bcreconcile.handler.filesource;

import cn.hutool.core.date.DateUtil;
import com.webank.bcreconcile.config.ReconcileConfig;
import com.webank.bcreconcile.db.entity.TaskInfo;
import com.webank.bcreconcile.exporter.bc.BCDataExportService;
import com.webank.bcreconcile.filetransfer.FileTransferService;
import com.webank.bcreconcile.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.util.Date;

/**
 * The default implementation of the abstract class that gets the reconciliation file includes the business data
 * and blockchain data,where the business data is obtained through the {@link FileTransferService} interface and
 * the blockchain data is obtained through {@link BCDataExportService}
 *
 * @author wesleywang
 * @Description: DefaultReconcileFileObtainHandler
 * @date 2020/6/19
 */
@Service
@Order(2)
public class DefaultReconcileFileObtainHandler extends ReconcileFileObtainHandler {

    @Autowired
    private FileTransferService fileService;
    @Autowired
    private BCDataExportService bcDataExportService;
    @Autowired
    private ReconcileConfig reconcileConfig;

    @Override
    File getBCReconcileFile(TaskInfo taskInfo) throws Exception {
        // Block chain reconciliation file name generation
        String filePath = FileUtils.BC_RECONCILE_FILEPATH + "BC_" + taskInfo.getBusinessFileName().split("_")[1];

        taskInfo.setBcFilePath(filePath);
        return bcDataExportService.exportData(filePath, taskInfo.getDataRangeBeginTime(),
                    taskInfo.getDataRangeEndTime());
    }

    @Override
    File getBusinessReconcileFile(TaskInfo taskInfo) throws Exception {
        String fileName = taskInfo.getBusinessFileName();
        if (fileName == null) {
            // Timing task filename generation rule: reconciliation is carried out across days, and the beginning time
            // of reconciliation data is taken as part of reconciliation name
            String time = LocalDate.now().minusDays(reconcileConfig.getDaysTimeRange()).toString();
            fileName = reconcileConfig.getBusinessName() + "_" + time + "." + reconcileConfig.getFileType();
            Date startDate = DateUtil.parse(time);
            Date endDate = DateUtil.offsetDay(startDate, 1);

            taskInfo.setDataRangeBeginTime(startDate);
            taskInfo.setDataRangeEndTime(endDate);
            taskInfo.setBusinessFileName(fileName);
        }
        taskInfo.setBusinessFilePath(FileUtils.BUS_RECONCILE_FILEPATH + fileName);
        return fileService.obtainFile(fileName, taskInfo.getBusinessFilePath());
    }
}
