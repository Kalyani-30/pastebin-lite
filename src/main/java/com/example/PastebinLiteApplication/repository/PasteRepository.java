package com.example.PastebinLiteApplication.repository;

import com.example.PastebinLiteApplication.model.Paste;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasteRepository extends JpaRepository<Paste, String> {
}
