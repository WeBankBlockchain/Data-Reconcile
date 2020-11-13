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

import com.webank.blockchain.data.reconcile.filetransfer.FileTransferService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/7/8
 */
public class FileTransferTest extends BaseTests{

    @Autowired
    private FileTransferService transferService;

    @Test
    public void transferServiceTest() throws Exception {
//        transferService.obtainFile("xxxxx_2020-07-06.txt","out/business/xxxx_2020-07-06.txt");
//        transferService.sendFile(new File("out/result/result_xxxx_2020-07-06.txt"));
    }

}
