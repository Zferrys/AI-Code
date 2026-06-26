-- =============================================
-- 学习路径种子数据
-- 基于主流技术栈的综合性学习路线
-- =============================================

-- 1. 补充标签
INSERT IGNORE INTO tag (name, category, color, sort_order, create_time) VALUES
('微服务', 'TOPIC', '#7c3aed', 7, NOW()),
('Spring Cloud', 'FRAMEWORK', '#6db33f', 6, NOW()),
('Django', 'FRAMEWORK', '#092e20', 7, NOW()),
('Flask', 'FRAMEWORK', '#000000', 8, NOW()),
('数据分析', 'TOPIC', '#059669', 8, NOW()),
('数据库', 'TOPIC', '#4479a1', 9, NOW());

-- =============================================
-- 路径1: Java 后端开发从零到精通
-- =============================================
INSERT INTO learning_path (title, description, difficulty, estimated_days, course_count, status, create_time, update_time)
VALUES ('Java 后端开发从零到精通',
'从零开始系统学习 Java 后端开发。涵盖 Java 核心语法、面向对象编程、集合框架、IO流、多线程、JVM 基础，再到 MySQL 数据库、JDBC、MyBatis、Spring Boot 等企业级框架，配合 20+ 实战项目循序渐进。学完可达初级后端开发工程师水平。',
'BEGINNER', 90, 12, 'PUBLISHED', NOW(), NOW());

SET @path1_id = LAST_INSERT_ID();

INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, create_time) VALUES
(@path1_id, 'Java 基础语法与开发环境搭建', '安装 JDK、配置环境变量、IDE 使用、第一个 Java 程序、基本数据类型、运算符、流程控制', 1, 'ARTICLE', 60, NOW()),
(@path1_id, '面向对象编程核心', '类与对象、封装继承多态、接口与抽象类、内部类、Lambda 表达式入门', 2, 'VIDEO', 90, NOW()),
(@path1_id, 'Java 集合框架深入', 'List/Set/Map 体系详解、ArrayList vs LinkedList、HashMap 原理、TreeMap、Collections 工具类、Stream API', 3, 'ARTICLE', 120, NOW()),
(@path1_id, 'IO流与文件操作', '字节流/字符流、缓冲流、转换流、对象序列化、NIO 基础、Files/Paths 工具类', 4, 'ARTICLE', 60, NOW()),
(@path1_id, '多线程与并发编程', 'Thread/Runnable、线程池、synchronized、Lock、volatile、CAS、ConcurrentHashMap、JUC 工具类', 5, 'VIDEO', 120, NOW()),
(@path1_id, 'JVM 内存模型与性能调优入门', 'JVM 内存分区、垃圾回收算法、常见 GC、类加载机制、jvisualvm/jstack 调优工具', 6, 'ARTICLE', 90, NOW()),
(@path1_id, 'MySQL 数据库从入门到实践', '数据库设计范式、CRUD、多表 JOIN、聚合函数、索引原理、事务与隔离级别、EXPLAIN 调优', 7, 'VIDEO', 120, NOW()),
(@path1_id, 'JDBC 与连接池', 'JDBC 原生操作、PreparedStatement 防注入、Druid/HikariCP 连接池、数据库事务操作', 8, 'ARTICLE', 60, NOW()),
(@path1_id, 'MyBatis 框架精讲', 'XML 映射、动态 SQL、结果映射、缓存机制、PageHelper 分页、MyBatis Generator 代码生成', 9, 'VIDEO', 90, NOW()),
(@path1_id, 'Spring Boot 快速入门', 'Spring IoC/DI、Bean 生命周期、配置管理、自动装配原理、Spring Boot Starter、Actuator 监控', 10, 'ARTICLE', 120, NOW()),
(@path1_id, 'RESTful API 开发实战', '@RestController、请求参数处理、统一响应格式、全局异常处理、参数校验、Swagger 接口文档', 11, 'CODING', 90, NOW()),
(@path1_id, '项目实战：企业级用户管理系统', '综合运用 Spring Boot + MyBatis + MySQL + Redis 构建完整 CRUD 项目、JWT 认证、拦截器、日志 AOP', 12, 'CODING', 180, NOW());

