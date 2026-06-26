package com.aicode.service;

import com.aicode.ai.AIPromptTemplate;
import com.aicode.ai.AIService;
import com.aicode.dto.CourseResourceVO;
import com.aicode.dto.LearningPathCourseVO;
import com.aicode.dto.LearningPathVO;
import com.aicode.dto.RecommendedPathVO;
import com.aicode.dto.UserProgressVO;
import com.aicode.entity.CourseResource;
import com.aicode.entity.LearningPath;
import com.aicode.entity.LearningPathCourse;
import com.aicode.entity.Tag;
import com.aicode.exception.BusinessException;
import com.aicode.mapper.ContentTagMapper;
import com.aicode.mapper.CourseResourceMapper;
import com.aicode.mapper.LearningPathCourseMapper;
import com.aicode.mapper.LearningPathMapper;
import com.aicode.mapper.UserLearningProgressMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 学习路径服务
 */
@Service
public class LearningPathService {

    private static final Logger log = LoggerFactory.getLogger(LearningPathService.class);

    @Autowired
    private LearningPathMapper learningPathMapper;

    @Autowired
    private LearningPathCourseMapper courseMapper;

    @Autowired
    private UserLearningProgressMapper progressMapper;

    @Autowired
    private ContentTagMapper contentTagMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private AIService aiService;

    @Autowired
    private UserTechTagService userTechTagService;

    @Autowired
    private CourseResourceMapper courseResourceMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String CACHE_KEY_ALL_PATHS = "learning_paths:all";

