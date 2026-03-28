package com.expensetracker.expenseTracker.mapper;

import com.expensetracker.expenseTracker.dto.request.RegisterRequest;
import com.expensetracker.expenseTracker.dto.response.AuthResponse;
import com.expensetracker.expenseTracker.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // RegisterRequest → User entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "incomes", ignore = true)
    @Mapping(target = "expenses", ignore = true)
    @Mapping(target = "savingGoals", ignore = true)
    @Mapping(target = "password", ignore = true) // encoded separately in service
    User toEntity(RegisterRequest request);

    // User entity → AuthResponse
    @Mapping(target = "token", ignore = true)   // set manually after JWT generation
    @Mapping(target = "tokenType", constant = "Bearer")
    AuthResponse toAuthResponse(User user);
}
