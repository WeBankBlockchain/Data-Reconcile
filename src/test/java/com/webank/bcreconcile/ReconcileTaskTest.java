package com.webank.bcreconcile;

import com.webank.bcreconcile.entity.ReconcileContext;
import com.webank.bcreconcile.enums.TriggerType;
import com.webank.bcreconcile.handler.InvocationHandler;
import com.webank.bcreconcile.handler.ReconcileHandlerFactory;
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
