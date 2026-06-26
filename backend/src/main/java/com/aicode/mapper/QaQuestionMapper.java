package com.aicode.mapper;

import com.aicode.entity.QaQuestion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 问答问题 Mapper
 */
public interface QaQuestionMapper {

    QaQuestion selectById(@Param("id") Long id);

    List<QaQuestion> selectByUserId(@Param("userId") Long userId,
                                    @Param("offset") Integer offset,
                                    @Param("limit") Integer limit);

    long countByUserId(@Param("userId") Long userId);

    long countAll();

    List<QaQuestion> selectAllPaged(@Param("offset") Integer offset,
                                    @Param("limit") Integer limit);

    List<QaQuestion> search(@Param("keyword") String keyword,
                            @Param("category") String category,
                            @Param("offset") Integer offset,
                            @Param("limit") Integer limit);

    long countSearch(@Param("keyword") String keyword,
                     @Param("category") String category);

    int insert(QaQuestion question);

    int updateById(QaQuestion question);

    int incrementViewCount(@Param("id") Long id);

    int deleteById(@Param("id") Long id);
}
