package com.aicode.service;

import com.aicode.dto.TagVO;
import com.aicode.entity.Tag;
import com.aicode.entity.UserTechTag;
import com.aicode.exception.BusinessException;
import com.aicode.mapper.TagMapper;
import com.aicode.mapper.UserTechTagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户技术标签服务
 */
@Service
public class UserTechTagService {

    @Autowired
    private UserTechTagMapper userTechTagMapper;

    @Autowired
    private TagMapper tagMapper;

    /**
     * 获取用户的技术标签列表
     */
    public List<TagVO> getUserTechTags(Long userId) {
        List<Tag> tags = userTechTagMapper.selectTagsByUserId(userId);
        return tags.stream().map(this::toTagVO).collect(Collectors.toList());
    }

    /**
     * 添加技术标签
     */
    @Transactional
    public void addTechTag(Long userId, Long tagId) {
        // 验证标签存在
        Tag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new BusinessException(404, "标签不存在");
        }

        // 检查是否已添加
        UserTechTag exist = userTechTagMapper.selectByUserAndTag(userId, tagId);
        if (exist != null) {
            throw new BusinessException(400, "已添加该标签");
        }

        UserTechTag utt = new UserTechTag();
        utt.setUserId(userId);
        utt.setTagId(tagId);
        userTechTagMapper.insert(utt);
    }

    /**
     * 删除技术标签
     */
    @Transactional
    public void removeTechTag(Long userId, Long tagId) {
        UserTechTag exist = userTechTagMapper.selectByUserAndTag(userId, tagId);
        if (exist == null) {
            throw new BusinessException(404, "未找到该技术标签");
        }
        userTechTagMapper.deleteByUserAndTag(userId, tagId);
    }

    private TagVO toTagVO(Tag tag) {
        if (tag == null) return null;
        TagVO vo = new TagVO();
        vo.setId(tag.getId());
        vo.setName(tag.getName());
        vo.setCategory(tag.getCategory());
        vo.setColor(tag.getColor());
        vo.setIcon(tag.getIcon());
        vo.setSortOrder(tag.getSortOrder());
        return vo;
    }
}
