package com.fjm.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-16 上午9:42
 * @Description:
 */
@Data
public class MongoSequence {
    @Id
    private String id;
    private Long seqId;
    private String collName;

}
