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
package com.webank.blockchain.data.reconcile;

import com.webank.blockchain.data.reconcile.entity.ReconcileExecuteData;
import com.webank.blockchain.data.reconcile.entity.ReconcileInfo;
import com.webank.blockchain.data.reconcile.reconcile.ReconcileExecutor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/7/8
 */
public class ReconcileExecutorTest extends BaseTests{


    @Autowired
    private ReconcileExecutor reconcileExecutor;

    @Test
    public void reconcileExecutorTest() throws Exception {
        ReconcileExecuteData reconcileExecuteData = new ReconcileExecuteData();
        ReconcileInfo busOrg = new ReconcileInfo();
        Map<String,String> fieldMap1 = new HashMap<>();
        fieldMap1.put("id","1");
        fieldMap1.put("name","wang");
        busOrg.setId("1");
        busOrg.setFieldMap(fieldMap1);

        ReconcileInfo bcOrg = new ReconcileInfo();
        Map<String,String> fieldMap2 = new HashMap<>();
        fieldMap2.put("id","1");
        fieldMap2.put("name","wang");
        bcOrg.setId("1");
        bcOrg.setFieldMap(fieldMap2);
        reconcileExecuteData.setBusOrg(busOrg);
        reconcileExecuteData.setId("1");
        reconcileExecuteData.setBcOrg(bcOrg);

        reconcileExecutor.execute(reconcileExecuteData);
    }

}
