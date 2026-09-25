package com.kfokam.kos.service.impl;

import com.kfokam.kos.dto.PromotionResponse;
import com.kfokam.kos.exception.ResourceNotFoundException;
import com.kfokam.kos.model.Promotion;
import com.kfokam.kos.repository.PromotionRepository;
import com.kfokam.kos.service.PromotionService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionServiceImpl(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @Override
    public List<PromotionResponse> findAll() {
        return promotionRepository.findAll().stream()
                .map(PromotionResponse::from)
                .toList();
    }

    @Override
    public PromotionResponse findById(Long id) {
        return PromotionResponse.from(promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PROMOTION_INCONNUE", "Promotion introuvable")));
    }

    @Override
    @Transactional
    public PromotionResponse create(String nom) {
        return PromotionResponse.from(promotionRepository.save(Promotion.builder().nom(nom.trim()).build()));
    }
}
