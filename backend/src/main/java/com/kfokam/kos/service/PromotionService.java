package com.kfokam.kos.service;

import com.kfokam.kos.dto.PromotionResponse;
import java.util.List;

public interface PromotionService {

    List<PromotionResponse> findAll();

    PromotionResponse findById(Long id);

    PromotionResponse create(String nom);
}
