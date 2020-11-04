/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.blockchain.data.reconcile.exporter.bc;

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
 * @date 2020/6/24
 */
@Service
@ConditionalOnProperty(value = "reconcile.file.type", havingValue = "txt")
public class BCDataExportServiceTxtImpl implements BCDataExportService{

    @Autowired
    private BCInfoDao bcInfoDao;

    @Override
    public File exportData(String filePath, Date dataRangeBeginTime, Date dataRangeEndTime) throws Exception {
        File file = FileUtils.newFile(filePath);
        Page<Map<String, Object>> page = bcInfoDao.pageQueryBCData(dataRangeBeginTime,
                dataRangeEndTime, 1, 500);
        int total = page.getTotalPages();
        BufferedRandomAccessFile writer = new BufferedRandomAccessFile(file, "rw");
        try {
            for (int currentPageNo = 2; currentPageNo <= total + 1; currentPageNo++) {
                List<Map<String, Object>> list = page.getContent();
                FileUtils.exportMapDataByTxtFormat(list, file, writer);
                page = bcInfoDao.pageQueryBCData(dataRangeBeginTime, dataRangeEndTime, currentPageNo, 500);
            }
        }finally {
            IOUtils.closeQuietly(writer);
        }
        return file;
    }
}
