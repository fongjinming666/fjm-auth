package com.fjm.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-04-21 上午11:11
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ConfigurationProperties(prefix = "auth.properties")
public class AuthProperties {

    /**
     * 默认30天 授权模式，用户确认授权动作需重新执行.
     */
    private int approval_Time = 30;
}
