/**
 * Copyright (C) 2018 WeBank, Inc. All Rights Reserved.
 */

package com.webank.bcreconcile.db.entity;

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
