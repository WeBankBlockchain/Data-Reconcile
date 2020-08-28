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
package com.webank.bcreconcile.db.repository;

import com.webank.bcreconcile.db.entity.TaskInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/6/19
 */
@Repository
public interface TaskInfoRepository extends JpaRepository<TaskInfo, Long>, JpaSpecificationExecutor<TaskInfo> {

    @Modifying
    @Query(value = "select * from task_info where status in (0,1,3) and business_file_name = ?1", nativeQuery = true)
    List<TaskInfo> queryNotOverTaskByBusFileName(String businessFileName);

    List<TaskInfo> findByStatus(int status);

    TaskInfo findByTaskId(String taskId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update task_info set status = ?3 where pk_id = ?1 and status = ?2", nativeQuery = true)
    void updateStatus(long pkId, int from, int to);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update task_info set status = ?3,last_execute_endtime = ?4 where pk_id = ?1 and status = ?2", nativeQuery = true)
    void updateStatus(long pkId, int from, int to, Date lastExecuteEndTime);

    TaskInfo findByPkId(long pkId);
}
