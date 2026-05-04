package com.example.bankbff.client;

import com.example.bankbff.dto.AccountDto;
import com.example.bankbff.dto.TransferRequestDto;
import com.example.bankbff.dto.TransferResponseDto;
import com.example.bankbff.dto.TransactionRequestDto;
import com.example.bankbff.dto.TransactionResponseDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class BankingApiClient {

    private final WebClient bankingWebClient;

    public BankingApiClient(WebClient bankingWebClient) {
        this.bankingWebClient = bankingWebClient;
    }

    public List<AccountDto> getAccounts() {
        // TODO 6.1
        return bankingWebClient.get()
                .uri("/accounts")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AccountDto>>() {})
                .block();
    }

    public AccountDto getAccount(String accountId) {
        // TODO
        return bankingWebClient.get()
                .uri("/accounts/{accountId}", accountId)
                .retrieve()
                .bodyToMono(AccountDto.class)
                .block();
    }

    public List<TransactionResponseDto> getTransactions(String accountId) {
        // TODO
        return bankingWebClient.get()
                .uri("/accounts/{accountId}/transactions", accountId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<TransactionResponseDto>>() {})
                .block();
    }

    public TransferResponseDto postTransfer(TransferRequestDto request) {
        // TODO 6.2
        return bankingWebClient.post()
                .uri("/transfers")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TransferResponseDto.class)
                .block();
    }

    public TransactionResponseDto createTransaction(TransactionRequestDto request, String idempotencyKey) {
        // TODO
        return bankingWebClient.post()
                .uri("/transactions")
                .bodyValue(request)
                .header("Idempotency-Key", idempotencyKey)
                .retrieve()
                .bodyToMono(TransactionResponseDto.class)
                .block();
    }
}