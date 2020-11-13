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
