package com.xiaoyuervae.blog_halo.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : xiaoyuervae
 * @date : 2018/1/3
 * @version : 1.0
 */
@Data
public class Theme implements Serializable {

    /**
     * 主题名称
     */
    private String themeName;

    /**
     * 是否支持设置
     */
    private boolean hasOptions;
}
