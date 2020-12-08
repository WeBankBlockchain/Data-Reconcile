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

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.google.common.collect.Lists;
import com.webank.blockchain.data.reconcile.entity.ReconcileContext;
import com.webank.blockchain.data.reconcile.exception.ReconcileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/6/28
 */
@Slf4j
public final class FileUtils {

    public static String BC_RECONCILE_FILEPATH;

    public static String BUS_RECONCILE_FILEPATH;

    public static String RESULT_RECONCILE_FILEPATH;

    public static final String TXT_FILE_SUFFIX = ".txt";

    public static final String JSON_FILE_SUFFIX = ".json";

    public static final String TXT_FILE_SPLIT = "#_#";

    public static final String NEWLINE_SYMBOL = "\n";

    public static String fileDir;

    public static final List<String> fileTypes = Lists.newArrayList("txt","json");

    static {
        fileDir = Paths.get(System.getProperty("user.dir"), "out").toString();
        Path path = Paths.get(fileDir);
        if(!Files.exists(path)) {
            try {
                Files.createDirectory(Paths.get(fileDir));
            } catch (IOException e) {
                log.error("create file failed , reason : " , e);
            }
        }
        BC_RECONCILE_FILEPATH = fileDir + "/bc/";
        BUS_RECONCILE_FILEPATH = fileDir + "/business/";
        RESULT_RECONCILE_FILEPATH = fileDir +  "/result/";
        mkDir(BC_RECONCILE_FILEPATH);
        mkDir(BUS_RECONCILE_FILEPATH);
        mkDir(RESULT_RECONCILE_FILEPATH);
    }

    public static boolean delFile(String filePath){
        if (StringUtils.isBlank(filePath)){
            return true;
        }
        File file = new File(filePath);
        return FileUtil.del(file);
    }

    public static boolean delFile(File file){
        return FileUtil.del(file);
    }


    public static boolean mkDir(String dir){
        if (StringUtils.isBlank(dir)){
            return true;
        }
        File file = new File(dir);
        if (!file.exists()) {
            return file.mkdir();
        }
        return true;
    }

    public static File file(String path){
        File file = new File(path);
        if (!file.exists()){
            try {
                if(file.createNewFile()){
                    return file;
                }
            } catch (IOException e) {
                log.error("create file failed , reason : " , e);
            }
        }
        return file;
    }

    public static File newFile(String path){
        delFile(path);
        return file(path);
    }

    public static void clearReconcileFileCache(ReconcileContext context){
        FileUtils.delFile(context.getBusinessReconFile());
        FileUtils.delFile(context.getBcReconFile());
        FileUtils.delFile(context.getResultFile());
    }

    public static <T> File exportDataByTxtFormat(List<T> dataList, String filePath) throws Exception {
        return exportDataByTxtFormat(dataList,filePath,false);
    }


    public static <T> File exportDataByTxtFormat(List<T> dataList, String filePath, boolean append) throws Exception {
        if (!append){
            delFile(filePath);
        }
        File file = file(filePath);
        if (CollectionUtil.isEmpty(dataList)){
            return file;
        }
        Object object = dataList.get(0);
        Class cls = object.getClass();
        Field[] fields = cls.getDeclaredFields();
        if (append) {
            BufferedRandomAccessFile reader = new BufferedRandomAccessFile(file, "r");
            String headLine = reader.readLine();
            if (headLine != null) {
                String[] heads = headLine.split(TXT_FILE_SPLIT);
                if (heads.length != fields.length - 1) {
                    throw new ReconcileException("The number of data class attributes is not equal to the number of source file fields");
                }
                HashMap<String, Field> fieldMap = new HashMap<>();
                for (Field field : fields) {
                    if ("this$0".equals(field.getName())){
                        continue;
                    }
                    fieldMap.put(field.getName(), field);
                }
                for (int i = 0; i < heads.length; i++) {
                    String head = heads[i];
                    if (!fieldMap.containsKey(head)) {
                        throw new ReconcileException("No matching field found in class property collection, field name =" + head);
                    }
                    fields[i] = fieldMap.get(head);
                }
            }else {
                append = false;
            }
        }
        StringBuilder builder = new StringBuilder();
        if (!append) {
            for (Field field : fields) {
                if ("this$0".equals(field.getName())){
                    continue;
                }
                builder.append(field.getName()).append(TXT_FILE_SPLIT);
            }
            builder.append(NEWLINE_SYMBOL);
        }
        for (Object data : dataList) {
            for (Field field : fields) {
                if ("this$0".equals(field.getName())){
                    continue;
                }
                field.setAccessible(true);
                Object value = field.get(data);
                builder.append(value == null ? "null" : value.toString()).append(TXT_FILE_SPLIT);
            }
            builder.append(NEWLINE_SYMBOL);
        }
        BufferedRandomAccessFile writer = new BufferedRandomAccessFile(file,"rw");
        try {
            writer.seek(file.length());
            writer.writeBytes(builder.toString());
        }finally {
            IOUtils.closeQuietly(writer);
        }
        return file;
    }


    public static File exportMapDataByTxtFormat(List<Map<String, Object>> list, String filePath, boolean append)
            throws Exception {
        if (!append){
            delFile(filePath);
        }
        File file = file(filePath);
        if (CollectionUtil.isEmpty(list)){
            return file;
        }
        boolean hasFirstLine = file.length() != 0;
        StringBuilder builder = new StringBuilder();
        for (Map<String, Object> objectMap : list) {
            if (!hasFirstLine) {
                for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
                    builder.append(entry.getKey()).append(TXT_FILE_SPLIT);
                }
                hasFirstLine = true;
            } else {
                for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
                    builder.append(entry.getValue().toString()).append(TXT_FILE_SPLIT);
                }
            }
            builder.append(NEWLINE_SYMBOL);
        }
        BufferedRandomAccessFile writer = new BufferedRandomAccessFile(file, "rw");
        try {
            writer.seek(file.length());
            writer.writeBytes(builder.toString());
        }finally {
            IOUtils.closeQuietly(writer);
        }
        return file;
    }

    public static void exportMapDataByTxtFormat(List<Map<String, Object>> list, File file,
                                                BufferedRandomAccessFile writer)
            throws Exception {
        if (CollectionUtil.isEmpty(list)){
            return;
        }
        boolean hasFirstLine = file.length() != 0;
        StringBuilder builder = new StringBuilder();
        for (Map<String, Object> objectMap : list) {
            if (!hasFirstLine) {
                for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
                    builder.append(entry.getKey()).append(TXT_FILE_SPLIT);
                }
                builder.append(NEWLINE_SYMBOL);
                for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
                    builder.append(entry.getValue().toString()).append(TXT_FILE_SPLIT);
                }
                hasFirstLine = true;
            } else {
                for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
                    builder.append(entry.getValue().toString()).append(TXT_FILE_SPLIT);
                }
            }
            builder.append(NEWLINE_SYMBOL);
        }
        writer.seek(file.length());
        writer.writeBytes(builder.toString());
    }

}
