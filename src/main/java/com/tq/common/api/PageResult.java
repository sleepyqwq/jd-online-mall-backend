package com.tq.common.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页统一返回体
 * 响应侧 data 内结构应包含 total list pageNum pageSize
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private long total;
    private List<T> list;
    private long pageNum;
    private long pageSize;
}
