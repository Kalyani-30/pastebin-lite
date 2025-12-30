package com.example.PastebinLiteApplication.controller;

import com.example.PastebinLiteApplication.model.Paste;
import com.example.PastebinLiteApplication.service.PasteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PasteController {

    private final PasteService service;

    public PasteController(PasteService service) {
        this.service = service;
    }

    // Health check
    @GetMapping("/healthz")
    public Map<String, Boolean> health() {
        return Map.of("ok", true);
    }

    // Create paste
    @PostMapping("/pastes")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {

        String content = (String) body.get("content");
        if (content == null || content.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "content is required"));
        }

        Integer ttl = body.get("ttl_seconds") == null
                ? null
                : ((Number) body.get("ttl_seconds")).intValue();

        Integer maxViews = body.get("max_views") == null
                ? null
                : ((Number) body.get("max_views")).intValue();

        Paste p = service.create(content, ttl, maxViews);

        return ResponseEntity.ok(
                Map.of(
                        "id", p.getId(),
                        "url", "/p/" + p.getId()
                )
        );
    }

    // Fetch paste (API)
    @GetMapping("/pastes/{id}")
    public ResponseEntity<?> fetch(
            @PathVariable String id,
            @RequestHeader(value = "x-test-now-ms", required = false) Long testNow
    ) {
        long now = testNow != null ? testNow : System.currentTimeMillis();

        Optional<Paste> opt = service.getValidPaste(id, now);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Not found"));
        }

        Paste p = opt.get();
        service.incrementViews(p);

        Map<String, Object> response = new HashMap<>();

        response.put("content", p.getContent());

        if (p.getMaxViews() != null) {
            response.put("remaining_views", p.getMaxViews() - p.getViews());
        } else {
            response.put("remaining_views", null);
        }

        if (p.getExpiresAt() != null) {
            response.put("expires_at", new Date(p.getExpiresAt()).toInstant());
        } else {
            response.put("expires_at", null);
        }

        return ResponseEntity.ok(response);

    }
}
