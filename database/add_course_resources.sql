-- =============================================
-- 为所有学习路径课程添加参考资料链接
-- =============================================

-- ===== Path 1: Java 后端开发从零到精通 =====

-- 课程1: Java 基础语法与面向对象
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(1, 'Java 基础语法官方教程', 'https://docs.oracle.com/javase/tutorial/java/nutsandbolts/index.html', 'LINK', NOW()),
(1, 'Java 面向对象编程入门', 'https://docs.oracle.com/javase/tutorial/java/concepts/', 'LINK', NOW()),
(1, '廖雪峰 Java 教程', 'https://www.liaoxuefeng.com/wiki/1252599548343744', 'LINK', NOW()),
(1, 'Java 8 实战（书籍）', 'https://www.oreilly.com/library/view/java-8-in/9781617291999/', 'LINK', NOW()),
(1, 'LeetCode Java 题解', 'https://leetcode.cn/problemset/all/?languageTags=java', 'LINK', NOW());

-- 课程2: Java 集合框架深入
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(2, 'Java 集合框架官方文档', 'https://docs.oracle.com/javase/8/docs/technotes/guides/collections/overview.html', 'LINK', NOW()),
(2, 'HashMap 源码深度解析', 'https://www.cnblogs.com/skywang12345/p/3310835.html', 'LINK', NOW()),
(2, 'ConcurrentHashMap 并发安全机制', 'https://www.baeldung.com/java-concurrent-hashmap', 'LINK', NOW()),
(2, 'Java 集合面试题整理', 'https://github.com/CyC2018/CS-Notes/blob/master/notes/Java%20%E5%AE%B9%E5%99%A8.md', 'LINK', NOW());

-- 课程3: Java 多线程与并发编程
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(3, 'Java 并发官方教程', 'https://docs.oracle.com/javase/tutorial/essential/concurrency/', 'LINK', NOW()),
(3, 'Java 线程池最佳实践', 'https://www.baeldung.com/thread-pool-java-and-guava', 'LINK', NOW()),
(3, 'CompletableFuture 实战', 'https://www.baeldung.com/java-completablefuture', 'LINK', NOW()),
(3, 'Java 并发编程实战 PDF', 'https://book.douban.com/subject/10484692/', 'LINK', NOW());

-- 课程4: IO 与网络编程
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(4, 'Java IO 教程', 'https://www.baeldung.com/java-io', 'LINK', NOW()),
(4, 'Java NIO 详解', 'https://www.baeldung.com/java-nio-2-file-api', 'LINK', NOW()),
(4, 'Netty 实战入门', 'https://netty.io/wiki/user-guide.html', 'LINK', NOW()),
(4, 'BIO/NIO/AIO 对比分析', 'https://tech.meituan.com/2016/11/04/nio.html', 'LINK', NOW());

-- 课程5: 常用设计模式
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(5, 'Java 设计模式示例', 'https://github.com/iluwatar/java-design-patterns', 'LINK', NOW()),
(5, '设计模式六大原则', 'https://www.baeldung.com/solid-principles', 'LINK', NOW()),
(5, 'Head First 设计模式', 'https://www.oreilly.com/library/view/head-first-design/0596007124/', 'LINK', NOW()),
(5, 'Spring 中的设计模式', 'https://www.baeldung.com/spring-framework-design-patterns', 'LINK', NOW());

-- 课程6: JVM 内存模型与性能调优
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(6, 'JVM 官方规范（Java 8）', 'https://docs.oracle.com/javase/specs/jvms/se8/html/', 'LINK', NOW()),
(6, '深入理解 JVM（书籍）', 'https://book.douban.com/subject/24722612/', 'LINK', NOW()),
(6, 'JVM 调优参数详解', 'https://www.baeldung.com/jvm-parameters', 'LINK', NOW()),
(6, 'GC 算法与调优实战', 'https://tech.meituan.com/2017/12/29/jvm-optimize.html', 'LINK', NOW());

-- 课程7: MySQL 数据库设计与优化
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(7, 'MySQL 官方文档', 'https://dev.mysql.com/doc/refman/5.7/en/', 'LINK', NOW()),
(7, 'MySQL 索引原理与优化', 'https://www.cnblogs.com/sheng-dimu/p/12019195.html', 'LINK', NOW()),
(7, 'SQL 优化实战案例', 'https://tech.meituan.com/2014/06/30/mysql-index.html', 'LINK', NOW()),
(7, 'MySQL 性能调优指南', 'https://www.baeldung.com/java-mysql-performance', 'LINK', NOW());

