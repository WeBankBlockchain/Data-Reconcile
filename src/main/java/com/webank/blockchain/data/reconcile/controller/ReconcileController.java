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
package com.webank.blockchain.data.reconcile.controller;

import cn.hutool.core.io.FileUtil;
import com.webank.blockchain.data.reconcile.constants.ResponseCode;
import com.webank.blockchain.data.reconcile.db.entity.TaskInfo;
import com.webank.blockchain.data.reconcile.entity.ReconcileContext;
import com.webank.blockchain.data.reconcile.entity.Responses;
import com.webank.blockchain.data.reconcile.enums.TriggerType;
import com.webank.blockchain.data.reconcile.handler.InvocationHandler;
import com.webank.blockchain.data.reconcile.handler.ReconcileHandlerFactory;
import com.webank.blockchain.data.reconcile.task.ReconcileTaskService;
import com.webank.blockchain.data.reconcile.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Date;

/**
 * ReconcileController
 *
 * @Description: ReconcileController
 * @author graysonzhang
 * @date 2020-06-10 16:28:22
 *
 */
@RestController
@Slf4j
@RequestMapping("reconcile")
public class ReconcileController {

    @Autowired
    private ReconcileHandlerFactory handlerFactory;
    @Autowired
    private ReconcileTaskService taskService;

    @RequestMapping(value = "/requestReconcile")
    public Responses<String> requestReconcile(@RequestParam(name = "fileName") String fileName,
                                        @RequestParam(name = "dataRangeBeginTime") @DateTimeFormat(pattern="yyyy-mm-dd HH:mm:ss") Date dataRangeBeginTime,
                                        @RequestParam(name = "dataRangeEndTime") @DateTimeFormat(pattern="yyyy-mm-dd HH:mm:ss") Date dataRangeEndTime){
        if (FileUtil.exist(FileUtils.BUS_RECONCILE_FILEPATH + fileName)){
            return Responses.createFailedResult(ResponseCode.RECONCILE_FAILED_CODE,"filename already exists");
        }
        InvocationHandler handler = handlerFactory.getInvocationHandler();
        ReconcileContext context = ReconcileContext.builder()
                .triggerType(TriggerType.MANUAL)
                .businessFileName(fileName)
                .dataRangeBeginTime(dataRangeBeginTime)
                .dataRangeEndTime(dataRangeEndTime)
                .async(true)
                .build();
        log.info("reconcile manual task start , time :" + LocalDate.now().toString());
        try {
            handler.handle(context);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Responses.createFailedResult(ResponseCode.RECONCILE_FAILED_CODE,"reconcile task submit failed," +
                    "the task id = " + context.getTaskInfo().getTaskId());
        }
        return Responses.createSuccessResult(context.getTaskInfo().getTaskId());
    }


    @SuppressWarnings("rawtypes")
    @GetMapping(value = "/getReconcileResult/{taskId}")
    public Responses getReconcileResult(@PathVariable("taskId") String taskId){
        TaskInfo taskInfo = taskService.queryTaskInfoByTaskId(taskId);
        if (taskInfo == null){
            return Responses.createSuccessResult("No task was found based on the taskId");
        }
        return Responses.createSuccessResult(taskInfo.getStatus());
    }

}
