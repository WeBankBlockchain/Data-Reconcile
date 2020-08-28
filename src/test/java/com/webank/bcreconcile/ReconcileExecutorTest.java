package com.webank.bcreconcile;

import com.webank.bcreconcile.entity.ReconcileExecuteData;
import com.webank.bcreconcile.entity.ReconcileInfo;
import com.webank.bcreconcile.reconcile.ReconcileExecutor;
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
