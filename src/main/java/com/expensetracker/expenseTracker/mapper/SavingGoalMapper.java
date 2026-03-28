package com.expensetracker.expenseTracker.mapper;

import com.expensetracker.expenseTracker.dto.request.SavingGoalRequest;
import com.expensetracker.expenseTracker.dto.response.SavingGoalResponse;
import com.expensetracker.expenseTracker.entity.SavingGoal;
import com.expensetracker.expenseTracker.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SavingGoalMapper {

    // SavingGoal entity → SavingGoalResponse DTO
    @Mapping(target = "id",                 source = "id")
    @Mapping(target = "name",               source = "name")
    @Mapping(target = "targetAmount",       source = "targetAmount")
    @Mapping(target = "savedAmount",        source = "savedAmount")
    @Mapping(target = "targetDate",         source = "targetDate")
    @Mapping(target = "status",             source = "status")
    @Mapping(target = "note",               source = "note")
    @Mapping(target = "createdAt",          source = "createdAt")
    @Mapping(target = "progressPercentage",
            expression = "java(savingGoal.getProgressPercentage())")
    SavingGoalResponse toResponse(SavingGoal savingGoal);

    // SavingGoalRequest + User → SavingGoal entity
    @Mapping(target = "id",           ignore = true)
    @Mapping(target = "createdAt",    ignore = true)
    @Mapping(target = "updatedAt",    ignore = true)
    @Mapping(target = "name",         source = "request.name")
    @Mapping(target = "targetAmount", source = "request.targetAmount")
    @Mapping(target = "targetDate",   source = "request.targetDate")
    @Mapping(target = "note",         source = "request.note")
    @Mapping(target = "user",         source = "user")
    @Mapping(target = "status",       constant = "IN_PROGRESS")
    @Mapping(target = "savedAmount",
            expression = "java(request.getSavedAmount() != null ? request.getSavedAmount() : java.math.BigDecimal.ZERO)")
    SavingGoal toEntity(SavingGoalRequest request, User user);

    // Partial update — preserves status, savedAmount, user
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",          ignore = true)
    @Mapping(target = "createdAt",   ignore = true)
    @Mapping(target = "updatedAt",   ignore = true)
    @Mapping(target = "status",      ignore = true)
    @Mapping(target = "savedAmount", ignore = true)
    @Mapping(target = "user",        ignore = true)
    void updateEntityFromRequest(SavingGoalRequest request,
                                 @MappingTarget SavingGoal savingGoal);
}
