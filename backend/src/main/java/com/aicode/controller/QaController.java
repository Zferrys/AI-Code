package com.aicode.controller;

import com.aicode.dto.*;
import com.aicode.service.QaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * AI 问答控制器
 */
@RestController
@RequestMapping("/api/qa")
public class QaController {

    @Autowired
    private QaService qaService;

    /**
     * 提问
     */
    @PostMapping("/ask")
    public ApiResponse<QaQuestionVO> ask(HttpServletRequest request,
                                          @RequestBody QaQuestionRequest questionRequest) {
        Long userId = (Long) request.getAttribute("userId");
        QaQuestionVO vo = qaService.askQuestion(userId, questionRequest);
        return ApiResponse.success(vo);
    }

    /**
     * 获取问题详情
     */
    @GetMapping("/{id}")
    public ApiResponse<QaQuestionVO> detail(@PathVariable Long id) {
        QaQuestionVO vo = qaService.getQuestionDetail(id);
        return ApiResponse.success(vo);
    }

    /**
     * 获取当前用户的问题列表（仅自己的历史记录）
     */
    @GetMapping("/list")
    public ApiResponse<?> list(HttpServletRequest request,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        List<QaQuestionVO> list = qaService.listUserQuestions(userId, page, pageSize);
        long total = qaService.countUserQuestions(userId);
        return ApiResponse.success(ApiResponse.page(list, total, page, pageSize));
    }

    /**
     * 追问
     */
    @PostMapping("/follow-up")
    public ApiResponse<QaAnswerVO> followUp(HttpServletRequest request,
                                             @RequestBody QaFollowUpRequest followUpRequest) {
        Long userId = (Long) request.getAttribute("userId");
        QaAnswerVO vo = qaService.askFollowUp(userId, followUpRequest);
        return ApiResponse.success(vo);
    }

    /**
     * 搜索问题
     */
    @GetMapping("/search")
    public ApiResponse<?> search(@RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) String category,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int pageSize) {
        List<QaQuestionVO> list = qaService.searchQuestions(keyword, category, page, pageSize);
        long total = qaService.countSearch(keyword, category);
        return ApiResponse.success(ApiResponse.page(list, total, page, pageSize));
    }
}
