package com.medislot.backend.repository;
import com.medislot.backend.model.AdminAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface AdminAccountRepository extends JpaRepository<AdminAccount,Long> { Optional<AdminAccount> findByEmailIgnoreCase(String email); }
