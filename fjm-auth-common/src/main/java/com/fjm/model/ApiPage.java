package com.fjm.model;

import com.fjm.emun.AdminResult;
import com.fjm.emun.ApiResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.Collections;

/**
 * @Author: jinmingfong
 * @CreateTime: 2020-04-15 21:53
 * @Description:
 */
@Data
@AllArgsConstructor
public class ApiPage<List> extends ApiData<List> implements Serializable {

    //@JsonView(pageLevel.class)
    private long pageNum = 1;

    //@JsonView(pageLevel.class)
    private long pageSize = 10;

    //@JsonView(pageLevel.class)
    private int size;

    //@JsonView(pageLevel.class)
    private long totalNum = 0;

    //@JsonView(pageLevel.class)
    private int pages;

    //@JsonView(pageLevel.class)
    private int prePage;

    //@JsonView(pageLevel.class)
    private int nextPage;

    //@JsonView(pageLevel.class)
    private boolean isFirstPage;

    //@JsonView(pageLevel.class)
    private boolean isLastPage;

    //@JsonView(pageLevel.class)
    private boolean hasPreviousPage;

    //@JsonView(pageLevel.class)
    private boolean hasNextPage;

    /**
     * 构造器
     *
     * @param data
     */
    public ApiPage(List data) {
        super(ApiResult.SUCCESS.getValue(), data);
    }

    /**
     * 构造器
     *
     * @param result
     * @param data
     */
    public ApiPage(Integer result, List data) {
        super(result, data);
    }

    /**
     * 构造器
     *
     * @param result
     * @param pageNum
     * @param pageSize
     * @param data
     */
    public ApiPage(Integer result, long pageNum, long pageSize, long totalNum, List data) {
        super(result, data);
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalNum = totalNum;
        if (this.pageSize > 0) {
            this.pages = (int) ((this.totalNum / this.pageSize) + (this.totalNum % this.pageSize > 0 ? 1 : 0));
        }
        this.hasNextPage = this.totalNum > (this.pageNum * this.pageSize);
        this.hasPreviousPage = (pageNum > 1);
        this.isFirstPage = pageNum == 1;
        this.isLastPage = (totalNum <= (this.pageNum * this.pageSize));
    }

    /**
     * 构造器
     *
     * @param result
     * @param pageNum
     * @param pageSize
     * @param size
     * @param totalNum
     * @param data
     */
    public ApiPage(Integer result, long pageNum, long pageSize, long size, long totalNum, List data) {
        super(result, data);
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalNum = totalNum;
        this.size = (int) size;
        if (this.pageSize > 0) {
            this.pages = (int) ((this.totalNum / this.pageSize) + (this.totalNum % this.pageSize > 0 ? 1 : 0));
        }
        this.hasNextPage = this.totalNum > (this.pageNum * this.pageSize);
        this.hasPreviousPage = (pageNum > 1);
        this.isFirstPage = pageNum == 1;
        this.isLastPage = (totalNum <= (this.pageNum * this.pageSize));
    }

    /**
     * 构造器
     *
     * @param pageNum
     * @param pageSize
     * @param totalNum
     * @param data
     * @return
     */
    public static ApiPage apiPageOk(long pageNum, long pageSize, long size, long totalNum, Object data) {
        return new ApiPage<>(ApiResult.SUCCESS.getValue(), pageNum, pageSize, size, totalNum, data);
    }

    /**
     * 空列表
     *
     * @return
     */
    public static ApiPage apiPageOk() {
        return new ApiPage<>(ApiResult.SUCCESS.getValue(), 1, 10, 0, 0, Collections.emptyList());
    }

    /**
     * @param page
     * @return
     */
    public static ApiPage apiPageOk(Page page) {
        if (page == null) {
            return apiPageOk();
        }
        return apiPageOk(page.getPageable().getPageNumber(), page.getPageable().getPageSize(),
                (page.getContent() == null || page.getContent().size() == 0) ? 0 : page.getContent().size(),
                page.getTotalElements(), page.getContent());
    }

    /**
     * 构造器
     *
     * @param pageNum
     * @param pageSize
     * @param totalNum
     * @param data
     * @return
     */
    public static ApiPage adminPageOk(long pageNum, long pageSize, long size, long totalNum, Object data) {
        return new ApiPage<>(AdminResult.SUCCESS.getValue(), pageNum, pageSize, size, totalNum, data);
    }

    /**
     * 空列表
     *
     * @return
     */
    public static ApiPage adminPageOk() {
        return new ApiPage<>(AdminResult.SUCCESS.getValue(), 1, 10, 0, 0, Collections.emptyList());
    }

    /**
     * @param page
     * @return
     */
    public static ApiPage adminPageOk(Page page) {
        if (page == null) {
            return adminPageOk();
        }
        return adminPageOk(page.getPageable().getPageNumber() + 1, page.getPageable().getPageSize(),
                (page.getContent() == null || page.getContent().size() == 0) ? 0 : page.getContent().size(),
                page.getTotalElements(), page.getContent());
    }
}
