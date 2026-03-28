package com.expensetracker.expenseTracker.mapper;

import com.expensetracker.expenseTracker.dto.request.ExpenseRequest;
import com.expensetracker.expenseTracker.dto.response.ExpenseResponse;
import com.expensetracker.expenseTracker.entity.Expense;
import com.expensetracker.expenseTracker.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    // Expense entity → ExpenseResponse DTO
    @Mapping(target = "id",        source = "id")
    @Mapping(target = "title",     source = "title")
    @Mapping(target = "amount",    source = "amount")
    @Mapping(target = "date",      source = "date")
    @Mapping(target = "category",  source = "category")
    @Mapping(target = "note",      source = "note")
    @Mapping(target = "createdAt", source = "createdAt")
    ExpenseResponse toResponse(Expense expense);

    // ExpenseRequest + User → Expense entity
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "title",     source = "request.title")
    @Mapping(target = "amount",    source = "request.amount")
    @Mapping(target = "date",      source = "request.date")
    @Mapping(target = "category",  source = "request.category")
    @Mapping(target = "note",      source = "request.note")
    @Mapping(target = "user",      source = "user")
    Expense toEntity(ExpenseRequest request, User user);

    // Partial update
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user",      ignore = true)
    void updateEntityFromRequest(ExpenseRequest request, @MappingTarget Expense expense);
}
