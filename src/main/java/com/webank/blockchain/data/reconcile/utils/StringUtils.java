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
package com.webank.blockchain.data.reconcile.utils;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/7/3
 */
public class StringUtils {

    public static final String EMPTY = "";

    public static final int INDEX_NOT_FOUND = -1;


    public static String substringAfterOfIgnoreCase(String str, String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (isEmpty(separator)) {
            return EMPTY;
        }
        int pos = org.apache.commons.lang3.StringUtils.indexOfIgnoreCase(str,separator);
        if (pos == INDEX_NOT_FOUND || pos == str.length() - separator.length()) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
}
