package com.expensetracker.expenseTracker.mapper;

import com.expensetracker.expenseTracker.dto.request.IncomeRequest;
import com.expensetracker.expenseTracker.dto.response.IncomeResponse;
import com.expensetracker.expenseTracker.entity.Income;
import com.expensetracker.expenseTracker.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface IncomeMapper {

    // Entity → Response DTO
    IncomeResponse toResponse(Income income);

    // Request DTO → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", source = "user")
    Income toEntity(IncomeRequest request, User user);

    // Update existing entity fields from request (ignores null)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromRequest(IncomeRequest request, @MappingTarget Income income);
}