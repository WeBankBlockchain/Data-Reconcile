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
package com.webank.blockchain.data.reconcile.task;

import com.webank.blockchain.data.reconcile.entity.ReconcileContext;
import com.webank.blockchain.data.reconcile.enums.TriggerType;
import com.webank.blockchain.data.reconcile.handler.InvocationHandler;
import com.webank.blockchain.data.reconcile.handler.ReconcileHandlerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Timed trigger processing of reconciliation
 *
 * @author wesleywang
 * @Description:
 * @date 2020/6/17
 */
@Component
@Slf4j
@EnableScheduling
@ConditionalOnProperty(value = "reconcile.task.timer.enable", havingValue = "true")
public class ReconcileTaskTimer {

    @Autowired
    private ReconcileHandlerFactory handlerFactory;

    @Scheduled(cron = "${reconcile.task.time.rule}")
    public void execute() {
        InvocationHandler handler = handlerFactory.getInvocationHandler();
        ReconcileContext context = ReconcileContext.builder()
                .triggerType(TriggerType.SCHEDULED).build();
        log.info("reconcile schedule task start , time :" + LocalDate.now().toString());
        try {
            handler.handle(context);
        } catch (Exception e) {
            log.error("reconcile schedule task failed, reason :", e);
        }
    }
}
