package com.kfokam.kos.service.impl;

import com.kfokam.kos.dto.PromotionResponse;
import com.kfokam.kos.repository.PromotionRepository;
import com.kfokam.kos.service.PromotionService;
import java.util.List;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new RuntimeException("Promotion non trouvée")));
    }

    @Override
    public PromotionResponse create(String nom) {
        return PromotionResponse.from(promotionRepository.save(
                Promotion.builder().nom(nom).build()));
    }
}
