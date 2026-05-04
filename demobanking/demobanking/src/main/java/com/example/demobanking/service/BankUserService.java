package com.example.demobanking.service;
import com.example.demobanking.dto.BankUserRequest;
import com.example.demobanking.dto.BankUserResponse;
import com.example.demobanking.entity.BankUser;
import com.example.demobanking.exception.DuplicateResourceException;
 import com.example.demobanking.exception.ResourceNotFoundException;
import com.example.demobanking.repository.BankUserRepository;
//import com.example.demobanking.service.BankUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

  import java.util.List;
  @Service
  @Transactional
public class BankUserService {
      private final BankUserRepository bankUserRepository;

      public BankUserService(BankUserRepository bankUserRepository) {
          this.bankUserRepository = bankUserRepository;
      }


      public BankUserResponse createUser(BankUserRequest request) {
          validateCreateRequest(request);
          BankUser entity = buildEntity(request);
          BankUser saved = bankUserRepository.save(entity);

          return new BankUserResponse(saved);
      }


      @Transactional
      public BankUserResponse getUserById(String userId) {
          return new BankUserResponse(getUserEntity(userId));
      }


      @Transactional(readOnly = true)
      public List<BankUserResponse> getAllUsers() {
          return bankUserRepository.findAll()
                  .stream()
                  .map(BankUserResponse::new)
                  .toList();
      }


      public BankUserResponse updateUser(String userId, BankUserRequest request) {
          if (!userId.equals(request.getUserId())) {
              throw new IllegalArgumentException("Path userId and request userId must match");
          }
          BankUser existingUser = getUserEntity(userId);
       if (bankUserRepository.existsBySubjectAndUserIdNot(request.getSubject(),userId) )
             {
                   throw new DuplicateResourceException("Another user already exists with subject: "+request.getSubject());
             }
          updateEntity(existingUser, request);
          BankUser updated = bankUserRepository.save(existingUser);
          return new BankUserResponse(updated);
      }



      public void deleteUser(String userId) {
          BankUser existingUser = getUserEntity(userId);
          bankUserRepository.delete(existingUser);
      }

      private void validateCreateRequest(BankUserRequest request) {
          if (bankUserRepository.existsById(request.getUserId())) {
              throw new DuplicateResourceException("Another user already exists with userId: " + request.getUserId());
          }
          if (bankUserRepository.existsBySubject(request.getSubject())) {
              throw new DuplicateResourceException("Another user already exists with subject: " + request.getSubject());
          }
      }

      private BankUser getUserEntity(String userId) {
          return bankUserRepository.findById(userId)
                  .orElseThrow(() -> new ResourceNotFoundException("BankUser not found with userId: " + userId));

      }

      private BankUser buildEntity(BankUserRequest request) {
          BankUser entity = new BankUser();
          entity.setUserId(request.getUserId());
          entity.setSubject(request.getSubject());
          entity.setEmail(request.getEmail());
          entity.setDisplayName(request.getDisplayName());
          entity.setRole(request.getRole());
             return entity;
      }
      private void updateEntity(BankUser entity, BankUserRequest request) {
                  entity.setUserId(request.getUserId());
                  entity.setSubject(request.getSubject()) ;
                  entity.setEmail(request.getEmail());
                 entity.setDisplayName(request.getDisplayName());
                  entity.setRole(request.getRole());
      }

  }

