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
package com.webank.blockchain.data.reconcile.task;

import com.webank.blockchain.data.reconcile.config.ReconcileConfig;
import com.webank.blockchain.data.reconcile.db.entity.TaskInfo;
import com.webank.blockchain.data.reconcile.entity.ReconcileContext;
import com.webank.blockchain.data.reconcile.enums.TaskStatus;
import com.webank.blockchain.data.reconcile.handler.InvocationHandler;
import com.webank.blockchain.data.reconcile.handler.ReconcileHandlerFactory;
import com.webank.blockchain.data.reconcile.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Reconciliation task compensation processing
 * reconciliation task status flow：
 * 1.INIT -> EXECUTING -> SUCCESS
 * 2.INIT -> EXECUTING -> FAILURE -> SUCCESS
 * 3.INIT -> EXECUTING -> FAILURE -> TERMINATE
 * Here we need to compensate the EXECUTING and FAILURE intermediate states
 *
 * @author wesleywang
 * @Description:
 * @date 2020/6/17
 */
@Component
@EnableScheduling
@Slf4j
public class TaskCompensateTimer {

    @Autowired
    private ReconcileTaskService taskService;
    @Autowired
    private ReconcileConfig reconcileConfig;
    @Autowired
    private ReconcileHandlerFactory handlerFactory;


    @Scheduled(cron = "${reconcile.executing.compensate.rule}")
    public void executingStateCompensate(){
        log.info("executingState compensate task start at " + LocalDate.now().toString());
        List<TaskInfo> taskInfoList = taskService.queryByTaskStatus(TaskStatus.EXECUTING.getStatus());
        log.info("the executingState reconcile task list size is " + taskInfoList.size());
        for (TaskInfo taskInfo : taskInfoList){
            try {
                if (System.currentTimeMillis() - taskInfo.getLastExecuteStartTime().getTime()
                        > reconcileConfig.getTaskTimeout()) {
                    taskService.changeReconcileTaskStatus(taskInfo.getPkId(), TaskStatus.EXECUTING.getStatus(),
                            TaskStatus.FAILURE.getStatus(), new Date());
                    clearReconcileFileByCompensate(taskInfo);
                    log.info("the reconcile task compensate success, the task id = " + taskInfo.getTaskId());
                }
            }catch (Exception e){
                log.error("taskService executingState compensate failed, the task id = " + taskInfo.getTaskId()  + "reason :",e);
            }
        }
    }

    @Scheduled(cron ="${reconcile.failed.compensate.rule}")
    public void failedStateCompensate(){
        log.info("failedState compensate task start at " + LocalDate.now().toString());
        List<TaskInfo> taskInfoList = taskService.queryByTaskStatus(TaskStatus.FAILURE.getStatus());
        log.info("the failedState reconcile task list size is " + taskInfoList.size());
        for (TaskInfo taskInfo : taskInfoList){
            try {
                if (System.currentTimeMillis() - taskInfo.getLastExecuteEndTime().getTime()
                        > reconcileConfig.getRetryIntervalTime()) {
                    if (reconcileConfig.getRetryCount() <= 0){
                        taskService.changeReconcileTaskStatus(taskInfo.getPkId(),TaskStatus.FAILURE.getStatus(),
                                TaskStatus.TERMINATE.getStatus());
                        clearReconcileFileByCompensate(taskInfo);
                        continue;
                    }
                    InvocationHandler handler = handlerFactory.getInvocationHandler();
                    ReconcileContext context = ReconcileContext.builder()
                            .taskInfo(taskInfo)
                            .build();
                    handler.handle(context);
                    if (taskInfo.getStatus() == TaskStatus.FAILURE.getStatus() &&
                            taskInfo.getRetryCount() >= reconcileConfig.getRetryCount()){
                        taskService.changeReconcileTaskStatus(taskInfo.getPkId(),TaskStatus.FAILURE.getStatus(),
                                TaskStatus.TERMINATE.getStatus());
                        clearReconcileFileByCompensate(taskInfo);
                    }
                    log.info("the reconcile task compensate success, the task id = " + taskInfo.getTaskId());
                }
            }catch (Exception e){
                log.error("taskService executingState compensate failed, the task id = " + taskInfo.getTaskId()  + "reason :",e);
            }
        }
    }


    private void clearReconcileFileByCompensate(TaskInfo taskInfo){
        FileUtils.delFile(taskInfo.getBusinessFilePath());
        FileUtils.delFile(taskInfo.getBcFilePath());
        FileUtils.delFile(taskInfo.getResultFilePath());
    }

}
