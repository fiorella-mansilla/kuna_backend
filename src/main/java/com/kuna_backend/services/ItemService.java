package com.kuna_backend.services;

import com.kuna_backend.exceptions.ItemNotExistsException;
import com.kuna_backend.models.Item;
import com.kuna_backend.repositories.ItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    public List<Item> getAllItems() {
        return (List<Item>) itemRepository.findAll();
    }

    public Item getItem (Integer id) {
        return itemRepository.findById(id).get();
    }

    public void createItem (Item item) {
        itemRepository.save(item);
    }

    public void deleteItem (Integer id) {
        itemRepository.deleteById(id);
    }

    public Item findById(Integer itemId) throws ItemNotExistsException {
        Optional<Item> optionalItem= itemRepository.findById(itemId);
        // Check if the item exists
        if (optionalItem.isEmpty()) {
            throw new ItemNotExistsException("Item id is invalid:" + itemId);
        }
        return optionalItem.get();
    }
}
