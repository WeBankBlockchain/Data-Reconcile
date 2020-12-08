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
package com.webank.blockchain.data.reconcile.reconcile;

import com.webank.blockchain.data.reconcile.config.ReconcileConfig;
import com.webank.blockchain.data.reconcile.entity.ReconcileExecuteData;
import com.webank.blockchain.data.reconcile.entity.ReconcileResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/6/24
 */
@ConditionalOnProperty(value = "reconcile.general.enabled", havingValue = "true")
@Component
@Slf4j
public class ReconcileExecutorImpl implements ReconcileExecutor {

    @Autowired
    private ReconcileConfig reconcileConfig;

    @Override
    public ReconcileResult execute(ReconcileExecuteData executeData) {
        Map<String, String> fieldMapping = reconcileConfig.getFieldMapping();
        ReconcileResult reconcileResult = new ReconcileResult();
        reconcileResult.setId(executeData.getId());
        reconcileResult.setSuccess(true);
        StringBuilder builder = new StringBuilder("failed reason : ");
        if (!executeData.isMatchById()) {
            builder.append("No matching unique key was found");
            reconcileResult.setSuccess(false);
        } else {
            Map<String, String> bcFieldMap = executeData.getBcOrg().getFieldMap();
            Map<String, String> busFieldMap = executeData.getBusOrg().getFieldMap();
            for (Map.Entry<String, String> entry : bcFieldMap.entrySet()) {
                String key = entry.getKey();
                if (fieldMapping.containsKey(key)) {
                    String keyMapping = fieldMapping.get(key);
                    String value = entry.getValue();
                    String valueMapping = busFieldMap.get(keyMapping);
                    if (value != null && !value.equals(valueMapping)) {
                        builder.append(key).append(" = ").append(entry.getValue()).append(", ")
                                .append(keyMapping).append(" = ").append(valueMapping).append("; ");
                        reconcileResult.setSuccess(false);
                    }
                }
            }
        }
        if (!reconcileResult.isSuccess()) {
            reconcileResult.setState("failed");
            reconcileResult.setNote(builder.toString());
            log.info("reconcile data id = " + executeData.getId() + " reconcile failed, reason:" + reconcileResult.getNote());
        } else {
            reconcileResult.setState("success");
            log.info("reconcile data id = " + executeData.getId() + " reconcile success ");
        }
        return reconcileResult;
    }
}
