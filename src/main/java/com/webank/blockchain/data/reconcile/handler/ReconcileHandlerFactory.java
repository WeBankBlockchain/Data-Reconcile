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
