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
package com.webank.bcreconcile.entity;

import com.webank.bcreconcile.db.entity.TaskInfo;
import com.webank.bcreconcile.enums.TriggerType;
import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/6/17
 */
@Data
@Builder
public class ReconcileContext {

    /**
     * Reconciliation task trigger mode
     */
    private TriggerType triggerType;

    /**
     * Whether the reconciliation task is enabled asynchronously
     */
    private boolean async;

    /**
     * Name of reconciliation document of business party
     */
    private String businessFileName;

    /**
     * The start time of the reconciliation data range
     */
    private Date dataRangeBeginTime;

    /**
     * The end time of the reconciliation data range
     */
    private Date dataRangeEndTime;

    /**
     * Reconciliation task detail object
     */
    private TaskInfo taskInfo;

    /**
     * Reconciliation business party documents
     */
    private File businessReconFile;

    /**
     * Reconciliation BC party documents
     */
    private File bcReconFile;

    /**
     * Reconciliation task results
     */
    private List<Future<ReconcileResult>> reconcileResults;

    /**
     * Reconciliation task result document
     */
    private File resultFile;

}