-- 课程8: JDBC 与数据库连接池
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(8, 'JDBC 官方教程', 'https://docs.oracle.com/javase/tutorial/jdbc/basics/', 'LINK', NOW()),
(8, 'HikariCP 连接池配置', 'https://github.com/brettwooldridge/HikariCP#frequently-used', 'LINK', NOW()),
(8, 'Druid 连接池监控', 'https://github.com/alibaba/druid/wiki/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98', 'LINK', NOW()),
(8, 'MyBatis Spring 集成', 'https://mybatis.org/spring/zh/index.html', 'LINK', NOW());

-- 课程9: MyBatis 持久层框架实战
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(9, 'MyBatis 官方文档（中文）', 'https://mybatis.org/mybatis-3/zh/index.html', 'LINK', NOW()),
(9, 'MyBatis Generator 代码生成', 'https://mybatis.org/generator/', 'LINK', NOW()),
(9, 'MyBatis-Plus 官方文档', 'https://baomidou.com/pages/24112f/', 'LINK', NOW()),
(9, 'MyBatis 动态 SQL 实战', 'https://mybatis.org/mybatis-3/dynamic-sql.html', 'LINK', NOW());

-- 课程10: Spring Boot 入门与自动配置
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(10, 'Spring Boot 官方文档', 'https://docs.spring.io/spring-boot/docs/2.7.x/reference/htmlsingle/', 'LINK', NOW()),
(10, 'Spring Boot 自动配置原理', 'https://www.baeldung.com/spring-boot-auto-configuration', 'LINK', NOW()),
(10, 'Spring Boot Starter 开发', 'https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.developing-auto-configuration', 'LINK', NOW()),
(10, 'Spring Initializr 项目生成', 'https://start.spring.io/', 'LINK', NOW());

-- 课程11: RESTful API 设计与开发
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(11, 'RESTful API 设计规范', 'https://docs.microsoft.com/zh-cn/azure/architecture/best-practices/api-design', 'LINK', NOW()),
(11, 'Swagger/OpenAPI 文档', 'https://swagger.io/docs/specification/about/', 'LINK', NOW()),
(11, 'Spring REST Docs', 'https://spring.io/projects/spring-restdocs', 'LINK', NOW()),
(11, 'Postman 接口测试', 'https://learning.postman.com/docs/getting-started/introduction/', 'LINK', NOW());

-- 课程12: 项目实战：电商后端
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(12, '电商系统架构设计', 'https://github.com/macrozheng/mall', 'LINK', NOW()),
(12, 'Spring Boot 电商实战', 'https://github.com/ityouknow/spring-boot-examples', 'LINK', NOW()),
(12, 'RuoYi 后台管理系统', 'https://gitee.com/y_project/RuoYi', 'LINK', NOW()),
(12, 'Spring Cloud 微服务电商', 'https://github.com/piggymetrics/piggymetrics', 'LINK', NOW());


-- ===== Path 2: Spring Boot 微服务架构实战 =====

-- 课程13: 微服务架构设计原则
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(13, '微服务架构设计指南', 'https://microservices.io/patterns/index.html', 'LINK', NOW()),
(13, 'Martin Fowler 微服务', 'https://martinfowler.com/articles/microservices.html', 'LINK', NOW()),
(13, 'Spring Cloud 官方文档', 'https://spring.io/projects/spring-cloud#overview', 'LINK', NOW()),
(13, '领域驱动设计（DDD）', 'https://www.baeldung.com/domain-driven-design-intro', 'LINK', NOW());

-- 课程14: Spring Cloud Nacos 服务注册与配置
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(14, 'Nacos 官方文档', 'https://nacos.io/zh-cn/docs/what-is-nacos.html', 'LINK', NOW()),
(14, 'Nacos Spring Cloud 集成', 'https://spring-cloud-alibaba-group.github.io/github-pages/hoxton/en-us/index.html', 'LINK', NOW()),
(14, 'Nacos 配置中心实战', 'https://blog.csdn.net/forezp/article/details/100073159', 'LINK', NOW()),
(14, 'Nacos 集群部署', 'https://nacos.io/zh-cn/docs/cluster-mode-quick-start.html', 'LINK', NOW());

-- 课程15: Spring Cloud Gateway 网关
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(15, 'Spring Cloud Gateway 官方', 'https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/', 'LINK', NOW()),
(15, 'Gateway 路由配置详解', 'https://www.baeldung.com/spring-cloud-gateway', 'LINK', NOW()),
(15, 'Gateway 限流与熔断', 'https://cloud.spring.io/spring-cloud-gateway/reference/html/#gateway-request-predicates-factories', 'LINK', NOW()),
(15, 'Gateway + Nacos 动态路由', 'https://blog.csdn.net/forezp/article/details/101205048', 'LINK', NOW());

