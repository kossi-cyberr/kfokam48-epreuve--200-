package com.kfokam.kos.service.impl;

import com.kfokam.kos.dto.ItemRequest;
import com.kfokam.kos.dto.ItemResponse;
import com.kfokam.kos.exception.ResourceNotFoundException;
import com.kfokam.kos.model.Item;
import com.kfokam.kos.repository.ItemRepository;
import com.kfokam.kos.service.ItemService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponse> findAll() {
        return itemRepository.findAll().stream().map(ItemResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponse findById(Long id) {
        return ItemResponse.from(getItem(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponse> search(String name) {
        return itemRepository.findByNameContainingIgnoreCase(name).stream()
                .map(ItemResponse::from)
                .toList();
    }

    @Override
    public ItemResponse create(ItemRequest request) {
        Item item = Item.builder()
                .name(request.name().trim())
                .description(request.description())
                .quantity(request.quantity() == null ? 0 : request.quantity())
                .build();
        return ItemResponse.from(itemRepository.save(item));
    }

    @Override
    public ItemResponse update(Long id, ItemRequest request) {
        Item item = getItem(id);
        item.setName(request.name().trim());
        item.setDescription(request.description());
        if (request.quantity() != null) {
            item.setQuantity(request.quantity());
        }
        return ItemResponse.from(itemRepository.save(item));
    }

    @Override
    public void delete(Long id) {
        itemRepository.delete(getItem(id));
    }

    private Item getItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", id));
    }
}
