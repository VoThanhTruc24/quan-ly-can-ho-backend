package com.example.backend.controller;

import com.example.backend.service.ChatAIService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/owner/ai")
@CrossOrigin(origins = "http://localhost:5173")
public class ChatAIController {

    private final ChatAIService chatAIService;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public ChatAIController(
            ChatAIService chatAIService
    ) {
        this.chatAIService = chatAIService;
    }

    // ============================================================
    // POST /api/owner/ai/chat
    // ============================================================

    @PostMapping("/chat")
    public ResponseEntity<?> chat(
            @RequestBody ChatRequest request
    ) {

        try {

            // ====================================================
            // VALIDATE REQUEST
            // ====================================================

            if (request == null) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "message",
                                        "Dữ liệu gửi lên không hợp lệ."
                                )
                        );
            }

            if (request.getMessage() == null ||
                    request.getMessage().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "message",
                                        "Vui lòng nhập câu hỏi."
                                )
                        );
            }

            // ====================================================
            // HISTORY
            // ====================================================

            List<ChatMessage> history =
                    request.getHistory();

            if (history == null) {
                history = List.of();
            }

            // ====================================================
            // CHAT AI
            // ====================================================

            String reply =
                    chatAIService.chat(
                            request.getMessage().trim(),
                            history
                    );

            // ====================================================
            // RESPONSE
            // ====================================================

            return ResponseEntity.ok(
                    Map.of(
                            "reply",
                            reply
                    )
            );

        } catch (RuntimeException e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage() != null
                                            ? e.getMessage()
                                            : "Có lỗi xảy ra khi xử lý câu hỏi."
                            )
                    );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "message",
                                    "Máy chủ RentHub đang gặp lỗi."
                            )
                    );
        }
    }

    // ============================================================
    // CHAT REQUEST
    // ============================================================

    public static class ChatRequest {

        private String message;

        private List<ChatMessage> history;

        public ChatRequest() {
        }

        // --------------------------------------------------------
        // MESSAGE
        // --------------------------------------------------------

        public String getMessage() {
            return message;
        }

        public void setMessage(
                String message
        ) {
            this.message = message;
        }

        // --------------------------------------------------------
        // HISTORY
        // --------------------------------------------------------

        public List<ChatMessage> getHistory() {
            return history;
        }

        public void setHistory(
                List<ChatMessage> history
        ) {
            this.history = history;
        }
    }

    // ============================================================
    // CHAT MESSAGE
    // ============================================================

    public static class ChatMessage {

        private String role;

        private String content;

        public ChatMessage() {
        }

        // --------------------------------------------------------
        // ROLE
        // --------------------------------------------------------

        public String getRole() {
            return role;
        }

        public void setRole(
                String role
        ) {
            this.role = role;
        }

        // --------------------------------------------------------
        // CONTENT
        // --------------------------------------------------------

        public String getContent() {
            return content;
        }

        public void setContent(
                String content
        ) {
            this.content = content;
        }
    }
}