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

    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber AND a.user = :user AND a.status = 'ACTIVE'")
    Optional<Account> findActiveAccountByAccountNumberAndUser(String accountNumber, Users user);

    @Query("SELECT a FROM Account a " +
            "JOIN FETCH a.user u " +
            "WHERE (:email IS NULL OR u.email = :email) " +
            "AND (:type IS NULL OR a.type = :type) " +
            "AND (:status IS NULL OR a.status = :status)" +
            "AND (:accountNumber IS NULL OR a.accountNumber = :accountNumber)")
    Page<Account> findFilteredAccounts(
            @Param("email") String email,
            @Param("type") Account.AccountType type,
            @Param("status") Account.AccountStatus status,
            @Param("accountNumber") String accountNumber,
            Pageable pageable);

    @Query(value = """
    SELECT MAX(CAST(SUBSTRING(a.account_number, 9, 6) AS BIGINT))
    FROM accounts a
    WHERE SUBSTRING(a.account_number, 5, 4) = :year
    """, nativeQuery = true)
    Optional<Long> findMaxSequenceForYear(@Param("year") String year);

    boolean existsByIdAndUserAndStatus(Long id, Users user, Account.AccountStatus status);
}
