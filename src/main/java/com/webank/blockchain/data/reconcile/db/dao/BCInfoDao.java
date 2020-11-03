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
package com.webank.blockchain.data.reconcile.db.dao;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.webank.blockchain.data.reconcile.config.ReconcileConfig;
import com.webank.blockchain.data.reconcile.utils.StringUtils;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/6/22
 */
@Service
@Transactional(readOnly = true)
public class BCInfoDao {

    @Autowired
    private ReconcileConfig reconcileConfig;
    @PersistenceContext
    private EntityManager entityManager;

    private String bcDataQuerySql;

    private String bcDataCountSql;

    @PostConstruct
    private void sqlInit(){
        bcDataQuerySql = reconcileConfig.getReconcileQuerySql();
        bcDataCountSql = "select count(1) from " + StringUtils.substringAfterOfIgnoreCase(bcDataQuerySql,"from");
    }

    public Page<Map<String,Object>> pageQueryBCData(Date beginTime, Date endTime, int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo-1,pageSize);
        String begin = DateUtil.format(beginTime, DatePattern.NORM_DATETIME_PATTERN);
        String end = DateUtil.format(endTime, DatePattern.NORM_DATETIME_PATTERN);
        String timeCondition = " and " + reconcileConfig.getReconcileQueryTimeField() +
                " between " + "'" + begin + "' and '" + end + "'";
        String countSql = bcDataCountSql + timeCondition;
        Query countQuery = entityManager.createNativeQuery(countSql);
        long total = ((BigInteger) countQuery.getSingleResult()).longValue();
        if (total == 0){
            return new PageImpl<>(Lists.newArrayList(), pageable, total);
        }
        String dataSql = bcDataQuerySql + timeCondition;
        Query dataQuery = entityManager.createNativeQuery(dataSql);
        dataQuery.setFirstResult((int) pageable.getOffset());
        dataQuery.setMaxResults(pageable.getPageSize());
        dataQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> resultList = dataQuery.getResultList();
        return new PageImpl<>(resultList,pageable,total);
    }


}
