package com.aicode.dto;

/**
 * 审查统计数据 VO
 */
public class ReviewStatusCountVO {

    private long total;

    public ReviewStatusCountVO() {}

    public ReviewStatusCountVO(long total) {
        this.total = total;
    }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
}
