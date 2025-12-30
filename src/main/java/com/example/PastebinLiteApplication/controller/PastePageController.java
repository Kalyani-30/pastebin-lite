package com.example.PastebinLiteApplication.controller;


import com.example.PastebinLiteApplication.model.Paste;
import com.example.PastebinLiteApplication.service.PasteService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
public class PastePageController {

    private final PasteService service;

    public PastePageController(PasteService service) {
        this.service = service;
    }

    @GetMapping("/p/{id}")
    public String viewPaste(@PathVariable String id, Model model) {

        long now = System.currentTimeMillis();
        Optional<Paste> opt = service.getValidPaste(id, now);

        if (opt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Paste paste = opt.get();
        service.incrementViews(paste);

        model.addAttribute("content",
                paste.getContent() == null ? "" : paste.getContent()
        );

        return "paste";
    }
}



