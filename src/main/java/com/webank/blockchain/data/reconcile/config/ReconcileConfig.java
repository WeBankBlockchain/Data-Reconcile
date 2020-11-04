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
package com.webank.blockchain.data.reconcile.config;

import cn.hutool.core.collection.CollectionUtil;
import com.webank.blockchain.data.reconcile.utils.FileUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wesleywang
 * @Description:
 * @date 2020-06-11
 */
@Configuration
@ConfigurationProperties("reconcile")
@Data
@Order(1)
@Slf4j
public class ReconcileConfig {

    @Value("${reconcile.task.timer.enable}")
    private boolean timerEnable;

    @Value("${reconcile.task.time.range.days}")
    private int daysTimeRange;

    @Value("${reconcile.task.time.rule}")
    private String taskTimeRule;

    @Value("${reconcile.task.timeout}")
    private long taskTimeout;

    @Value("${reconcile.task.retry.interval.time}")
    private long retryIntervalTime;

    @Value("${reconcile.task.retry.count}")
    private int retryCount;

    @Value("${reconcile.business.name}")
    private String businessName;

    @Value("${reconcile.general.enabled}")
    private Boolean generalEnabled;

    @Value("${reconcile.file.type}")
    private String fileType;

    @Value("${reconcile.field.business.uniqueColumn}")
    private String businessUniqueColumn;

    @Value("${reconcile.field.bc.uniqueColumn}")
    private String bcUniqueColumn;

    @Value("${reconcile.bc.reconcileQuerySql}")
    private String reconcileQuerySql;

    @Value("${reconcile.bc.QueryTimeField}")
    private String reconcileQueryTimeField;

    private Map<String,String> fieldMapping = new HashMap<>();


    @PostConstruct
    private void checkParam(){
        if(!FileUtils.fileTypes.contains(this.fileType)){
            log.error("The current system does not support this format file ：" + this.fileType);
            System.exit(0);
        }
        if (businessName == null) {
            log.error("businessUniqueColumn config is null, please configure the properties in reconcile.properties");
            System.exit(0);
        }
        if (this.getGeneralEnabled()) {
            if (businessUniqueColumn == null) {
                log.error("businessUniqueColumn config is null, please configure the properties in reconcile.properties");
                System.exit(0);
            }
            if (bcUniqueColumn == null) {
                log.error("bcUniqueColumn config is null, please configure the properties in reconcile.properties");
                System.exit(0);
            }
            if (CollectionUtil.isEmpty(fieldMapping)) {
                log.error("the fieldMapping is empty , please configure the properties in reconcile.properties");
                System.exit(0);
            }
        }
    }

}
