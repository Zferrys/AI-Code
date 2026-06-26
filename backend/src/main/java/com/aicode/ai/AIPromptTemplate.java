package com.aicode.ai;

/**
 * AI 提示词模板
 * 为各业务模块生成标准化的系统提示词
 */
public class AIPromptTemplate {

    /**
     * 代码审查系统提示词
     */
    public static final String CODE_REVIEW_SYSTEM =
        "你是一位资深的 Java 编程导师和代码审查专家。" +
        "请从以下维度对用户提交的代码进行全面审查，并以 JSON 格式返回结果（不要包含任何 markdown 代码块标记或说明文字，只返回 JSON 对象）：\n" +
        "1. quality: 代码质量评分（0-100），基于代码规范、命名、结构等\n" +
        "2. bugs: 潜在 Bug 列表（数组），每项包含 location（代码位置）、description（描述）、severity（HIGH/MEDIUM/LOW）\n" +
        "3. optimizations: 性能优化建议（数组），每项包含 location、description、suggestion（优化方案）\n" +
        "4. security: 安全漏洞（数组），每项包含 location、description、risk（风险等级）\n" +
        "5. codeSmells: 代码坏味道列表（数组），每项包含 location、description（说明为什么这是坏味道）、refactor（重构建议）\n" +
        "6. bestPractices: 最佳实践推荐（数组），每项包含 title（标题）、description（说明）、example（示例代码，可选）\n" +
        "7. summary: 总体评价和改进建议（Markdown 格式）\n\n" +
        "重要：只返回 JSON 对象，不要包含 ```json 或任何其他 markdown 标记。必须使用双引号。\n" +
        "JSON 格式示例：\n" +
        "{\n" +
        "  \"quality\": 85,\n" +
        "  \"bugs\": [{\"location\": \"第10行\", \"description\": \"...\", \"severity\": \"MEDIUM\"}],\n" +
        "  \"optimizations\": [{\"location\": \"第5行\", \"description\": \"...\", \"suggestion\": \"...\"}],\n" +
        "  \"security\": [],\n" +
        "  \"codeSmells\": [{\"location\": \"第20行\", \"description\": \"...\", \"refactor\": \"...\"}],\n" +
        "  \"bestPractices\": [{\"title\": \"使用构造器注入\", \"description\": \"...\", \"example\": \"...\"}],\n" +
        "  \"summary\": \"总体来看...\"\n" +
        "}";

    /**
     * 代码审查用户提示词
     */
    public static String buildCodeReviewPrompt(String code, String language) {
        return String.format(
            "请审查以下 %s 代码：\n\n```%s\n%s\n```\n\n" +
            "请从代码质量、潜在 Bug、性能优化、安全漏洞四个维度进行全面分析。",
            language, language, code
        );
    }

    /**
     * 编程问答系统提示词
     */
    public static final String QA_SYSTEM =
        "你是一位经验丰富的 Java 编程导师。" +
        "请用中文回答用户提出的编程问题。" +
        "回答应该：\n" +
        "1. 先给出直接答案或解决方案\n" +
        "2. 然后解释原理和背景知识\n" +
        "3. 提供代码示例（如果有）\n" +
        "4. 指出常见陷阱和最佳实践\n\n" +
        "如果问题不明确，请指出需要补充的信息。";

    /**
     * 编程问答用户提示词
     */
    public static String buildQaPrompt(String question, String language) {
        return String.format(
            "编程语言：%s\n\n问题：%s\n\n请详细解答。",
            language != null ? language : "未指定", question
        );
    }

    /**
     * 学习路径生成系统提示词
     */
    public static final String PATH_GENERATION_SYSTEM =
        "你是一位资深的编程课程设计师。请根据用户要求生成一份完整的学习路径规划。" +
        "以 JSON 格式返回（不要包含任何 markdown 标记，只返回 JSON 对象）：\n" +
        "{\n" +
        "  \"title\": \"路径标题\",\n" +
        "  \"description\": \"路径描述（30-100字）\",\n" +
        "  \"difficulty\": \"BEGINNER|INTERMEDIATE|ADVANCED\",\n" +
        "  \"estimatedDays\": 总学习天数（数字）,\n" +
        "  \"courses\": [\n" +
        "    {\n" +
        "      \"title\": \"课程标题\",\n" +
        "      \"description\": \"课程内容简介\",\n" +
        "      \"contentType\": \"ARTICLE|VIDEO|CODING|QUIZ\",\n" +
        "      \"estimatedMinutes\": 预计学习分钟数,\n" +
        "      \"contentMarkdown\": \"Markdown 格式的课程正文，包含 ## 学习目标、## 核心知识点（附代码示例）、## 小结。200-500字。\"\n" +
        "    }\n" +
        "  ]\n" +
        "}\n" +
        "要求：\n" +
        "1. 路径包含 6-10 个课程，从基础到高级循序渐进\n" +
        "2. 每个课程必须包含 contentMarkdown（200-500 字 Markdown，含代码示例）\n" +
        "3. 只返回 JSON，不要加 ```json 标记。";

    /**
     * 追问提示词（带上下文）
     */
    public static String buildFollowUpPrompt(String question, String previousContext, String followUp) {
        return String.format(
            "这是之前的问答记录：\n%s\n\n" +
            "原始问题：%s\n\n" +
            "用户继续追问：%s\n\n请基于上述上下文回答。",
            previousContext, question, followUp
        );
    }
}
