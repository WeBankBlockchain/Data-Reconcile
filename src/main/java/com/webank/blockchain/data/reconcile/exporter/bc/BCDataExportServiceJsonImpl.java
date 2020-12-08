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
package com.webank.blockchain.data.reconcile.exporter.bc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.blockchain.data.reconcile.db.dao.BCInfoDao;
import com.webank.blockchain.data.reconcile.utils.BufferedRandomAccessFile;
import com.webank.blockchain.data.reconcile.utils.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/7/14
 */
@Service
@ConditionalOnProperty(value = "reconcile.file.type", havingValue = "json")
public class BCDataExportServiceJsonImpl implements BCDataExportService{


    @Autowired
    private BCInfoDao bcInfoDao;

    @Override
    public File exportData(String filePath, Date dataRangeBeginTime, Date dataRangeEndTime) throws Exception {
        File file = FileUtils.newFile(filePath);
        Page<Map<String, Object>> page = bcInfoDao.pageQueryBCData(dataRangeBeginTime,
                dataRangeEndTime, 1, 500);
        int total = page.getTotalPages();
        long max = page.getTotalElements();
        BufferedRandomAccessFile writer = new BufferedRandomAccessFile(file, "rw");
        try {
            writer.writeBytes("[\n");
            StringBuilder stringBuilder;
            ObjectMapper mapper = new ObjectMapper();
            int i = 0;
            for (int currentPageNo = 2; currentPageNo <= total + 1; currentPageNo++) {
                List<Map<String, Object>> list = page.getContent();
                stringBuilder = new StringBuilder();
                for (Map<String, Object> map : list) {
                    String str = mapper.writeValueAsString(map);
                    if (++i >= max){
                        stringBuilder.append(str).append(FileUtils.NEWLINE_SYMBOL);
                        continue;
                    }
                    stringBuilder.append(str).append(",").append(FileUtils.NEWLINE_SYMBOL);
                }
                writer.seek(file.length());
                writer.writeBytes(stringBuilder.toString());
                page = bcInfoDao.pageQueryBCData(dataRangeBeginTime, dataRangeEndTime, currentPageNo, 500);
            }
            writer.seek(file.length());
            writer.writeBytes("]");
        }finally {
            IOUtils.closeQuietly(writer);
        }
        return file;
    }
}
