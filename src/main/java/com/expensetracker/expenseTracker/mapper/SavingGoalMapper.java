package com.expensetracker.expenseTracker.mapper;

import com.expensetracker.expenseTracker.dto.request.SavingGoalRequest;
import com.expensetracker.expenseTracker.dto.response.SavingGoalResponse;
import com.expensetracker.expenseTracker.entity.SavingGoal;
import com.expensetracker.expenseTracker.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SavingGoalMapper {

    // Entity → Response DTO
    // progressPercentage is a @Transient method on entity — MapStruct picks it up
    @Mapping(target = "progressPercentage", expression = "java(savingGoal.getProgressPercentage())")
    SavingGoalResponse toResponse(SavingGoal savingGoal);

    // Request DTO → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", constant = "IN_PROGRESS")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "savedAmount",
            expression = "java(request.getSavedAmount() != null ? request.getSavedAmount() : java.math.BigDecimal.ZERO)")
    SavingGoal toEntity(SavingGoalRequest request, User user);

    // Partial update — preserves status and savedAmount unless explicitly set
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromRequest(SavingGoalRequest request, @MappingTarget SavingGoal savingGoal);
}
