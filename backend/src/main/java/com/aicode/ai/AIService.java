package com.aicode.ai;

/**
 * AI 服务接口
 * 封装与 AI 模型的交互，供各业务模块复用
 */
public interface AIService {

    /**
     * 发送消息到 AI 模型并获取回复
     * @param systemPrompt 系统提示词
     * @param userMessage  用户消息
     * @param timeoutMs    超时时间（毫秒）
     * @return AI 回复内容
     */
    String chat(String systemPrompt, String userMessage, int timeoutMs);

    /**
     * 发送消息到 AI 模型并获取回复（使用默认超时）
     */
    String chat(String systemPrompt, String userMessage);
}