-- 课程16: OpenFeign 声明式服务调用
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(16, 'Spring Cloud OpenFeign 官方', 'https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/', 'LINK', NOW()),
(16, 'Feign 负载均衡配置', 'https://www.baeldung.com/spring-cloud-load-balancer', 'LINK', NOW()),
(16, 'Feign 熔断降级集成', 'https://www.baeldung.com/spring-cloud-feign-hystrix', 'LINK', NOW()),
(16, 'OpenFeign 最佳实践', 'https://developer.aliyun.com/article/708617', 'LINK', NOW());

-- 课程17: Sentinel 流量控制与熔断
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(17, 'Sentinel 官方文档', 'https://sentinelguard.io/zh-cn/docs/introduction.html', 'LINK', NOW()),
(17, 'Sentinel 控制台部署', 'https://sentinelguard.io/zh-cn/docs/dashboard.html', 'LINK', NOW()),
(17, 'Sentinel 规则配置详解', 'https://github.com/alibaba/Sentinel/wiki/%E6%B5%81%E9%87%8F%E6%8E%A7%E5%88%B6', 'LINK', NOW()),
(17, 'Sentinel + Spring Cloud 集成', 'https://spring-cloud-alibaba-group.github.io/github-pages/hoxton/en-us/index.html#_sentinel', 'LINK', NOW());

-- 课程18: 分布式事务 Seata
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(18, 'Seata 官方文档', 'https://seata.io/zh-cn/docs/overview/what-is-seata.html', 'LINK', NOW()),
(18, 'Seata AT 模式详解', 'https://seata.io/zh-cn/docs/dev/mode/at-mode.html', 'LINK', NOW()),
(18, 'Seata TCC 模式实战', 'https://seata.io/zh-cn/docs/dev/mode/tcc-mode.html', 'LINK', NOW()),
(18, '分布式事务解决方案对比', 'https://www.cnblogs.com/jajian/p/11191305.html', 'LINK', NOW()),
(18, 'Seata + Spring Cloud 集成', 'https://github.com/seata/seata-samples', 'LINK', NOW());

-- 课程19: Spring Cloud Sleuth 链路追踪
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(19, 'Spring Cloud Sleuth 官方', 'https://docs.spring.io/spring-cloud-sleuth/docs/current/reference/html/', 'LINK', NOW()),
(19, 'Zipkin 部署与集成', 'https://zipkin.io/pages/quickstart.html', 'LINK', NOW()),
(19, 'SkyWalking APM 系统', 'https://skywalking.apache.org/docs/main/latest/readme/', 'LINK', NOW()),
(19, 'Micrometer 指标监控', 'https://micrometer.io/docs', 'LINK', NOW());

-- 课程20: Docker 容器化部署
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(20, 'Docker 官方文档', 'https://docs.docker.com/', 'LINK', NOW()),
(20, 'Docker Compose 编排', 'https://docs.docker.com/compose/', 'LINK', NOW()),
(20, 'Dockerfile 最佳实践', 'https://docs.docker.com/develop/develop-images/dockerfile_best-practices/', 'LINK', NOW()),
(20, 'Docker 从入门到实践', 'https://yeasy.gitbook.io/docker_practice/', 'LINK', NOW());

-- 课程21: Kubernetes 容器编排
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(21, 'Kubernetes 官方文档', 'https://kubernetes.io/docs/home/', 'LINK', NOW()),
(21, 'Kubernetes 中文指南', 'https://jimmysong.io/kubernetes-handbook/', 'LINK', NOW()),
(21, 'K8s 核心概念 Pod/Service', 'https://kubernetes.io/docs/concepts/', 'LINK', NOW()),
(21, 'Helm 包管理工具', 'https://helm.sh/docs/', 'LINK', NOW()),
(21, 'Kubernetes 实践案例', 'https://github.com/rootsongjc/kubernetes-hardening-guide', 'LINK', NOW());

-- 课程22: 微服务项目实战
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(22, 'Spring Cloud 微服务电商', 'https://github.com/macrozheng/mall-swarm', 'LINK', NOW()),
(22, 'Pig 微服务开发平台', 'https://pig4cloud.com/', 'LINK', NOW()),
(22, 'Spring Cloud Alibaba 示例', 'https://github.com/alibaba/spring-cloud-alibaba/tree/master/spring-cloud-alibaba-examples', 'LINK', NOW()),
(22, 'GitHub Actions CI/CD', 'https://docs.github.com/zh/actions', 'LINK', NOW());


