package com.gzhennaxia.accounting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzhennaxia.accounting.pojo.entity.AccTag;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 标签 Mapper。
 *
 * @author Gzhennaxia
 * @date 2026-05-21
 */
public interface AccTagMapper extends BaseMapper<AccTag> {

  /**
   * 统计流水表中引用该标签 ID 的记录数（tags 为逗号分隔 ID）。
   *
   * @param tagIdStr 标签 ID 字符串
   * @return 引用次数
   */
  @Select("""
      SELECT COUNT(*) FROM acc_transaction
      WHERE tags IS NOT NULL AND tags <> ''
        AND (tags = #{tagIdStr}
          OR tags LIKE CONCAT(#{tagIdStr}, ',%')
          OR tags LIKE CONCAT('%,', #{tagIdStr}, ',%')
          OR tags LIKE CONCAT('%,', #{tagIdStr}))
      """)
  long countTransactionUsage(@Param("tagIdStr") String tagIdStr);
}