    /**
     * 获取所有已发布的路径（带 Redis 缓存）
     */
    public List<LearningPathVO> getAllPublishedPaths() {
        String cached = redisService.get(CACHE_KEY_ALL_PATHS);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, LearningPathVO.class));
            } catch (Exception e) {
                log.warn("缓存反序列化失败", e);
            }
        }

        List<LearningPath> paths = learningPathMapper.selectAllPublished();
        List<LearningPathVO> result = paths.stream()
                .map(this::toSimpleVO)
                .collect(Collectors.toList());

        try {
            redisService.set(CACHE_KEY_ALL_PATHS, objectMapper.writeValueAsString(result));
        } catch (JsonProcessingException e) {
            log.warn("缓存序列化失败", e);
        }
        return result;
    }

    /**
     * 分页获取已发布的路径（不含缓存，给列表页使用）
     */
    public List<LearningPathVO> getPublishedPathsPaged(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<LearningPath> paths = learningPathMapper.selectAllPublishedPaged(offset, pageSize);
        return paths.stream().map(this::toSimpleVO).collect(Collectors.toList());
    }

    public long countPublishedPaths() {
        return learningPathMapper.countAllPublished();
    }

    /**
     * 获取学习路径详情
     */
    public LearningPathVO getPathDetail(Long pathId, Long userId) {
        LearningPath path = learningPathMapper.selectById(pathId);
        if (path == null) {
            throw new BusinessException(404, "学习路径不存在");
        }

        List<LearningPathCourse> courses = courseMapper.selectByPathId(pathId);

        // 批量查询用户进度（一次查出所有课程的进度，避免 N+1）
        Map<Long, String> progressMap = new HashMap<>();
        if (userId != null && !courses.isEmpty()) {
            List<Long> courseIds = courses.stream().map(LearningPathCourse::getId).collect(Collectors.toList());
            List<com.aicode.entity.UserLearningProgress> progressList =
                    progressMapper.selectByUserAndCourses(userId, courseIds);
            if (progressList != null) {
                for (com.aicode.entity.UserLearningProgress p : progressList) {
                    progressMap.put(p.getCourseId(), p.getStatus());
                }
            }
        }

        List<LearningPathCourseVO> courseVOs = courses.stream()
                .map(c -> {
                    LearningPathCourseVO vo = toCourseVO(c);
                    String status = progressMap.get(c.getId());
                    if (status != null) {
                        vo.setUserStatus(status);
                    }
                    return vo;
                })
                .collect(Collectors.toList());

        LearningPathVO vo = toSimpleVO(path);
        vo.setCourses(courseVOs);

        if (userId != null) {
            int total = courses.size();
            int completed = progressMapper.countCompletedByUserAndPath(userId, pathId);
            vo.setUserProgress(new UserProgressVO(pathId, total, completed));
        }
        return vo;
    }

    /**
     * 推荐学习路径（基于用户技术标签匹配）
     */
    public List<RecommendedPathVO> getRecommendedPaths(Long userId) {
        List<LearningPath> allPaths = learningPathMapper.selectAllPublished();
        if (allPaths.isEmpty()) return Collections.emptyList();

        // 获取用户技术标签名列表
        List<String> userTagNames = new ArrayList<>();
        if (userId != null) {
            userTagNames = userTechTagService.getUserTechTags(userId).stream()
                    .map(com.aicode.dto.TagVO::getName)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
        }

        // 计算每条路径的匹配度
        List<RecommendedPathVO> recommended = new ArrayList<>();
        for (LearningPath path : allPaths) {
            RecommendedPathVO rvo = new RecommendedPathVO();
            rvo.setId(path.getId());
            rvo.setTitle(path.getTitle());
            rvo.setDescription(path.getDescription());
            rvo.setDifficulty(path.getDifficulty());
            rvo.setEstimatedDays(path.getEstimatedDays());
            rvo.setCoverImage(path.getCoverImage());
            rvo.setCourseCount(path.getCourseCount());

            // 匹配度计算：路径标题/描述和用户标签的重叠
            List<String> matchedTags = new ArrayList<>();
            if (!userTagNames.isEmpty()) {
                String pathText = (path.getTitle() + " " + path.getDescription()).toLowerCase();
                for (String tag : userTagNames) {
                    if (pathText.contains(tag)) {
                        matchedTags.add(tag);
                    }
                }
            }
            rvo.setMatchTags(matchedTags);
            // 匹配分数 = 匹配标签数 / 用户标签总数 * 100
            rvo.setMatchScore(userTagNames.isEmpty() ? 50 :
                    Math.min(100, (double) matchedTags.size() / Math.max(1, userTagNames.size()) * 100));

            recommended.add(rvo);
        }

        // 按匹配度降序
        recommended.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
        return recommended;
    }

    /**
     * 获取课程详情（含 Markdown 内容和资源列表）
     */
    public LearningPathCourse getCourseDetail(Long courseId) {
        LearningPathCourse course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }
        return course;
    }

    /**
     * 获取课程资源列表
     */
    public List<CourseResourceVO> getCourseResources(Long courseId) {
        List<CourseResource> resources = courseResourceMapper.selectByCourseId(courseId);
        if (resources == null || resources.isEmpty()) return Collections.emptyList();
        return resources.stream().map(this::toResourceVO).collect(Collectors.toList());
    }

    /**
     * 获取课程详情 VO（含资源和进度）
     */
    public LearningPathCourseVO getCourseDetailVO(Long courseId, Long userId) {
        LearningPathCourse course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }
        LearningPathCourseVO vo = toCourseVO(course);
        if (userId != null) {
            com.aicode.entity.UserLearningProgress prog =
                    progressMapper.selectByUserAndCourse(userId, courseId);
            if (prog != null) {
                vo.setUserStatus(prog.getStatus());
            }
        }
        return vo;
    }

    // ========== AI 生成 ==========

    /**
     * AI 智能生成学习路径
     * AI 调用和 JSON 解析在事务外，仅数据库写入在事务内
     */
    public LearningPathVO generatePathByAI(String requirement) {
        String prompt = "请根据以下需求生成学习路径：\n" + requirement
                + "\n\n生成" + (requirement.contains("入门") || requirement.contains("初级") ? 6 : 10)
                + "个课程，JSON 格式返回，每个课程包含 200-500 字的 contentMarkdown。";
        // Phase 1: AI 调用（事务外，可能耗时 30-180s）
        String response = aiService.chat(AIPromptTemplate.PATH_GENERATION_SYSTEM, prompt, 180000);

        // Phase 2: 解析 JSON（事务外）
        com.fasterxml.jackson.databind.JsonNode root;
        try {
            root = objectMapper.readTree(response);
        } catch (Exception e) {
            log.error("AI 生成路径解析失败: {}", e.getMessage());
            throw new BusinessException(500, "AI 生成失败，响应解析异常：" + e.getMessage());
        }

        // Phase 3: 保存到数据库（事务内）
        return saveGeneratedPath(root);
    }

    @Transactional
    public LearningPathVO saveGeneratedPath(com.fasterxml.jackson.databind.JsonNode root) {
        try {
            LearningPathVO vo = new LearningPathVO();
            vo.setTitle(root.get("title").asText());
            vo.setDescription(root.get("description").asText());
            vo.setDifficulty(root.has("difficulty") ? root.get("difficulty").asText() : "BEGINNER");
            vo.setEstimatedDays(root.has("estimatedDays") ? root.get("estimatedDays").asInt() : 30);
            vo.setStatus("DRAFT");

            LearningPath path = new LearningPath();
            path.setTitle(vo.getTitle());
            path.setDescription(vo.getDescription());
            path.setDifficulty(vo.getDifficulty());
            path.setEstimatedDays(vo.getEstimatedDays());
            path.setCourseCount(0);
            path.setStatus("DRAFT");
            path.setCreateTime(new Date());
            path.setUpdateTime(new Date());
            learningPathMapper.insert(path);

            com.fasterxml.jackson.databind.JsonNode courses = root.get("courses");
            if (courses != null && courses.isArray()) {
                int idx = 1;
                for (com.fasterxml.jackson.databind.JsonNode c : courses) {
                    LearningPathCourse course = new LearningPathCourse();
                    course.setPathId(path.getId());
                    course.setTitle(c.get("title").asText());
                    course.setDescription(c.get("description").asText());
                    course.setOrderIndex(idx++);
                    String ct = c.has("contentType") ? c.get("contentType").asText() : "ARTICLE";
                    course.setContentType(ct);
                    course.setEstimatedMinutes(c.has("estimatedMinutes") ? c.get("estimatedMinutes").asInt() : 30);
                    course.setContentMarkdown(c.has("contentMarkdown") ? c.get("contentMarkdown").asText() : "");
                    course.setCreateTime(new Date());
                    courseMapper.insert(course);
                }
                learningPathMapper.updateCourseCount(path.getId(), idx - 1);
            }

            redisService.delete(CACHE_KEY_ALL_PATHS);
            log.info("AI 生成学习路径成功: id={}, title={}", path.getId(), vo.getTitle());
            return toSimpleVO(path);
        } catch (Exception e) {
            log.error("AI 生成路径保存失败: {}", e.getMessage());
            throw new BusinessException(500, "保存失败：" + e.getMessage());
        }
    }

    // ========== 管理员方法 ==========

    @Transactional
    public LearningPathVO createPath(LearningPathVO vo) {
        LearningPath path = new LearningPath();
        path.setTitle(vo.getTitle());
        path.setDescription(vo.getDescription());
        path.setDifficulty(vo.getDifficulty() != null ? vo.getDifficulty() : "BEGINNER");
        path.setEstimatedDays(vo.getEstimatedDays());
        path.setCoverImage(vo.getCoverImage());
        path.setCourseCount(0);
        path.setStatus(vo.getStatus() != null ? vo.getStatus() : "DRAFT");
        path.setCreateTime(new Date());
        path.setUpdateTime(new Date());
        learningPathMapper.insert(path);
        redisService.delete(CACHE_KEY_ALL_PATHS);
        return toSimpleVO(path);
    }

    @Transactional
    public void updatePath(LearningPathVO vo) {
        LearningPath path = learningPathMapper.selectById(vo.getId());
        if (path == null) throw new BusinessException(404, "路径不存在");
        path.setTitle(vo.getTitle());
        path.setDescription(vo.getDescription());
        path.setDifficulty(vo.getDifficulty());
        path.setEstimatedDays(vo.getEstimatedDays());
        path.setCoverImage(vo.getCoverImage());
        path.setStatus(vo.getStatus());
        path.setUpdateTime(new Date());
        learningPathMapper.updateById(path);
        redisService.delete(CACHE_KEY_ALL_PATHS);
    }

    @Transactional
    public void deletePath(Long id) {
        LearningPath path = learningPathMapper.selectById(id);
        if (path == null) throw new BusinessException(404, "路径不存在");
        courseMapper.deleteByPathId(id);
        learningPathMapper.deleteById(id);
        redisService.delete(CACHE_KEY_ALL_PATHS);
    }

    @Transactional
    public LearningPathCourseVO addCourse(LearningPathCourseVO vo) {
        if (vo.getOrderIndex() == null || vo.getOrderIndex() < 1 || vo.getOrderIndex() > 99) {
            throw new BusinessException(400, "排序序号范围 1-99");
        }
        LearningPathCourse course = new LearningPathCourse();
        course.setPathId(vo.getPathId());
        course.setTitle(vo.getTitle());
        course.setDescription(vo.getDescription());
        course.setOrderIndex(vo.getOrderIndex());
        course.setContentType(vo.getContentType() != null ? vo.getContentType() : "ARTICLE");
        course.setEstimatedMinutes(vo.getEstimatedMinutes());
        course.setCreateTime(new Date());
        courseMapper.insert(course);

        // 更新路径的课程数
        int count = courseMapper.countByPathId(vo.getPathId());
        learningPathMapper.updateCourseCount(vo.getPathId(), count);
        redisService.delete(CACHE_KEY_ALL_PATHS);

        return toCourseVO(course);
    }

    /**
     * 获取路径下的所有课程（管理员用）
     */
    public List<LearningPathCourseVO> getCoursesByPathId(Long pathId) {
        List<LearningPathCourse> courses = courseMapper.selectByPathId(pathId);
        return courses.stream().map(this::toCourseVO).collect(Collectors.toList());
    }

    @Transactional
    public LearningPathCourseVO updateCourse(LearningPathCourseVO vo) {
        LearningPathCourse course = courseMapper.selectById(vo.getId());
        if (course == null) throw new BusinessException(404, "课程不存在");
        if (vo.getTitle() != null) course.setTitle(vo.getTitle());
        if (vo.getDescription() != null) course.setDescription(vo.getDescription());
        if (vo.getContentType() != null) course.setContentType(vo.getContentType());
        if (vo.getContentMarkdown() != null) course.setContentMarkdown(vo.getContentMarkdown());
        if (vo.getContentUrl() != null) course.setContentUrl(vo.getContentUrl());
        if (vo.getEstimatedMinutes() != null) course.setEstimatedMinutes(vo.getEstimatedMinutes());
        if (vo.getOrderIndex() != null) {
            if (vo.getOrderIndex() < 1 || vo.getOrderIndex() > 99) {
                throw new BusinessException(400, "排序序号范围 1-99");
            }
            course.setOrderIndex(vo.getOrderIndex());
        }
        courseMapper.updateById(course);
        redisService.delete(CACHE_KEY_ALL_PATHS);
        return toCourseVO(course);
    }

    @Transactional
    public void deleteCourse(Long courseId) {
        LearningPathCourse course = courseMapper.selectById(courseId);
        if (course == null) throw new BusinessException(404, "课程不存在");
        Long pathId = course.getPathId();
        courseResourceMapper.deleteByCourseId(courseId);
        courseMapper.deleteById(courseId);
        // 更新路径的课程数
        int count = courseMapper.countByPathId(pathId);
        learningPathMapper.updateCourseCount(pathId, count);
        redisService.delete(CACHE_KEY_ALL_PATHS);
    }

    // ========== 课程资源管理 ==========

    @Transactional
    public CourseResourceVO addCourseResource(CourseResourceVO vo) {
        CourseResource resource = new CourseResource();
        resource.setCourseId(vo.getCourseId());
        resource.setTitle(vo.getTitle());
        resource.setType(vo.getType() != null ? vo.getType() : "LINK");
        resource.setUrl(vo.getUrl());
        resource.setDescription(vo.getDescription());
        resource.setSortOrder(vo.getSortOrder() != null ? vo.getSortOrder() : 0);
        resource.setCreateTime(new Date());
        courseResourceMapper.insert(resource);
        return toResourceVO(resource);
    }

    @Transactional
    public void deleteCourseResource(Long resourceId) {
        courseResourceMapper.deleteById(resourceId);
    }

    public List<LearningPathVO> listAllPaths(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return learningPathMapper.selectAllPaged(offset, pageSize).stream()
                .map(this::toSimpleVO).collect(Collectors.toList());
    }

    public long countAllPaths() {
        return learningPathMapper.countAll();
    }

    // ========== 内部方法 ==========

    private LearningPathVO toSimpleVO(LearningPath path) {
        if (path == null) return null;
        LearningPathVO vo = new LearningPathVO();
        vo.setId(path.getId());
        vo.setTitle(path.getTitle());
        vo.setDescription(path.getDescription());
        vo.setDifficulty(path.getDifficulty());
        vo.setEstimatedDays(path.getEstimatedDays());
        vo.setCoverImage(path.getCoverImage());
        vo.setCourseCount(path.getCourseCount());
        vo.setStatus(path.getStatus());
        vo.setCreateTime(path.getCreateTime());
        return vo;
    }

    private LearningPathCourseVO toCourseVO(LearningPathCourse course) {
        if (course == null) return null;
        LearningPathCourseVO vo = new LearningPathCourseVO();
        vo.setId(course.getId());
        vo.setPathId(course.getPathId());
        vo.setTitle(course.getTitle());
        vo.setDescription(course.getDescription());
        vo.setOrderIndex(course.getOrderIndex());
        vo.setContentType(course.getContentType());
        vo.setContentMarkdown(course.getContentMarkdown());
        vo.setContentUrl(course.getContentUrl());
        vo.setEstimatedMinutes(course.getEstimatedMinutes());
        vo.setUserStatus("NOT_STARTED");
        return vo;
    }

    private CourseResourceVO toResourceVO(CourseResource resource) {
        if (resource == null) return null;
        CourseResourceVO vo = new CourseResourceVO();
        vo.setId(resource.getId());
        vo.setCourseId(resource.getCourseId());
        vo.setTitle(resource.getTitle());
        vo.setType(resource.getType());
        vo.setUrl(resource.getUrl());
        vo.setDescription(resource.getDescription());
        vo.setSortOrder(resource.getSortOrder());
        return vo;
    }
}
