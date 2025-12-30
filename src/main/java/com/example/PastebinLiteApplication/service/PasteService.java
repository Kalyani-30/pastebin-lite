package com.example.PastebinLiteApplication.service;

import com.example.PastebinLiteApplication.model.Paste;
import com.example.PastebinLiteApplication.repository.PasteRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PasteService {

    private final PasteRepository repo;

    public PasteService(PasteRepository repo) {
        this.repo = repo;
    }

    public Paste create(String content, Integer ttl, Integer maxViews) {
        Paste p = new Paste();

        p.setId(UUID.randomUUID().toString());
        p.setContent(content);
        p.setViews(0);   // 🔥 IMPORTANT
        p.setMaxViews(maxViews);

        if (ttl != null) {
            p.setExpiresAt(System.currentTimeMillis() + (ttl * 1000L));
        }

        return repo.save(p);
    }


    public Optional<Paste> getValidPaste(String id, long now) {

        Optional<Paste> opt = repo.findById(id);
        if (opt.isEmpty()) return Optional.empty();

        Paste p = opt.get();

        if (p.getExpiresAt() != null && now > p.getExpiresAt())
            return Optional.empty();

        if (p.getMaxViews() != null && p.getViews() >= p.getMaxViews())
            return Optional.empty();

        return Optional.of(p);
    }

    public void incrementViews(Paste paste) {
        if (paste.getViews() == null) {
            paste.setViews(1);
        } else {
            paste.setViews(paste.getViews() + 1);
        }
        repo.save(paste);
    }

}
