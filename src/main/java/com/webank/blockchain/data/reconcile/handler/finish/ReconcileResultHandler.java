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

import com.webank.blockchain.data.reconcile.entity.ReconcileContext;
import com.webank.blockchain.data.reconcile.entity.ReconcileResult;
import com.webank.blockchain.data.reconcile.exception.ReconcileException;
import com.webank.blockchain.data.reconcile.handler.Handler;
import com.webank.blockchain.data.reconcile.handler.InvocationHandler;
import com.webank.blockchain.data.reconcile.utils.FileUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

/**
 * The abstract class for reconciliation result processing, which is the end of the entire chain of responsibility,
 * is responsible for exporting and transmitting the reconciliation result and provides the default implementation
 *
 * @author wesleywang
 * @Description: ReconcileResultHandler
 * @date 2020/6/17
 */
public abstract class ReconcileResultHandler implements Handler {


    private ReconcileContext reconcileContext;

    @Override
    public void invoke(ReconcileContext context, InvocationHandler handler) throws Exception {
        reconcileContext = context;
        File file = null;
        try {
            file = exportData(context.getReconcileResults());
        }catch (Exception e){
            FileUtils.clearReconcileFileCache(context);
            throw new ReconcileException("reconcile result data export failed", e);
        }
        context.getTaskInfo().setResultFilePath(file.getPath());
        context.setResultFile(file);
        transferResultFile(file);
    }

    abstract File exportData(List<Future<ReconcileResult>> reconcileResults) throws Exception;

    abstract void transferResultFile(File file) throws Exception;

    public ReconcileContext getReconcileContext() {
        return reconcileContext;
    }
}
