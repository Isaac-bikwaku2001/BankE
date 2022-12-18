package com.example.ebankbackend.repositories;

import com.example.ebankbackend.entities.AccountOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {
    Collection<AccountOperation> findAccountOperationById(String accountId);
    Page<AccountOperation> findAccountOperationById(String accountId, Pageable pageable);
    Collection<AccountOperation> findAccountOperationByType(String type);

}
