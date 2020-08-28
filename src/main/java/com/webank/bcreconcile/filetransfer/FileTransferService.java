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
package com.webank.bcreconcile.filetransfer;

import java.io.File;
import java.io.IOException;

/**
 * File network transfer interface
 *
 * @Description: FileSender
 * @author graysonzhang
 * @date 2020-06-10 16:48:26
 *
 */
public interface FileTransferService {

    String sendFile(File file) throws Exception;

    File obtainFile(String fileName, String localFilePath) throws IOException;

}