-- 路径1标签关联
INSERT INTO content_tag (tag_id, content_type, content_id)
SELECT t.id, 'PATH', @path1_id FROM tag t WHERE t.name IN ('Java', 'Spring Boot', 'MySQL', 'MyBatis');

-- =============================================
-- 路径2: Spring Boot 微服务架构实战
-- =============================================
INSERT INTO learning_path (title, description, difficulty, estimated_days, course_count, status, create_time, update_time)
VALUES ('Spring Boot 微服务架构实战',
'深入微服务架构设计与实践。从单体应用到微服务拆分，学习 Spring Cloud 全家桶（Nacos 注册中心、Gateway 网关、Sentinel 熔断降级、Feign 远程调用、Sleuth 链路追踪），掌握 Docker 容器化部署、K8s 编排、分布式事务 Seata 等生产级核心技术。适合 1-3 年经验的 Java 开发者进阶。',
'ADVANCED', 50, 10, 'PUBLISHED', NOW(), NOW());

SET @path2_id = LAST_INSERT_ID();

INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, create_time) VALUES
(@path2_id, '微服务架构设计原则', '单体架构 vs 微服务、DDD 限界上下文、服务拆分策略、微服务通信模式、API 网关模式', 1, 'ARTICLE', 60, NOW()),
(@path2_id, 'Spring Cloud Nacos 注册中心与配置中心', 'Nacos 安装部署、服务注册与发现、配置管理、命名空间隔离、集群搭建', 2, 'VIDEO', 90, NOW()),
(@path2_id, 'Spring Cloud Gateway 网关', '路由断言、过滤器链、自定义过滤器、限流熔断、跨域配置、网关聚合 Swagger', 3, 'ARTICLE', 90, NOW()),
(@path2_id, 'OpenFeign 远程调用与负载均衡', '声明式 HTTP 客户端、自定义配置、熔断降级整合 Sentinel、请求拦截器、日志增强', 4, 'CODING', 60, NOW()),
(@path2_id, 'Sentinel 流量控制与熔断降级', '流量整形、熔断策略、热点参数限流、系统自适应保护、规则持久化、控制台监控', 5, 'VIDEO', 90, NOW()),
(@path2_id, '分布式事务 Seata 实战', 'AT 模式原理、TCC 模式、Seata Server 搭建、XA 模式、分布式事务最佳实践与避坑', 6, 'ARTICLE', 120, NOW()),
(@path2_id, 'Spring Cloud Sleuth 链路追踪', '分布式链路追踪原理、Zipkin 集成、ELK 日志收集、SkyWalking 可视化监控', 7, 'ARTICLE', 60, NOW()),
(@path2_id, 'Docker 容器化部署', 'Dockerfile 编写、Docker Compose 编排、镜像优化、多阶段构建、私有仓库搭建', 8, 'VIDEO', 90, NOW()),
(@path2_id, 'Kubernetes 微服务编排', 'K8s 核心概念、Pod/Service/Deployment、ConfigMap/Secret、Ingress 控制器、Helm 包管理', 9, 'VIDEO', 120, NOW()),
(@path2_id, '项目实战：电商订单平台', '从零搭建完整微服务电商系统：用户服务、商品服务、订单服务、支付服务、分布式事务保证最终一致性', 10, 'CODING', 240, NOW());

INSERT INTO content_tag (tag_id, content_type, content_id)
SELECT t.id, 'PATH', @path2_id FROM tag t WHERE t.name IN ('Spring Boot', '微服务', 'Docker', 'Spring Cloud');

-- =============================================
-- 路径3: Python 全栈开发从入门到项目
-- =============================================
INSERT INTO learning_path (title, description, difficulty, estimated_days, course_count, status, create_time, update_time)
VALUES ('Python 全栈开发从入门到项目',
'系统学习 Python 全栈开发。从 Python 语法基础到 Web 开发（Flask/Django），再到数据分析与 AI 应用。覆盖 RESTful API 设计、ORM 数据库操作、前端模板渲染、数据可视化等核心技能。适合零基础入门但希望直达实战的开发者。',
'BEGINNER', 60, 10, 'PUBLISHED', NOW(), NOW());

