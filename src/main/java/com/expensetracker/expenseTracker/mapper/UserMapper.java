package com.expensetracker.expenseTracker.mapper;

import com.expensetracker.expenseTracker.dto.response.AuthResponse;
import com.expensetracker.expenseTracker.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Map User entity → AuthResponse
    // token is set manually in the service after JWT generation
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "tokenType", constant = "Bearer")
    AuthResponse toAuthResponse(User user);
}
