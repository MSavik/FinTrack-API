package com.fintrack.fintrack_api.repository;

import com.fintrack.fintrack_api.model.Account;
import com.fintrack.fintrack_api.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a WHERE a.user = :user AND a.status = 'ACTIVE'")
    List<Account> findActiveAccountsByUser(Users user);

    @Query("SELECT a FROM Account a WHERE a.id = :id AND a.user = :user AND a.status = 'ACTIVE'")
    Optional<Account> findActiveAccountByIdAndUser(Long id, Users user);

    @Query("SELECT a FROM Account a " +
            "JOIN FETCH a.user u " +
            "WHERE (:email IS NULL OR u.email = :email) " +
            "AND (:type IS NULL OR a.type = :type) " +
            "AND (:status IS NULL OR a.status = :status)")
    Page<Account> findFilteredAccounts(
            @Param("email") String email,
            @Param("type") Account.AccountType type,
            @Param("status") Account.AccountStatus status,
            Pageable pageable);

    boolean existsByIdAndUserAndStatus(Long id, Users user, Account.AccountStatus status);
}
