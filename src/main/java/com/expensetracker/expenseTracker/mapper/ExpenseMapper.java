package com.expensetracker.expenseTracker.mapper;

import com.expensetracker.expenseTracker.dto.request.ExpenseRequest;
import com.expensetracker.expenseTracker.dto.response.ExpenseResponse;
import com.expensetracker.expenseTracker.entity.Expense;
import com.expensetracker.expenseTracker.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    // Entity → Response DTO
    ExpenseResponse toResponse(Expense expense);

    // Request DTO → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", source = "user")
    Expense toEntity(ExpenseRequest request, User user);

    // Partial update
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromRequest(ExpenseRequest request, @MappingTarget Expense expense);
}
