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

import com.webank.bcreconcile.db.entity.TaskInfo;
import com.webank.bcreconcile.entity.ReconcileContext;
import com.webank.bcreconcile.exception.ReconcileException;
import com.webank.bcreconcile.handler.Handler;
import com.webank.bcreconcile.handler.InvocationHandler;
import com.webank.bcreconcile.utils.FileUtils;

import java.io.File;

/**
 * An abstract class that obtain reconciliation files, responsible for the steps of file acquisition,
 * including business side reconciliation files and block chain reconciliation file acquisition template method,
 * providing default implementation
 *
 * @author wesleywang
 * @Description: ReconcileFileObtainHandler
 * @date 2020/6/17
 */
public abstract class ReconcileFileObtainHandler implements Handler {

    @Override
    public void invoke(ReconcileContext context, InvocationHandler handler) throws Exception {
        File businessReconcileFile = null;
        File bcReconcileFile = null;
        try {
            businessReconcileFile = getBusinessReconcileFile(context.getTaskInfo());
            bcReconcileFile = getBCReconcileFile(context.getTaskInfo());
            context.setBcReconFile(bcReconcileFile);
            context.setBusinessReconFile(businessReconcileFile);
        }catch (Exception e){
            FileUtils.clearReconcileFileCache(context);
            throw new ReconcileException("reconcile file obtain failed", e);
        }
        TaskInfo taskInfo = context.getTaskInfo();
        context.setBusinessFileName(taskInfo.getBusinessFileName());
        taskInfo.setBcFilePath(bcReconcileFile.getPath());
        taskInfo.setBusinessFilePath(businessReconcileFile.getPath());

        handler.handle(context);
    }

    abstract File getBCReconcileFile(TaskInfo taskInfo) throws Exception;

    abstract File getBusinessReconcileFile(TaskInfo taskInfo) throws Exception;

}
