package com.skk.blog.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageResult<T> implements Serializable {

    private Long total;
    private Long page;
    private Long limit;
    private Long totalPages;
    private java.util.List<T> records;

    public static <T> PageResult<T> of(java.util.List<T> records, Long total, Long page, Long limit) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setPage(page);
        result.setLimit(limit);
        result.setTotalPages((total + limit - 1) / limit);
        return result;
    }
}