SET @path3_id = LAST_INSERT_ID();

INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, create_time) VALUES
(@path3_id, 'Python 快速入门', '环境搭建、语法基础、数据类型、函数定义、模块导入、包管理 pip、虚拟环境', 1, 'ARTICLE', 60, NOW()),
(@path3_id, 'Python 面向对象与高级特性', '类与继承、魔术方法、装饰器、生成器、上下文管理器、类型注解', 2, 'ARTICLE', 90, NOW()),
(@path3_id, 'Flask Web 框架入门', '路由与视图、模板引擎 Jinja2、表单处理、Session/Cookie、RESTful API 构建', 3, 'VIDEO', 90, NOW()),
(@path3_id, 'Flask 实战：博客系统', 'SQLAlchemy ORM、用户认证、CRUD 操作、Markdown 渲染、分页、部署', 4, 'CODING', 120, NOW()),
(@path3_id, 'Django 框架精讲', 'MTV 架构模式、ORM 模型、Admin 后台、Class-based Views、Django REST Framework', 5, 'VIDEO', 120, NOW()),
(@path3_id, 'Django 实战：在线教育平台', 'DRF 构建 API、JWT 认证、权限控制、文件上传、Celery 异步任务、Redis 缓存', 6, 'CODING', 180, NOW()),
(@path3_id, 'Python 数据分析基础', 'NumPy 数组运算、Pandas 数据处理、Matplotlib/Seaborn 数据可视化、数据清洗', 7, 'ARTICLE', 90, NOW()),
(@path3_id, 'Python 爬虫与 API 交互', 'Requests/httpx 库、BeautifulSoup 解析、Scrapy 框架、反爬应对策略、API 数据采集', 8, 'VIDEO', 90, NOW()),
(@path3_id, '自动化测试与部署', 'unittest/pytest 测试框架、CI/CD 入门、Docker 部署 Flask/Django、Nginx 反向代理', 9, 'ARTICLE', 60, NOW()),
(@path3_id, '项目实战：全栈数据分析平台', '整合 Django + DRF + Vue.js 前端 + Pandas 分析引擎，构建完整数据可视化平台', 10, 'CODING', 240, NOW());

INSERT INTO content_tag (tag_id, content_type, content_id)
SELECT t.id, 'PATH', @path3_id FROM tag t WHERE t.name IN ('Python', 'Django', 'Flask', '数据分析');

-- =============================================
-- 路径4: 前端工程化与架构演进
-- =============================================
INSERT INTO learning_path (title, description, difficulty, estimated_days, course_count, status, create_time, update_time)
VALUES ('前端工程化与架构演进',
'从基础到前沿的现代前端进阶路线。深入 JavaScript/TypeScript 核心，掌握 Vue 3/React 18 框架原理，学习组件化设计、状态管理、路由懒加载、Vite 构建优化、微前端架构等工程化体系。适合 1-2 年前端经验的开发者进阶。',
'INTERMEDIATE', 40, 10, 'PUBLISHED', NOW(), NOW());

SET @path4_id = LAST_INSERT_ID();

INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, create_time) VALUES
(@path4_id, 'JavaScript 进阶与 ES6+', '作用域与闭包、Promise/async-await、Generator、Proxy/Reflect、模块化规范、Symbol/Map/Set 深入', 1, 'ARTICLE', 90, NOW()),
(@path4_id, 'TypeScript 从入门到精通', '类型系统、泛型、装饰器、类型体操、声明文件、tsconfig 配置、React/Vue 中的 TS 实践', 2, 'VIDEO', 120, NOW()),
(@path4_id, 'Vue 3 组合式 API 深入', 'ref/reactive、computed/watch、生命周期、自定义 Hooks、Teleport/Suspense、Pinia 状态管理', 3, 'VIDEO', 120, NOW()),
(@path4_id, 'React 18 核心原理', 'JSX 本质、Fiber 架构、Hooks 原理、Context、useReducer、Suspense、React Router 6', 4, 'ARTICLE', 120, NOW()),
(@path4_id, '前端工程化与构建工具', 'Vite/Webpack 核心配置、Loader/Plugin 原理、代码分割、Tree Shaking、Module Federation', 5, 'ARTICLE', 90, NOW()),
(@path4_id, '组件库设计与开发', '设计系统 Token、组件 API 设计、Storybook 文档、单元测试、按需加载、发布 npm 包', 6, 'CODING', 120, NOW()),
(@path4_id, '前端性能优化实战', '性能指标 LCP/FID/CLS、懒加载策略、图片优化、CDN 加速、SSR/SSG、Service Worker 缓存', 7, 'ARTICLE', 90, NOW()),
(@path4_id, '微前端架构实践', 'qiankun/Module Federation 方案、主应用与子应用通信、样式隔离、沙箱机制、公共依赖管理', 8, 'VIDEO', 90, NOW()),
(@path4_id, '前端测试与质量保障', 'Jest/Vitest 单元测试、Testing Library 组件测试、Cypress E2E 测试、覆盖率门禁', 9, 'CODING', 60, NOW()),
(@path4_id, '项目实战：企业级管理后台', '基于 Vue 3 + TypeScript + Vite 构建完整的 RBAC 权限管理系统，集成国际化、主题切换、数据看板', 10, 'CODING', 240, NOW());

INSERT INTO content_tag (tag_id, content_type, content_id)
SELECT t.id, 'PATH', @path4_id FROM tag t WHERE t.name IN ('JavaScript', 'Vue.js', 'React', 'TypeScript');

-- =============================================
-- 路径5: 数据库核心与高性能架构
-- =============================================
INSERT INTO learning_path (title, description, difficulty, estimated_days, course_count, status, create_time, update_time)
VALUES ('数据库核心与高性能架构',
'MySQL + Redis + MongoDB 三库精通之路。深入 SQL 优化与索引设计、MySQL 主从复制与分库分表、Redis 缓存策略与分布式锁、MongoDB 文档建模与聚合管道。覆盖大厂面试高频考点与生产级高可用方案。适合全栈/后端开发者进阶。',
'INTERMEDIATE', 30, 8, 'PUBLISHED', NOW(), NOW());

SET @path5_id = LAST_INSERT_ID();

INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, create_time) VALUES
(@path5_id, 'MySQL 索引原理与优化', 'B+Tree 索引结构、聚簇索引/二级索引、最左前缀原则、索引下推、覆盖索引、索引失效场景分析', 1, 'ARTICLE', 90, NOW()),
(@path5_id, 'SQL 优化与慢查询治理', 'EXPLAIN 执行计划解读、慢查询日志分析、SQL 重写技巧、分页优化、JOIN 优化、子查询优化', 2, 'VIDEO', 90, NOW()),
(@path5_id, 'MySQL 事务与锁机制', '事务隔离级别实现原理、MVCC 机制、行锁/间隙锁/临键锁、死锁分析、乐观锁 vs 悲观锁', 3, 'ARTICLE', 90, NOW()),
(@path5_id, 'MySQL 高可用架构', '主从复制原理、Binlog/Relaylog 解析、半同步复制、MHA 高可用切换、MyCat/ShardingSphere 分库分表', 4, 'VIDEO', 120, NOW()),
(@path5_id, 'Redis 核心数据结构与场景', 'String/Hash/List/Set/ZSet 底层编码、Geo/BloomFilter/HyperLogLog 高级结构、缓存穿透/击穿/雪崩', 5, 'VIDEO', 90, NOW()),
(@path5_id, 'Redis 分布式锁与高可用', 'Redisson 分布式锁源码分析、RedLock 算法、哨兵模式、Cluster 集群、Redis 性能调优', 6, 'ARTICLE', 90, NOW()),
(@path5_id, 'MongoDB 从入门到实践', '文档模型设计、CRUD 操作、聚合管道、索引优化、副本集、分片集群、WiredTiger 存储引擎', 7, 'ARTICLE', 90, NOW()),
(@path5_id, '项目实战：多数据库缓存架构', '设计高并发秒杀系统：MySQL 主库 + Redis 缓存 + MongoDB 日志，ShardingSphere 分表分库实践', 8, 'CODING', 180, NOW());

