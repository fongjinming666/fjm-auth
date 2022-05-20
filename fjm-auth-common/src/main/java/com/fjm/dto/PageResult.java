package com.fjm.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: jinmingfong
 * @CreateTime: 2021-03-16 下午4:50
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResult<T> {

    /**
     * 页码，从1开始
     */
    private Integer pageNum;

    /**
     * 页面大小
     */
    private Integer pageSize;


    /**
     * 总数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 数据
     */
    private List<T> list;

    /**
     * 构造器
     *
     * @param pageNum
     * @param pageSize
     */
    public PageResult(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    /**
     * @param pageNum
     * @param pageSize
     * @param list
     */
    public PageResult(Integer pageNum, Integer pageSize, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.list = list;
    }

    /**
     * 构造器
     *
     * @param pageNum
     * @param pageSize
     * @param total
     * @param list
     */
    public PageResult(Integer pageNum, Integer pageSize, Long total, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
    }
}
