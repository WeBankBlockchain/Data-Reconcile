package com.webank.blockchain.data.reconcile;

import com.webank.blockchain.data.reconcile.entity.ReconcileContext;
import com.webank.blockchain.data.reconcile.enums.TriggerType;
import com.webank.blockchain.data.reconcile.handler.InvocationHandler;
import com.webank.blockchain.data.reconcile.handler.ReconcileHandlerFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/6/28
 */
public class ReconcileTaskTest extends BaseTests {


    @Autowired
    private ReconcileHandlerFactory handlerFactory;

    @Test
    public void test() throws Exception {
        InvocationHandler handler = handlerFactory.getInvocationHandler();
        ReconcileContext context = ReconcileContext.builder()
                .triggerType(TriggerType.SCHEDULED).build();
        try {
            handler.handle(context);
        } catch (Exception e) {
//            e.printStackTrace();
        }

       // Thread.sleep(1000000000);
    }
}
