package com.fjm.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-16 上午9:49
 * @Description:
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity implements Serializable {

    public static final String ID = "id";
    @Id
    @Fixed
    protected String id;
}
