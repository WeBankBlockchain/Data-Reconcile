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
package com.webank.blockchain.data.reconcile.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/6/17
 */
public final class ThreadUtils {

    public static ThreadPoolExecutor executor;

    public static ScheduledExecutorService timer;

    public static ThreadPoolExecutor transfer;

    static {
        executor = new ThreadPoolExecutor(5,20,1000L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(2048));

        timer = Executors.newScheduledThreadPool(4);

        transfer = new ThreadPoolExecutor(2,5,100L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(256));

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                executor.shutdown();
                timer.shutdown();
                transfer.shutdown();
            }
        });
    }

}
