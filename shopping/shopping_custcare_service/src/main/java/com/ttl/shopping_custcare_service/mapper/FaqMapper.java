package com.ttl.shopping_custcare_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ttl.shopping_common.pojo.Faq;
import org.apache.ibatis.annotations.Mapper;

/**
 * FAQ答案数据访问层
 */
@Mapper
public interface FaqMapper extends BaseMapper<Faq> {
}