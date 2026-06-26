package com.aicode.service;

import com.aicode.entity.Favorite;
import com.aicode.exception.BusinessException;
import com.aicode.mapper.FavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    public void addFavorite(Long userId, String contentType, Long contentId) {
        Favorite exist = favoriteMapper.selectByUserAndTarget(userId, contentType, contentId);
        if (exist != null) throw new BusinessException(400, "已收藏");
        Favorite f = new Favorite();
        f.setUserId(userId); f.setContentType(contentType); f.setContentId(contentId); f.setCreateTime(new Date());
        favoriteMapper.insert(f);
    }

    public void removeFavorite(Long userId, String contentType, Long contentId) {
        favoriteMapper.deleteByUserAndTarget(userId, contentType, contentId);
    }

    public boolean checkFavorite(Long userId, String contentType, Long contentId) {
        return favoriteMapper.selectByUserAndTarget(userId, contentType, contentId) != null;
    }

    public List<Favorite> getUserFavorites(Long userId, int page, int pageSize) {
        return favoriteMapper.selectByUserId(userId, (page-1)*pageSize, pageSize);
    }

    public long countUserFavorites(Long userId) { return favoriteMapper.countByUserId(userId); }
}
