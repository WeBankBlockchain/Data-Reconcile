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
package com.webank.blockchain.data.reconcile.handler.task;

import com.webank.blockchain.data.reconcile.db.entity.TaskInfo;
import com.webank.blockchain.data.reconcile.entity.ReconcileContext;
import com.webank.blockchain.data.reconcile.enums.TaskStatus;
import com.webank.blockchain.data.reconcile.exception.ReconcileException;
import com.webank.blockchain.data.reconcile.handler.Handler;
import com.webank.blockchain.data.reconcile.handler.InvocationHandler;
import com.webank.blockchain.data.reconcile.task.ReconcileTaskService;
import com.webank.blockchain.data.reconcile.utils.FileUtils;
import com.webank.blockchain.data.reconcile.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Reconciliation task flow management, all exceptions will be thrown to this layer to resolve, is the beginning
 * of the entire chain of responsibility
 *
 * @author wesleywang
 * @Description: ReconcileTaskHandler
 * @date 2020/6/17
 */
@Service
@Order(1)
@Slf4j
public class ReconcileTaskHandler implements Handler {

    @Autowired
    private ReconcileTaskService taskService;

    @Transactional
    @Override
    public void invoke(ReconcileContext context, InvocationHandler handler) throws ReconcileException {
        TaskInfo taskInfo = context.getTaskInfo();
        if(taskInfo != null && taskInfo.getStatus() == TaskStatus.FAILURE.getStatus()){
            int retryCount = taskInfo.getRetryCount();
            taskInfo.setRetryCount(++retryCount);
            log.info("the failed reconcile task retry time ++, the task id=" + taskInfo.getTaskId());
        }else {
            if (taskService.existTaskInfo(context.getBusinessFileName())){
                throw new ReconcileException("the reconcile task already exists，filename = " + context.getBusinessFileName());
            }
            taskInfo = taskService.createReconcileTask(context.getTriggerType(), context.getBusinessFileName(),
                    context.getDataRangeBeginTime(),context.getDataRangeEndTime());
            log.info("create a reconcile task, the task id=" + taskInfo.getTaskId());
            context.setTaskInfo(taskInfo);
        }

        if (context.isAsync()){
            ThreadUtils.timer.schedule(() ->
                    taskExecute(context,handler),100L, TimeUnit.MILLISECONDS);
            return;
        }
        taskExecute(context,handler);
    }

    private void taskExecute(ReconcileContext context, InvocationHandler handler){
        TaskInfo taskInfo = context.getTaskInfo();
        //reconcile begins
        taskInfo.setStatus(TaskStatus.EXECUTING.getStatus())
                .setLastExecuteStartTime(new Date());
        taskService.save(taskInfo);
        log.info("the reconcile task begins, the task id=" + taskInfo.getTaskId());
        try{
            handler.handle(context);
            taskService.save(taskInfo);
        }catch (Exception e){
            //reconcile failed
            taskInfo.setStatus(TaskStatus.FAILURE.getStatus());
            taskInfo.setLastExecuteEndTime(new Date());
            taskService.changeReconcileTaskStatus(taskInfo.getPkId(),
                    TaskStatus.EXECUTING.getStatus(),taskInfo.getStatus(),taskInfo.getLastExecuteEndTime());
            FileUtils.clearReconcileFileCache(context);
            log.error("the reconcile task execute failed,the task id=" + taskInfo.getTaskId() + " reason:",e);
            return;
        }
        //reconcile success
        taskInfo.setStatus(TaskStatus.SUCCESS.getStatus());
        taskInfo.setLastExecuteEndTime(new Date());
        taskService.changeReconcileTaskStatus(taskInfo.getPkId(),
                TaskStatus.EXECUTING.getStatus(),taskInfo.getStatus(),taskInfo.getLastExecuteEndTime());
        log.info("the reconcile task execute success,the task id=" + taskInfo.getTaskId());
        // file consistency processing
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                TaskInfo validTask = taskService.queryTaskInfoByPkId(taskInfo.getPkId());
                if (validTask.getStatus() != TaskStatus.SUCCESS.getStatus()){
                    FileUtils.clearReconcileFileCache(context);
                }
            }
        });
    }
}
