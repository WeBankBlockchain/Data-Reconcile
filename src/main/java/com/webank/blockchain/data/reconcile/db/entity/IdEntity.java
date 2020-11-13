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

package com.webank.blockchain.data.reconcile.db.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/6/17
 */
@Data
@MappedSuperclass
@Accessors(chain = true)
public abstract class IdEntity implements Serializable {

    private static final long serialVersionUID = 5903397383140175895L;
    /** @Fields pkId : primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id")
    protected Long pkId;
}
