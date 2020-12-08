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
package com.webank.blockchain.data.reconcile.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * The assembly and construction of the responsibility chain, and the management of the processor
 *
 * @author wesleywang
 * @Description: ReconcileHandlerFactory
 * @date 2020/6/19
 */
@Service
public class ReconcileHandlerFactory {

    @Autowired
    private List<Handler> chain;

    private InvocationHandler invocationHandler;

    @PostConstruct
    private void initChain(){
         invocationHandler = createInvocationHandler();
    }


    private InvocationHandler createInvocationHandler() {
        InvocationHandler last = null;
        for (int i = chain.size() - 1; i >= 0; i--) {
            Handler handler = chain.get(i);
            InvocationHandler next = last;
            last = context -> handler.invoke(context, next);
        }
        return last;
    }

    public InvocationHandler getInvocationHandler() {
        return invocationHandler;
    }
}
