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
package com.webank.bcreconcile.handler.executor;

import com.webank.bcreconcile.entity.ReconcileResult;
import com.webank.bcreconcile.parser.FileParser;
import com.webank.bcreconcile.reconcile.ReconcileExecutor;
import com.webank.bcreconcile.reconcile.ReconcileTransfer;
import com.webank.bcreconcile.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * The default implementation for the reconciliation processing step consists of two modules, parsing
 * {@link FileParser} and executing {@link ReconcileExecutor}, with a decoupling between the modules through
 * the {@link ReconcileTransfer} interface
 *
 * @author wesleywang
 * @Description: DefaultReconcileExecuteHandler
 * @date 2020/6/23
 */
@Service
@Order(3)
public class DefaultReconcileExecuteHandler extends ReconcileExecuteHandler{

    @Autowired
    private FileParser fileParser;
    @Autowired
    private ReconcileExecutor reconcileExecutor;

    @Override
    List<Future<ReconcileResult>> parseAndExecute(File businessReconFile, File bcReconFile) throws Exception {
        List<Future<ReconcileResult>> reconcileResults = new ArrayList<>();
        fileParser.parseAndTransfer(businessReconFile, bcReconFile, executeData -> {
            Future<ReconcileResult> future = ThreadUtils.executor.submit(
                    () -> reconcileExecutor.execute(executeData)
            );
            reconcileResults.add(future);
        });
        return reconcileResults;
    }

}
