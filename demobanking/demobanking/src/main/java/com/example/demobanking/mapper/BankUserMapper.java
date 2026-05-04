package com.example.demobanking.mapper;
import com.example.demobanking.dto.BankUserRequest;
import com.example.demobanking.dto.BankUserResponse;
import com.example.demobanking.entity.BankUser;
public class BankUserMapper {
    private BankUserMapper(){

    }
     public static BankUser toEntity(BankUserRequest request) {
        BankUser entity = new BankUser();
        entity.setUserId(request.getUserId());
        entity.setSubject(request.getSubject());
        entity.setEmail(request.getEmail());
        entity.setDisplayName(request.getDisplayName());
        entity.setRole(request.getRole());
        return entity;
    }
    public static BankUserResponse toResponse(BankUser entity) {
        BankUserResponse response = new BankUserResponse();
        response.setSubject(entity.getSubject());
        response.setEmail(entity.getEmail());
        response.setDisplayName(entity.getDisplayName());
        response.setRole(entity.getRole());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }

    public static void updateEntity(BankUser entity, BankUserRequest request) {
        entity.setSubject(request.getSubject());
        entity.setEmail(request.getEmail());
        entity.setDisplayName(request.getDisplayName());
        entity.setRole(request.getRole());
    }
}
