package com.aicode.service;

import com.aicode.dto.TagVO;
import com.aicode.entity.Tag;
import com.aicode.exception.BusinessException;
import com.aicode.mapper.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签服务
 */
@Service
public class TagService {

    @Autowired
    private TagMapper tagMapper;

    /**
     * 获取所有标签
     */
    public List<TagVO> getAllTags() {
        return tagMapper.selectAll().stream()
                .map(this::toTagVO)
                .collect(Collectors.toList());
    }

    /**
     * 按分类获取标签
     */
    public List<TagVO> getTagsByCategory(String category) {
        return tagMapper.selectByCategory(category).stream()
                .map(this::toTagVO)
                .collect(Collectors.toList());
    }

    /**
     * 获取单个标签
     */
    public TagVO getTagById(Long id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException(404, "标签不存在");
        }
        return toTagVO(tag);
    }

    /**
     * 添加标签
     */
    public TagVO addTag(String name, String category, String color) {
        Tag exist = tagMapper.selectByName(name);
        if (exist != null) {
            throw new BusinessException(400, "标签已存在");
        }
        Tag tag = new Tag();
        tag.setName(name);
        tag.setCategory(category);
        tag.setColor(color != null ? color : "#1a56db");
        tag.setCreateTime(new Date());
        tagMapper.insert(tag);
        return toTagVO(tag);
    }

    /**
     * 删除标签
     */
    public void deleteTag(Long id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException(404, "标签不存在");
        }
        tagMapper.deleteById(id);
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