INSERT INTO content_tag (tag_id, content_type, content_id)
SELECT t.id, 'PATH', @path5_id FROM tag t WHERE t.name IN ('MySQL', 'Redis', 'MongoDB', '数据库');

-- =============================================
-- 路径6: 数据结构与算法面试通关
-- =============================================
INSERT INTO learning_path (title, description, difficulty, estimated_days, course_count, status, create_time, update_time)
VALUES ('数据结构与算法面试通关',
'系统梳理大厂面试常考的数据结构与算法题型。覆盖数组、链表、栈、队列、树、图、堆、哈希表等核心数据结构，以及排序、搜索、动态规划、贪心、回溯等算法范式。配合 100+ LeetCode 高频题精讲，助你冲刺大厂 offer。',
'ADVANCED', 45, 10, 'PUBLISHED', NOW(), NOW());

SET @path6_id = LAST_INSERT_ID();

INSERT INTO learning_path_course (path_id, title, description, order_index, content_type, estimated_minutes, create_time) VALUES
(@path6_id, '数组、链表与字符串', '数组遍历技巧、双指针、滑动窗口、链表反转/合并/环检测、KMP 字符串匹配', 1, 'ARTICLE', 90, NOW()),
(@path6_id, '栈、队列与哈希表', '单调栈应用、队列实现栈、LRU/LFU 缓存设计、哈希冲突解决、一致性哈希', 2, 'VIDEO', 90, NOW()),
(@path6_id, '二叉树与树形结构', '二叉树遍历（递归/迭代）、BST 验证/构建、LCA 最近公共祖先、Trie 前缀树、线段树', 3, 'ARTICLE', 120, NOW()),
(@path6_id, '堆与优先队列', '堆的构建与调整、Top K 问题、数据流中位数、合并 K 个有序链表、定时器算法', 4, 'ARTICLE', 60, NOW()),
(@path6_id, '排序算法大全', '快排/归并/堆排原理与实现、排序稳定性分析、TOPK 问题、桶排序/计数排序/基数排序', 5, 'VIDEO', 90, NOW()),
(@path6_id, '图论与搜索', 'BFS/DFS 遍历、拓扑排序、最短路径 Dijkstra/Floyd、最小生成树 Kruskal/Prim、并查集', 6, 'VIDEO', 120, NOW()),
(@path6_id, '动态规划进阶', 'DP 方法论、背包问题、最长子序列、区间 DP、数位 DP、状态压缩 DP、树形 DP', 7, 'VIDEO', 150, NOW()),
(@path6_id, '贪心算法与回溯', '区间调度、跳跃游戏、括号生成、N 皇后、子集/组合/排列问题、剪枝优化', 8, 'CODING', 90, NOW()),
(@path6_id, '设计模式与系统设计', '单例/工厂/观察者/策略模式、设计微信朋友圈、短 URL 服务、TopK 系统、限流器设计', 9, 'ARTICLE', 120, NOW()),
(@path6_id, '高频面试题精讲', '精选 LeetCode Hot 100 + 剑指 Offer 核心题，一题多解、复杂度分析、面试技巧', 10, 'CODING', 180, NOW());

INSERT INTO content_tag (tag_id, content_type, content_id)
SELECT t.id, 'PATH', @path6_id FROM tag t WHERE t.name IN ('数据结构', '算法', '设计模式', '多线程');

-- 验证数据
SELECT 'Seed data inserted successfully!' AS result;
SELECT id, title, difficulty, estimated_days, course_count FROM learning_path WHERE status='PUBLISHED';
