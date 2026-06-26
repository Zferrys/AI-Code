/**
 * 为每门课程插入参考资源链接 (course_resource 表)
 * 用法: node seed_resources.mjs
 */
const mysql = require('mysql2/promise');

(async () => {
  const conn = await mysql.createConnection({
    host: '127.0.0.1', port: 3306,
    user: 'root', password: 'zph',
    database: 'aicode_db'
  });

  // 清空旧数据
  await conn.execute('DELETE FROM course_resource');

  const resources = [
    // ========== 路径1: Java 后端开发 ==========
    { id: 1,  title: 'Java 官方文档 (JDK 8)', type: 'REFERENCE', url: 'https://docs.oracle.com/javase/8/docs/api/', desc: 'JDK 8 API 官方文档' },
    { id: 1,  title: 'Java 基础教程 - 菜鸟教程', type: 'LINK', url: 'https://www.runoob.com/java/java-tutorial.html', desc: 'Java 入门到精通' },
    { id: 1,  title: 'On Java 8 中文版', type: 'REFERENCE', url: 'https://github.com/LingCoder/OnJava8', desc: 'GitHub 开源中译版' },
    { id: 2,  title: '面向对象设计原则 SOLID', type: 'LINK', url: 'https://www.cnblogs.com/hellojava/p/15694211.html', desc: '详解 SOLID 原则' },
    { id: 3,  title: 'Java 集合框架源码分析', type: 'LINK', url: 'https://www.cnblogs.com/chengxiao/p/6842045.html', desc: 'HashMap/ArrayList 源码分析' },
    { id: 4,  title: 'Java IO 流完整指南', type: 'LINK', url: 'https://www.liaoxuefeng.com/wiki/1252599548343744/1298069154955297', desc: '廖雪峰 IO 流教程' },
    { id: 5,  title: 'Java 多线程 - 廖雪峰', type: 'LINK', url: 'https://www.liaoxuefeng.com/wiki/1252599548343744/1306580838119458', desc: '廖雪峰多线程教程' },
    { id: 6,  title: 'JVM 内存模型深入理解', type: 'LINK', url: 'https://www.cnblogs.com/dolphin0520/p/3920820.html', desc: 'Java 内存区域与内存模型' },
    { id: 7,  title: 'MySQL 官方文档 (5.7)', type: 'REFERENCE', url: 'https://dev.mysql.com/doc/refman/5.7/en/', desc: 'MySQL 5.7 官方文档' },
    { id: 7,  title: 'SQL 优化与索引设计', type: 'LINK', url: 'https://www.cnblogs.com/liqiangchn/p/9068311.html', desc: 'MySQL 索引原理解析' },
    { id: 9,  title: 'MyBatis 中文文档', type: 'REFERENCE', url: 'https://mybatis.org/mybatis-3/zh/index.html', desc: 'MyBatis 3 中文文档' },
    { id: 10, title: 'Spring Boot 2.x 官方文档', type: 'REFERENCE', url: 'https://docs.spring.io/spring-boot/docs/2.7.x/reference/html/', desc: 'Spring Boot 官方参考' },
    { id: 11, title: 'RESTful API 设计规范', type: 'LINK', url: 'https://zhuanlan.zhihu.com/p/39420714', desc: 'RESTful API 设计最佳实践' },
    { id: 12, title: 'Spring Boot 项目实战', type: 'LINK', url: 'https://github.com/ityouknow/spring-boot-examples', desc: 'GitHub Spring Boot 实战' },

    // ========== 路径2: 微服务 ==========
    { id: 13, title: '微服务设计模式', type: 'LINK', url: 'https://microservices.io/patterns/index.html', desc: '微服务设计模式全集' },
    { id: 14, title: 'Nacos 官方文档', type: 'REFERENCE', url: 'https://nacos.io/zh-cn/docs/what-is-nacos.html', desc: 'Nacos 注册与配置中心' },
    { id: 15, title: 'Spring Cloud Gateway 官方', type: 'REFERENCE', url: 'https://cloud.spring.io/spring-cloud-gateway/reference/html/', desc: 'Spring Cloud Gateway 参考' },
    { id: 17, title: 'Sentinel 官方文档', type: 'REFERENCE', url: 'https://sentinelguard.io/zh-cn/docs/introduction.html', desc: '阿里流量控制组件' },
    { id: 18, title: 'Seata 分布式事务', type: 'LINK', url: 'https://seata.io/zh-cn/docs/overview/what-is-seata.html', desc: 'Seata 分布式事务解决方案' },
    { id: 20, title: 'Docker 从入门到实践', type: 'LINK', url: 'https://yeasy.gitbook.io/docker_practice/', desc: 'Docker 中文教程' },
    { id: 21, title: 'Kubernetes 官方教程', type: 'LINK', url: 'https://kubernetes.io/zh/docs/tutorials/', desc: 'K8s 官方入门教程' },

    // ========== 路径3: Python ==========
    { id: 23, title: 'Python 官方中文教程', type: 'REFERENCE', url: 'https://docs.python.org/zh-cn/3/tutorial/', desc: 'Python 3 官方教程' },
    { id: 24, title: 'Python OOP - 廖雪峰', type: 'LINK', url: 'https://www.liaoxuefeng.com/wiki/1016959663602400/1017496679217440', desc: '廖雪峰 Python 面向对象' },
    { id: 25, title: 'Flask 官方文档', type: 'LINK', url: 'https://flask.palletsprojects.com/zh-cn/2.3.x/', desc: 'Flask 框架文档' },
    { id: 27, title: 'Django 中文文档', type: 'REFERENCE', url: 'https://docs.djangoproject.com/zh-hans/4.2/', desc: 'Django 中文文档' },
    { id: 27, title: 'Django REST Framework', type: 'REFERENCE', url: 'https://www.django-rest-framework.org/', desc: 'DRF 官方文档' },
    { id: 29, title: 'Pandas 官方用户指南', type: 'LINK', url: 'https://pandas.pydata.org/docs/user_guide/index.html', desc: 'Pandas 官方用户指南' },
    { id: 30, title: 'Scrapy 爬虫框架文档', type: 'LINK', url: 'https://scrapy.org/doc/', desc: 'Scrapy 爬虫框架' },

    // ========== 路径4: 前端 ==========
    { id: 33, title: 'ES6 入门教程 - 阮一峰', type: 'LINK', url: 'https://es6.ruanyifeng.com/', desc: 'ECMAScript 6 入门' },
    { id: 34, title: 'TypeScript 中文文档', type: 'REFERENCE', url: 'https://www.typescriptlang.org/zh/docs/', desc: 'TypeScript 官方文档' },
    { id: 35, title: 'Vue 3 中文文档', type: 'REFERENCE', url: 'https://cn.vuejs.org/guide/introduction.html', desc: 'Vue 3 官方文档' },
    { id: 36, title: 'React 官方文档', type: 'REFERENCE', url: 'https://react.dev/', desc: 'React 官方文档' },
    { id: 37, title: 'Vite 官方文档', type: 'REFERENCE', url: 'https://cn.vitejs.dev/guide/', desc: 'Vite 构建工具文档' },
    { id: 38, title: 'Storybook 官方文档', type: 'LINK', url: 'https://storybook.js.org/', desc: 'Storybook 组件开发工具' },
    { id: 39, title: '前端性能优化清单', type: 'LINK', url: 'https://github.com/thedaviddias/Front-End-Checklist', desc: 'GitHub 前端优化清单' },
    { id: 40, title: 'qiankun 微前端', type: 'LINK', url: 'https://qiankun.umijs.org/zh/guide/', desc: '微前端框架 qiankun' },

    // ========== 路径5: 数据库 ==========
    { id: 43, title: 'MySQL 索引原理 - B+ 树', type: 'LINK', url: 'https://segmentfault.com/a/1190000021395728', desc: 'B+ 树索引原理解析' },
    { id: 44, title: 'MySQL 慢查询优化', type: 'LINK', url: 'https://www.cnblogs.com/xiaoboluo768/p/5423457.html', desc: '慢查询日志与 SQL 优化' },
    { id: 45, title: 'MySQL 事务隔离级别详解', type: 'LINK', url: 'https://www.cnblogs.com/huanzi-qch/p/10928867.html', desc: '事务隔离与 MVCC 详解' },
    { id: 47, title: 'Redis 官方文档', type: 'REFERENCE', url: 'https://redis.io/docs/', desc: 'Redis 数据结构与命令' },
    { id: 47, title: 'Redis 设计与实现', type: 'LINK', url: 'https://redisbook.readthedocs.io/', desc: 'Redis 底层原理' },
    { id: 49, title: 'MongoDB 官方手册', type: 'REFERENCE', url: 'https://www.mongodb.com/docs/manual/', desc: 'MongoDB 文档' },

    // ========== 路径6: 算法 ==========
    { id: 51, title: 'LeetCode 力扣', type: 'LINK', url: 'https://leetcode.cn/', desc: '力扣刷题平台' },
    { id: 52, title: '数据结构可视化', type: 'LINK', url: 'https://visualgo.net/zh', desc: '数据结构和算法可视化工具' },
    { id: 53, title: 'labuladong 算法小抄', type: 'LINK', url: 'https://labuladong.github.io/algo/', desc: '算法框架思维' },
    { id: 55, title: '十大排序算法详解', type: 'LINK', url: 'https://www.cnblogs.com/onepixel/p/7674659.html', desc: '排序算法图文详解' },
    { id: 56, title: 'OI Wiki - 图论', type: 'LINK', url: 'https://oi-wiki.org/graph/', desc: 'OI Wiki 图论算法' },
    { id: 57, title: '背包问题九讲', type: 'LINK', url: 'https://github.com/tianyicui/pack', desc: '动态规划背包问题' },
    { id: 59, title: '设计模式 - 菜鸟教程', type: 'LINK', url: 'https://www.runoob.com/design-pattern/design-pattern-tutorial.html', desc: '23 种设计模式详解' },
    { id: 60, title: 'Java 高级面试题', type: 'LINK', url: 'https://github.com/doocs/advanced-java', desc: 'GitHub 面试题汇总' },
    { id: 60, title: '牛客网 - 在线编程', type: 'LINK', url: 'https://www.nowcoder.com/', desc: '面试刷题与面经' },
  ];

  for (const r of resources) {
    await conn.execute(
      'INSERT INTO course_resource (course_id, title, type, url, description, sort_order, create_time) VALUES (?, ?, ?, ?, ?, 0, NOW())',
      [r.id, r.title, r.type, r.url, r.desc]
    );
  }

  console.log(`✅ 插入了 ${resources.length} 条课程资源`);
  await conn.end();
})().catch(e => { console.error('❌', e.message); process.exit(1); });
