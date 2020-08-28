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
package com.webank.bcreconcile.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * reconciliation task status flow：
 * 1.INIT -> EXECUTING -> SUCCESS
 * 2.INIT -> EXECUTING -> FAILURE -> SUCCESS
 * 3.INIT -> EXECUTING -> FAILURE -> TERMINATE
 *
 * @author wesleywang
 * @Description:TaskStatus
 * @date 2020/6/19
 */
@Getter
@ToString
@AllArgsConstructor
@Slf4j
public enum TaskStatus {

    INIT(0, "waiting for executing"),
    EXECUTING(1, "executing"),
    SUCCESS(2, "execute successfully"),
    FAILURE(3,"execute failed"),
    TERMINATE(4,"execute terminated");


    private int status;
    private String describe;

    public static TaskStatus getByDescribe(String Describe){
        for(TaskStatus type : TaskStatus.values()){
            if(type.describe.equals(Describe)){
                return type;
            }
        }
        log.error("TaskStatus type {} can't be converted.", Describe);
        return null;
    }

    public static TaskStatus getByStatus(int status){
        for(TaskStatus type : TaskStatus.values()){
            if(type.status == status){
                return type;
            }
        }
        log.error("TaskStatus type {} error.", status);
        return null;
    }


}
