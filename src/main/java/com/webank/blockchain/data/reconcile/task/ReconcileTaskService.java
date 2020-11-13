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

import cn.hutool.core.collection.CollectionUtil;
import com.webank.blockchain.data.reconcile.db.dao.TaskDao;
import com.webank.blockchain.data.reconcile.db.entity.TaskInfo;
import com.webank.blockchain.data.reconcile.enums.TaskStatus;
import com.webank.blockchain.data.reconcile.enums.TriggerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Description: ReconclieTask
 * @author graysonzhang
 * @date 2020-06-10 16:30:06
 */
@Service
public class ReconcileTaskService {

    @Autowired
    private TaskDao taskDao;
    @Autowired
    private InetAddress inetAddress;

    public TaskInfo createReconcileTask(TriggerType triggered, String businessFileName,
                                        Date dataRangeBeginTime, Date dataRangeEndTime){
        TaskInfo task = new TaskInfo();
        task.setNodeId(inetAddress.getHostAddress())
                .setTaskId(UUID.randomUUID().toString())
                .setStatus(TaskStatus.INIT.getStatus())
                .setBusinessFileName(businessFileName)
                .setDataRangeBeginTime(dataRangeBeginTime)
                .setDataRangeEndTime(dataRangeEndTime)
                .setTriggered(triggered.getType());
        return save(task);
    }

    public TaskInfo save(TaskInfo taskInfo){
        return taskDao.save(taskInfo);
    }

    public void changeReconcileTaskStatus(long pkId, int from, int to){
        taskDao.updateTaskStatus(pkId, from,to);
    }

    public void changeReconcileTaskStatus(long pkId, int from, int to, Date lastExecuteEndTime){
        taskDao.updateTaskStatus(pkId, from,to,lastExecuteEndTime);
    }

    public List<TaskInfo> queryByTaskStatus(int status){
        return taskDao.queryTaskInfoByStatus(status);
    }

    public List<TaskInfo> queryNotOverTaskByBusFileName(String fileName){
        return taskDao.queryNotOverTaskByBusFileName(fileName);
    }

    public boolean existTaskInfo(String fileName){
        return fileName != null && CollectionUtil.isNotEmpty(queryNotOverTaskByBusFileName(fileName));
    }

    public TaskInfo queryTaskInfoByTaskId(String taskId){
        return taskDao.queryTaskInfoByTaskId(taskId);
    }

    public TaskInfo queryTaskInfoByPkId(long pkId){
        return taskDao.queryTaskInfoByPkId(pkId);
    }

}
