package com.xiaoyuervae.blog_halo.repository;

import com.xiaoyuervae.blog_halo.model.domain.Options;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : xiaoyuervae
 * @date : 2017/11/14
 * @version : 1.0
 */
public interface OptionsRepository extends JpaRepository<Options,Long>{

    /**
     * 根据key查询单个option
     *
     * @param key key
     * @return String
     */
    Options findOptionsByOptionName(String key);
}
