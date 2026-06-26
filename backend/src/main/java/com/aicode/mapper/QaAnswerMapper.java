package com.aicode.mapper;

import com.aicode.entity.QaAnswer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 问答回答 Mapper
 */
public interface QaAnswerMapper {

    List<QaAnswer> selectByQuestionId(@Param("questionId") Long questionId);

    int countByQuestionId(@Param("questionId") Long questionId);

    int insert(QaAnswer answer);

    int updateRating(@Param("id") Long id, @Param("rating") Integer rating);
}
