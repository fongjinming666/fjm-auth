package com.fjm.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-16 下午4:50
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageQuery {
    @NotNull(message = "当前页面不允许为空")
    private Integer currentPage = 1;
    @NotNull(message = "分页大小不允许为空")
    private Integer pageSize = 20;
}
