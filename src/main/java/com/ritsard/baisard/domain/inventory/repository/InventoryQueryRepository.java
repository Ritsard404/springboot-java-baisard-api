package com.ritsard.baisard.domain.inventory.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ritsard.baisard.base.repository.BaseQueryDslRepositoryImpl;
import com.ritsard.baisard.domain.inventory.dto.response.InventoryDto;
import com.ritsard.baisard.domain.inventory.entity.Inventory;
import com.ritsard.baisard.domain.inventory.entity.QInventory;
import com.ritsard.baisard.domain.inventory.entity.QProduct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public class InventoryQueryRepository extends BaseQueryDslRepositoryImpl<Inventory, UUID> {

    public InventoryQueryRepository(JPAQueryFactory queryFactory, EntityManager entityManager) {
        super(queryFactory, entityManager);
    }

    public List<InventoryDto> findInventoryDtos() {
        QInventory i = QInventory.inventory;
        QProduct p = QProduct.product;

        return queryFactory
                .select(Projections.constructor(
                        InventoryDto.class,
                        i.uuidInventory,
                        i.quantity,
                        i.type,
                        i.reference,
                        p.uuidProduct,
                        p.name
                ))
                .from(i)
                .join(i.product, p)
                .fetch();
    }

    @Override
    protected EntityPathBase<Inventory> getEntityPath() {
        return null;
    }

    @Override
    protected SimpleExpression<UUID> getIdPath() {
        return null;
    }

    @Override
    protected Class<Inventory> getEntityClass() {
        return null;
    }

    @Override
    protected void addDynamicConditions(BooleanBuilder builder, Object searchCondition) {

    }
}