-- ===== Path 3: Python 全栈开发 =====

-- 课程23: Python 基础语法
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(23, 'Python 官方教程（中文）', 'https://docs.python.org/zh-cn/3/tutorial/', 'LINK', NOW()),
(23, '廖雪峰 Python 教程', 'https://www.liaoxuefeng.com/wiki/1016959663602400', 'LINK', NOW()),
(23, 'Python 编程从入门到实践', 'https://www.ituring.com.cn/book/2784', 'LINK', NOW()),
(23, 'Python 标准库参考', 'https://docs.python.org/3/library/index.html', 'LINK', NOW());

-- 课程24: Python 面向对象与模块化
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(24, 'Python 面向对象编程', 'https://docs.python.org/3/tutorial/classes.html', 'LINK', NOW()),
(24, 'Python 模块与包管理', 'https://docs.python.org/3/tutorial/modules.html', 'LINK', NOW()),
(24, 'PEP 8 Python 编码规范', 'https://peps.python.org/pep-0008/', 'LINK', NOW()),
(24, 'Python 装饰器详解', 'https://www.runoob.com/w3cnote/python-func-decorators.html', 'LINK', NOW());

-- 课程25: Flask Web 快速开发
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(25, 'Flask 官方文档（中文）', 'https://dormousehole.readthedocs.io/en/latest/', 'LINK', NOW()),
(25, 'Flask 大型应用实战', 'https://flask.palletsprojects.com/en/2.3.x/tutorial/', 'LINK', NOW()),
(25, 'Flask SQLAlchemy 集成', 'https://flask-sqlalchemy.palletsprojects.com/', 'LINK', NOW()),
(25, 'Flask RESTful API', 'https://flask-restful.readthedocs.io/', 'LINK', NOW());

-- 课程26: Flask 项目实战
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(26, 'Flask Blog 实战教程', 'https://github.com/greyli/hello-flask', 'LINK', NOW()),
(26, 'Flask 用户认证系统', 'https://flask-login.readthedocs.io/', 'LINK', NOW()),
(26, 'Flask 文件上传与处理', 'https://flask.palletsprojects.com/en/2.3.x/patterns/fileuploads/', 'LINK', NOW()),
(26, 'Flask + Celery 异步任务', 'https://blog.miguelgrinberg.com/post/using-celery-with-flask', 'LINK', NOW());

-- 课程27: Django 入门
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(27, 'Django 官方文档（中文）', 'https://docs.djangoproject.com/zh-hans/4.2/', 'LINK', NOW()),
(27, 'Django 入门教程', 'https://developer.mozilla.org/zh-CN/docs/Learn/Server-side/Django', 'LINK', NOW()),
(27, 'Django ORM 详解', 'https://docs.djangoproject.com/en/4.2/topics/db/queries/', 'LINK', NOW()),
(27, 'Django Admin 后台定制', 'https://docs.djangoproject.com/en/4.2/ref/contrib/admin/', 'LINK', NOW());

-- 课程28: Django REST Framework
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(28, 'Django REST Framework 官方', 'https://www.django-rest-framework.org/', 'LINK', NOW()),
(28, 'DRF 序列化器详解', 'https://www.django-rest-framework.org/api-guide/serializers/', 'LINK', NOW()),
(28, 'DRF 认证与权限', 'https://www.django-rest-framework.org/api-guide/authentication/', 'LINK', NOW()),
(28, 'DRF + JWT 认证', 'https://django-rest-framework-simplejwt.readthedocs.io/', 'LINK', NOW());

-- 课程29: Python 数据分析
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(29, 'NumPy 官方快速入门', 'https://numpy.org/doc/stable/user/quickstart.html', 'LINK', NOW()),
(29, 'Pandas 数据处理教程', 'https://pandas.pydata.org/docs/getting_started/intro_tutorials/', 'LINK', NOW()),
(29, 'Matplotlib 数据可视化', 'https://matplotlib.org/stable/tutorials/index.html', 'LINK', NOW()),
(29, 'Python 数据分析实战', 'https://github.com/wesm/pydata-book', 'LINK', NOW());

