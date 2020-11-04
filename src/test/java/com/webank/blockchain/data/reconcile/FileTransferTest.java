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
