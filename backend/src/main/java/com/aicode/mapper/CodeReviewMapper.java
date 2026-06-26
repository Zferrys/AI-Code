package com.aicode.mapper;

import com.aicode.entity.CodeReview;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 代码审查 Mapper
 */
public interface CodeReviewMapper {

    CodeReview selectById(@Param("id") Long id);

    List<CodeReview> selectByUserId(@Param("userId") Long userId,
                                    @Param("offset") Integer offset,
                                    @Param("limit") Integer limit);

    long countByUserId(@Param("userId") Long userId);

    long countAll();

    List<CodeReview> selectAllPaged(@Param("offset") Integer offset,
                                    @Param("limit") Integer limit);

    int insert(CodeReview review);

    int updateResult(CodeReview review);

    int updateStatus(@Param("id") Long id, @Param("status") String status);

    int deleteById(@Param("id") Long id);
}
