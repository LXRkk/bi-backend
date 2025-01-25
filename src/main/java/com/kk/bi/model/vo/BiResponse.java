package com.kk.bi.model.vo;

import lombok.Data;

/**
 * Bi 的返回结果
 *
 * @author : LXRkk
 * @date : 2025/1/23 22:39
 */
@Data
public class BiResponse {

    private String genChart;

    private String genResult;

    private Long chartId;
}
