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
package com.webank.blockchain.data.reconcile.handler.executor;

import com.webank.blockchain.data.reconcile.entity.ReconcileContext;
import com.webank.blockchain.data.reconcile.entity.ReconcileResult;
import com.webank.blockchain.data.reconcile.handler.Handler;
import com.webank.blockchain.data.reconcile.handler.InvocationHandler;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

/**
 * An abstract class that handles the reconciliation execution steps and is responsible for parsing and comparing
 * reconciliation files. It contains a core method for reconciliation execution and provides a default implementation
 *
 * @author wesleywang
 * @Description: ReconcileExecuteHandler
 * @date 2020/6/17
 */
public abstract class ReconcileExecuteHandler implements Handler {

    @Override
    public void invoke(ReconcileContext context, InvocationHandler handler) throws Exception {
        List<Future<ReconcileResult>> reconcileResults = parseAndExecute(context.getBusinessReconFile(),
                context.getBcReconFile());
        context.setReconcileResults(reconcileResults);
        handler.handle(context);
    }

    abstract List<Future<ReconcileResult>> parseAndExecute(File businessReconFile, File bcReconFile) throws Exception;

}