-- 课程30: Python 后端 API 开发
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(30, 'FastAPI 官方文档', 'https://fastapi.tiangolo.com/', 'LINK', NOW()),
(30, 'FastAPI SQLAlchemy 集成', 'https://fastapi.tiangolo.com/tutorial/sql-databases/', 'LINK', NOW()),
(30, 'FastAPI 部署指南', 'https://fastapi.tiangolo.com/deployment/', 'LINK', NOW()),
(30, 'Pydantic 数据验证', 'https://docs.pydantic.dev/latest/', 'LINK', NOW());

-- 课程31: 爬虫与自动化
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(31, 'Requests 库官方文档', 'https://docs.python-requests.org/zh_CN/latest/', 'LINK', NOW()),
(31, 'BeautifulSoup 解析教程', 'https://www.crummy.com/software/BeautifulSoup/bs4/doc/zh/', 'LINK', NOW()),
(31, 'Scrapy 框架官方文档', 'https://scrapy.org/doc/', 'LINK', NOW()),
(31, 'Selenium 自动化测试', 'https://www.selenium.dev/documentation/zh-cn/', 'LINK', NOW());

-- 课程32: Python 全栈项目实战
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(32, 'Flask 大型项目结构', 'https://github.com/pallets/flask/tree/main/examples/tutorial', 'LINK', NOW()),
(32, 'Django Blog 项目实战', 'https://github.com/divio/django-cms', 'LINK', NOW()),
(32, 'FastAPI 全栈模板', 'https://github.com/tiangolo/full-stack-fastapi-postgresql', 'LINK', NOW()),
(32, 'Vue + Python 前后端分离', 'https://github.com/gtalarico/flask-vuejs-template', 'LINK', NOW());


-- ===== Path 4: 前端工程化 =====

-- 课程33: JavaScript 核心与 ES6+
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(33, 'MDN JavaScript 教程', 'https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Guide', 'LINK', NOW()),
(33, 'ES6 入门教程（阮一峰）', 'https://es6.ruanyifeng.com/', 'LINK', NOW()),
(33, 'JavaScript 高级程序设计', 'https://www.ituring.com.cn/book/2472', 'LINK', NOW()),
(33, 'JavaScript 算法与数据结构', 'https://github.com/trekhleb/javascript-algorithms', 'LINK', NOW());

-- 课程34: TypeScript 类型系统
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(34, 'TypeScript 官方文档（中文）', 'https://www.typescriptlang.org/zh/docs/', 'LINK', NOW()),
(34, 'TypeScript 入门教程', 'https://ts.xcatliu.com/', 'LINK', NOW()),
(34, 'TypeScript 类型体操', 'https://github.com/type-challenges/type-challenges', 'LINK', NOW()),
(34, 'TypeScript 设计模式', 'https://github.com/torokmark/design_patterns_in_typescript', 'LINK', NOW());

-- 课程35: Vue 3 Composition API
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(35, 'Vue 3 官方文档（中文）', 'https://cn.vuejs.org/guide/introduction.html', 'LINK', NOW()),
(35, 'Vue 3 Composition API 详解', 'https://vuejs.org/guide/extras/composition-api-faq.html', 'LINK', NOW()),
(35, 'Pinia 状态管理', 'https://pinia.vuejs.org/zh/', 'LINK', NOW()),
(35, 'VueUse 工具库', 'https://vueuse.org/', 'LINK', NOW());

-- 课程36: React 18 核心概念
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(36, 'React 官方文档', 'https://react.dev/', 'LINK', NOW()),
(36, 'React Hooks 详解', 'https://react.dev/reference/react/hooks', 'LINK', NOW()),
(36, 'Next.js 全栈框架', 'https://nextjs.org/docs', 'LINK', NOW()),
(36, 'React 状态管理对比', 'https://www.robinwieruch.de/react-state-management/', 'LINK', NOW());

-- 课程37: 组件库设计与开发
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(37, 'Element Plus 组件库', 'https://element-plus.org/zh-CN/', 'LINK', NOW()),
(37, 'Ant Design Vue', 'https://next.antdv.com/docs/vue/getting-started-cn', 'LINK', NOW()),
(37, 'Storybook 组件开发', 'https://storybook.js.org/docs/vue/get-started', 'LINK', NOW()),
(37, 'Vue 组件设计原则', 'https://cn.vuejs.org/guide/components/registration.html', 'LINK', NOW());

-- 课程38: Webpack 与 Vite 构建
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(38, 'Webpack 5 官方文档', 'https://webpack.js.org/concepts/', 'LINK', NOW()),
(38, 'Vite 官方文档', 'https://cn.vitejs.dev/guide/', 'LINK', NOW()),
(38, 'Webpack 优化实战', 'https://webpack.js.org/guides/production/', 'LINK', NOW()),
(38, 'Rollup 模块打包器', 'https://rollupjs.org/guide/en/', 'LINK', NOW());

