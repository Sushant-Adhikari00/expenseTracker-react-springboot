package com.expensetracker.expenseTracker.mapper;

import com.expensetracker.expenseTracker.dto.request.IncomeRequest;
import com.expensetracker.expenseTracker.dto.response.IncomeResponse;
import com.expensetracker.expenseTracker.dto.response.PageResponse;
import com.expensetracker.expenseTracker.entity.Income;
import com.expensetracker.expenseTracker.entity.User;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface IncomeMapper {

    // Income entity → IncomeResponse DTO
    @Mapping(target = "id",         source = "id")
    @Mapping(target = "source",     source = "source")
    @Mapping(target = "amount",     source = "amount")
    @Mapping(target = "date",       source = "date")
    @Mapping(target = "category",   source = "category")
    @Mapping(target = "note",       source = "note")
    @Mapping(target = "createdAt",  source = "createdAt")
    IncomeResponse toResponse(Income income);

    // IncomeRequest + User → Income entity
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "source",    source = "request.source")
    @Mapping(target = "amount",    source = "request.amount")
    @Mapping(target = "date",      source = "request.date")
    @Mapping(target = "category",  source = "request.category")
    @Mapping(target = "note",      source = "request.note")
    @Mapping(target = "user",      source = "user")
    Income toEntity(IncomeRequest request, User user);

    // Partial update — only non-null fields overwrite existing entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user",      ignore = true)
    void updateEntityFromRequest(IncomeRequest request, @MappingTarget Income income);

    // Page<Income> → PageResponse<IncomeResponse>
    default PageResponse<IncomeResponse> toPageResponse(Page<Income> page) {
        return PageResponse.<IncomeResponse>builder()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}