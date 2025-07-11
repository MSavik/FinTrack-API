package com.fintrack.fintrack_api.mapper;

import com.fintrack.fintrack_api.dto.request.CreateAccountRequestDTO;
import com.fintrack.fintrack_api.dto.request.UpdateAccountRequestDTO;
import com.fintrack.fintrack_api.dto.response.AccountResponseDTO;
import com.fintrack.fintrack_api.model.Account;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "userEmail", source = "user.email")
    AccountResponseDTO toAccountResponseDTO(Account account);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Account toAccount(CreateAccountRequestDTO accountRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "currency", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Account toAccount(UpdateAccountRequestDTO accountRequest);
}
