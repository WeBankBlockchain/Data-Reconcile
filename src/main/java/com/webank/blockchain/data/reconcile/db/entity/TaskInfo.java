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
package com.webank.blockchain.data.reconcile.db.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/6/17
 */
@Data
@Accessors(chain = true)
@Entity(name = "task_info")
@Table(name = "task_info",indexes = { @Index(name = "status", columnList = "status")})
@EqualsAndHashCode(callSuper = true)
public class TaskInfo extends IdEntity {
    private static final long serialVersionUID = 9096084645189100625L;

    /**
     * Task unique id
     */
    @Column(name = "task_id", unique = true)
    private String taskId;

    /**
     * Execution node ip
     */
    @Column(name = "node_id")
    private String nodeId;

    /**
     * task status
     */
    @Column(name = "status")
    private int status;

    /**
     * Mode of triggering
     */
    @Column(name = "triggered")
    private int triggered;

    /**
     * Name of reconciliation document of business party
     */
    @Column(name = "business_file_name")
    private String businessFileName;

    /**
     * Path of reconciliation document of business party
     */
    @Column(name = "business_file_path")
    private String businessFilePath;

    /**
     * Path of reconciliation document of block chain
     */
    @Column(name = "bc_file_path")
    private String bcFilePath;

    /**
     * Path of reconciliation document of result
     */
    @Column(name = "result_file_path")
    private String resultFilePath;

    /**
     * Last execution start time
     */
    @Column(name = "last_execute_starttime")
    private Date lastExecuteStartTime;

    /**
     * Last execution end time
     */
    @Column(name = "last_execute_endtime")
    private Date lastExecuteEndTime;

    /**
     * Number of failed retries
     */
    @Column(name = "retry_count")
    private int retryCount;

    /**
     * The start time of the reconciliation data range
     */
    @Column(name = "data_range_begintime")
    private Date dataRangeBeginTime;

    /**
     * The end time of the reconciliation data range
     */
    @Column(name = "data_range_endtime")
    private Date dataRangeEndTime;

    @CreationTimestamp
    @Column(name = "createtime", columnDefinition = "timestamp NOT NULL DEFAULT '2019-01-01 00:00:00'", insertable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @UpdateTimestamp
    @Column(name = "updatetime", columnDefinition = "timestamp not null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    protected Date updateTime;

}