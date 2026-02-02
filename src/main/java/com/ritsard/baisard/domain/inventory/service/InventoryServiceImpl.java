package com.ritsard.baisard.domain.inventory.service;

import com.ritsard.baisard.domain.inventory.dto.request.InventoryTransactionRequestDto;
import com.ritsard.baisard.domain.inventory.entity.Inventory;
import com.ritsard.baisard.domain.inventory.entity.Product;
import com.ritsard.baisard.domain.inventory.repository.InventoryRepository;
import com.ritsard.baisard.domain.inventory.repository.ProductRepository;
import com.ritsard.baisard.global.exception.ConflictException;
import com.ritsard.baisard.utils.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public void stockProduct(UUID uuidProduct, BigDecimal qty) {

        if (qty == null || qty.compareTo(BigDecimal.ZERO) == 0)
            throw new ConflictException("Quantity must not be zero.");

        if (!productRepository.existsById(uuidProduct))
            throw new NotFoundException("Product not found.");

        productRepository.incrementStock(uuidProduct, qty);

//                auditLog.addManagerAudit(
//                        String.format("Stock IN: %s units added to product '%s' (Ref: %s)",
//                                qty, product.getName(), dto.getReference())
//                );
    }

    @Override
    public void RecordInventoryTransaction(InventoryTransactionRequestDto dto) {

        // Validate transaction type
        if (dto.getInventoryTransactionType() == null)
            throw new ConflictException("Inventory transaction type is required.");


        if (dto.getQuantity() == null || dto.getQuantity().compareTo(BigDecimal.ZERO) <= 0)
            throw new ConflictException("Quantity must be greater than zero.");


        // Fetch product
        Product product = productRepository.findById(dto.getUuidProduct())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        BigDecimal qty = dto.getQuantity();

        // Adjust product quantity
        switch (dto.getInventoryTransactionType()) {
            case IN -> product.setQuantity(product.getQuantity() != null
                    ? product.getQuantity().add(qty)
                    : qty);
            case OUT -> {
                if (product.getQuantity() == null || product.getQuantity().compareTo(qty) < 0) {
                    throw new IllegalStateException("Not enough stock.");
                }
                product.setQuantity(product.getQuantity().subtract(qty));

//                auditLog.addCashierAudit(
//                        String.format("Stock OUT: %s units removed from product '%s' (Ref: %s)",
//                                qty, product.getName(), dto.getReference())
//                );
            }
            case ADJUSTMENT -> //                auditLog.addManagerAudit(
                //                        String.format("Stock ADJUSTMENT: Product '%s' quantity set to %s (Ref: %s)",
                //                                product.getName(), qty, dto.getReference())
                //                );
                    product.setQuantity(qty);
            default -> throw new IllegalArgumentException("Invalid inventory transaction type.");
        }

        // Create inventory record
        Inventory inventory = Inventory.builder()
                .product(product)
                .quantity(qty)
                .type(dto.getInventoryTransactionType())
                .reference(dto.getReference())
                .build();

        inventoryRepository.save(inventory);
        productRepository.save(product);
    }

}
