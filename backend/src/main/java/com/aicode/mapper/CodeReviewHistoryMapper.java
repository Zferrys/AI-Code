package com.aicode.mapper;

import com.aicode.entity.CodeReviewHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 代码审查历史 Mapper
 */
public interface CodeReviewHistoryMapper {

    List<CodeReviewHistory> selectByReviewId(@Param("reviewId") Long reviewId);

    int insert(CodeReviewHistory history);
}