-- 课程39: 前端性能优化
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(39, 'Google Web 性能优化', 'https://web.dev/learn/performance/', 'LINK', NOW()),
(39, 'Lighthouse 性能审计', 'https://developer.chrome.com/docs/lighthouse/overview/', 'LINK', NOW()),
(39, '前端性能优化指南', 'https://github.com/woai3c/Front-end-articles/blob/master/performance.md', 'LINK', NOW()),
(39, 'Chrome DevTools 调试', 'https://developer.chrome.com/docs/devtools/', 'LINK', NOW());

-- 课程40: 微前端架构
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(40, 'qiankun 微前端框架', 'https://qiankun.umijs.org/zh/guide/', 'LINK', NOW()),
(40, 'Module Federation 联邦模块', 'https://webpack.js.org/concepts/module-federation/', 'LINK', NOW()),
(40, '微前端设计原理', 'https://martinfowler.com/articles/micro-frontends.html', 'LINK', NOW()),
(40, 'Single-SPA 微前端', 'https://single-spa.js.org/docs/getting-started-overview/', 'LINK', NOW());

-- 课程41: 前端测试与质量
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(41, 'Jest 测试框架', 'https://jestjs.io/zh-Hans/docs/getting-started', 'LINK', NOW()),
(41, 'Vue Test Utils', 'https://test-utils.vuejs.org/guide/', 'LINK', NOW()),
(41, 'Cypress E2E 测试', 'https://docs.cypress.io/guides/overview/why-cypress', 'LINK', NOW()),
(41, 'ESLint 代码规范', 'https://eslint.org/docs/latest/use/getting-started', 'LINK', NOW());

-- 课程42: 前端工程化项目
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(42, 'Vue 3 + Vite 项目模板', 'https://github.com/vuejs/create-vue', 'LINK', NOW()),
(42, '前端工程化实践', 'https://github.com/woai3c/Front-end-articles/blob/master/project.md', 'LINK', NOW()),
(42, 'GitHub Actions 前端 CI/CD', 'https://docs.github.com/zh/actions/automating-builds-and-tests/building-and-testing-nodejs', 'LINK', NOW()),
(42, '前端项目架构设计', 'https://github.com/luoxue-victor/frontend-architecture', 'LINK', NOW());


-- ===== Path 5: 数据库核心 =====

-- 课程43: MySQL 架构与存储引擎
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(43, 'MySQL 官方架构文档', 'https://dev.mysql.com/doc/refman/5.7/en/server-architecture.html', 'LINK', NOW()),
(43, 'InnoDB 存储引擎详解', 'https://dev.mysql.com/doc/refman/5.7/en/innodb-introduction.html', 'LINK', NOW()),
(43, 'MySQL Buffer Pool 机制', 'https://dev.mysql.com/doc/refman/5.7/en/innodb-buffer-pool.html', 'LINK', NOW()),
(43, 'Redo Log 与 Binlog 区别', 'https://www.cnblogs.com/rickiyang/p/11075053.html', 'LINK', NOW());

-- 课程44: SQL 优化与执行计划
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(44, 'MySQL EXPLAIN 详解', 'https://dev.mysql.com/doc/refman/5.7/en/explain-output.html', 'LINK', NOW()),
(44, 'SQL 优化最佳实践', 'https://www.baeldung.com/sql-optimization-techniques', 'LINK', NOW()),
(44, '索引失效场景大全', 'https://www.cnblogs.com/developer_chan/p/12760162.html', 'LINK', NOW()),
(44, '慢查询日志分析', 'https://dev.mysql.com/doc/refman/5.7/en/slow-query-log.html', 'LINK', NOW());

-- 课程45: MySQL 主从与高可用
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(45, 'MySQL 主从复制配置', 'https://dev.mysql.com/doc/refman/5.7/en/replication-configuration.html', 'LINK', NOW()),
(45, 'MySQL 读写分离方案', 'https://dev.mysql.com/doc/refman/5.7/en/replication-solutions.html', 'LINK', NOW()),
(45, 'MHA 高可用架构', 'https://github.com/yoshinorim/mha4mysql-manager', 'LINK', NOW()),
(45, 'MySQL 备份与恢复', 'https://dev.mysql.com/doc/refman/5.7/en/backup-and-recovery.html', 'LINK', NOW());

