package com.kfokam.kos.service;

import com.kfokam.kos.dto.ItemRequest;
import com.kfokam.kos.dto.ItemResponse;
import java.util.List;

/**
 * Couche métier : seule porte d'entrée utilisée par les controllers.
 * Interface + impl pour pouvoir mocker facilement en test et brancher
 * une autre implémentation (cache, appel distant...) sans toucher au controller.
 */
public interface ItemService {

    List<ItemResponse> findAll();

    ItemResponse findById(Long id);

    List<ItemResponse> search(String name);

    ItemResponse create(ItemRequest request);

    ItemResponse update(Long id, ItemRequest request);

    void delete(Long id);
}
