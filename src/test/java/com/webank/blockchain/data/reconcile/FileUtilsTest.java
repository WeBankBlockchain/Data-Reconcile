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

import com.webank.blockchain.data.reconcile.utils.FileUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/7/8
 */
public class FileUtilsTest  extends BaseTests {

    @Test
    public void exportDataUtilTest() throws Exception {
        List<ExportData> dataList = new ArrayList<>();
        ExportData data1 = new ExportData(1, "wew", 2);
        ExportData data2 = new ExportData(2, "ds", 4);
        dataList.add(data1);
        dataList.add(data2);
        FileUtils.exportDataByTxtFormat(dataList,"out/export.txt");
    }

    static class ExportData{

        long amount;

        String name;

        int age;

        String note;

        public ExportData(long amount, String name, int age) {
            this.amount = amount;
            this.name = name;
            this.age = age;
        }
    }
}