-- 课程46: MySQL 分库分表
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(46, 'ShardingSphere 官方文档', 'https://shardingsphere.apache.org/document/current/cn/overview/', 'LINK', NOW()),
(46, 'MyCat 中间件', 'http://www.mycat.org.cn/document/MyCat_V1.6.1.pdf', 'LINK', NOW()),
(46, '分库分表设计原则', 'https://tech.meituan.com/2016/11/18/dianping-order-db-sharding.html', 'LINK', NOW()),
(46, '分布式主键生成策略', 'https://www.baeldung.com/snowflake-id-algorithm', 'LINK', NOW());

-- 课程47: Redis 核心数据结构
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(47, 'Redis 官方文档', 'https://redis.io/docs/data-types/', 'LINK', NOW()),
(47, 'Redis 数据结构与编码', 'https://redis.io/docs/data-types/strings/', 'LINK', NOW()),
(47, 'Redis 内存优化', 'https://redis.io/docs/management/optimization/memory-optimization/', 'LINK', NOW()),
(47, 'Redis 缓存穿透与雪崩', 'https://www.baeldung.com/redis-cache-penetration', 'LINK', NOW());

-- 课程48: Redis 高可用集群
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(48, 'Redis Sentinel 哨兵模式', 'https://redis.io/docs/management/sentinel/', 'LINK', NOW()),
(48, 'Redis Cluster 集群', 'https://redis.io/docs/management/scaling/', 'LINK', NOW()),
(48, 'Redis 持久化机制', 'https://redis.io/docs/management/persistence/', 'LINK', NOW()),
(48, 'Redis 运维最佳实践', 'https://www.cnblogs.com/kismetv/p/8654978.html', 'LINK', NOW());

-- 课程49: MongoDB 文档数据库
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(49, 'MongoDB 官方文档', 'https://www.mongodb.com/docs/manual/introduction/', 'LINK', NOW()),
(49, 'MongoDB 聚合管道', 'https://www.mongodb.com/docs/manual/aggregation/', 'LINK', NOW()),
(49, 'MongoDB 索引策略', 'https://www.mongodb.com/docs/manual/indexes/', 'LINK', NOW()),
(49, 'MongoDB 复制集与分片', 'https://www.mongodb.com/docs/manual/replication/', 'LINK', NOW());

-- 课程50: 数据库备份与恢复
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(50, 'mysqldump 备份详解', 'https://dev.mysql.com/doc/refman/5.7/en/mysqldump.html', 'LINK', NOW()),
(50, 'MySQL 增量备份', 'https://dev.mysql.com/doc/refman/5.7/en/point-in-time-recovery.html', 'LINK', NOW()),
(50, 'XtraBackup 物理备份', 'https://docs.percona.com/percona-xtrabackup/8.0/', 'LINK', NOW()),
(50, '数据库灾备方案', 'https://cloud.tencent.com/document/product/236/7609', 'LINK', NOW());


-- ===== Path 6: 数据结构与算法 =====

-- 课程51: 数组与链表
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(51, 'LeetCode 数组专题', 'https://leetcode.cn/tag/array/', 'LINK', NOW()),
(51, 'LeetCode 链表专题', 'https://leetcode.cn/tag/linked-list/', 'LINK', NOW()),
(51, '双指针技巧', 'https://leetcode.cn/problems/two-sum-ii-input-array-is-sorted/solution/liang-shu-zhi-he-ii-by-leetcode-solution/', 'LINK', NOW()),
(51, 'Java 链表实现源码', 'https://docs.oracle.com/javase/8/docs/api/java/util/LinkedList.html', 'LINK', NOW());

-- 课程52: 栈与队列
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(52, 'LeetCode 栈专题', 'https://leetcode.cn/tag/stack/', 'LINK', NOW()),
(52, 'LeetCode 队列专题', 'https://leetcode.cn/tag/queue/', 'LINK', NOW()),
(52, '单调栈算法详解', 'https://leetcode.cn/problems/largest-rectangle-in-histogram/solutions/108083/84-by-81796054/', 'LINK', NOW()),
(52, '优先队列（堆）原理', 'https://docs.oracle.com/javase/8/docs/api/java/util/PriorityQueue.html', 'LINK', NOW());

-- 课程53: 二叉树与递归
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(53, 'LeetCode 树专题', 'https://leetcode.cn/tag/tree/', 'LINK', NOW()),
(53, '二叉树遍历详解', 'https://leetcode.cn/problems/binary-tree-preorder-traversal/solution/er-cha-shu-de-qian-xu-bian-li-by-leetcode-solution/', 'LINK', NOW()),
(53, '递归思想与回溯', 'https://leetcode.cn/problems/permutations/solution/hui-su-suan-fa-by-labuladong-2/', 'LINK', NOW()),
(53, 'LCA 最近公共祖先', 'https://leetcode.cn/problems/lowest-common-ancestor-of-a-binary-tree/solution/', 'LINK', NOW());

-- 课程54: 图论算法
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(54, 'LeetCode 图专题', 'https://leetcode.cn/tag/graph/', 'LINK', NOW()),
(54, '图论基础概念', 'https://www.baeldung.com/cs/graph-theory-intro', 'LINK', NOW()),
(54, '拓扑排序详解', 'https://leetcode.cn/problems/course-schedule/solution/ke-cheng-biao-by-leetcode-solution/', 'LINK', NOW()),
(54, '最短路径算法对比', 'https://www.baeldung.com/cs/dijkstra-vs-bellman-ford', 'LINK', NOW());

-- 课程55: 哈希表
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(55, 'LeetCode 哈希表专题', 'https://leetcode.cn/tag/hash-table/', 'LINK', NOW()),
(55, '布隆过滤器原理', 'https://www.baeldung.com/cs/bloom-filter', 'LINK', NOW()),
(55, '一致性哈希算法', 'https://www.baeldung.com/cs/consistent-hashing', 'LINK', NOW()),
(55, 'Java HashMap 源码分析', 'https://www.baeldung.com/java-hashmap', 'LINK', NOW());

-- 课程56: 排序算法
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(56, '排序算法可视化', 'https://www.sorting-algorithms.com/', 'LINK', NOW()),
(56, 'LeetCode 排序专题', 'https://leetcode.cn/tag/sorting/', 'LINK', NOW()),
(56, '快速排序原理与实现', 'https://www.baeldung.com/java-quicksort', 'LINK', NOW()),
(56, '归并排序应用', 'https://leetcode.cn/problems/sort-an-array/solution/pai-xu-shu-zi-by-leetcode-solution/', 'LINK', NOW());

-- 课程57: 二分查找
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(57, 'LeetCode 二分查找专题', 'https://leetcode.cn/tag/binary-search/', 'LINK', NOW()),
(57, '二分查找模版', 'https://leetcode.cn/problems/binary-search/solution/er-fen-cha-zhao-by-leetcode/', 'LINK', NOW()),
(57, '旋转数组二分', 'https://leetcode.cn/problems/search-in-rotated-sorted-array/solution/', 'LINK', NOW()),
(57, '二分答案技巧', 'https://leetcode.cn/problems/find-first-and-last-position-of-element-in-sorted-array/solution/', 'LINK', NOW());

-- 课程58: 动态规划
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(58, 'LeetCode 动态规划专题', 'https://leetcode.cn/tag/dynamic-programming/', 'LINK', NOW()),
(58, '动态规划解题框架', 'https://leetcode.cn/problems/fibonacci-number/solution/fei-bo-na-qi-shu-by-leetcode-solution/', 'LINK', NOW()),
(58, '背包九讲', 'https://github.com/tianyicui/pack', 'LINK', NOW()),
(58, '状态压缩 DP 入门', 'https://www.baeldung.com/cs/state-space-search-dynamic-programming', 'LINK', NOW());

-- 课程59: 字符串匹配
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(59, 'LeetCode 字符串专题', 'https://leetcode.cn/tag/string/', 'LINK', NOW()),
(59, 'KMP 算法详解', 'https://www.baeldung.com/java-knuth-morris-pratt', 'LINK', NOW()),
(59, 'Trie 树实现', 'https://leetcode.cn/problems/implement-trie-prefix-tree/solution/', 'LINK', NOW()),
(59, 'AC 自动机', 'https://www.baeldung.com/cs/aho-corasick', 'LINK', NOW());

-- 课程60: 大厂面试真题
INSERT INTO course_resource (course_id, title, url, type, create_time) VALUES
(60, 'LeetCode 热题 100', 'https://leetcode.cn/problem-list/2cktkvj/', 'LINK', NOW()),
(60, '剑指 Offer 专题', 'https://leetcode.cn/problem-list/xb9nqhhg/', 'LINK', NOW()),
(60, '大厂面试高频题', 'https://github.com/afatcoder/LeetcodeTop', 'LINK', NOW()),
(60, '算法面试通关 40 讲', 'https://leetcode.cn/leetbook/detail/algorithm-interview-crash-course/', 'LINK', NOW());
