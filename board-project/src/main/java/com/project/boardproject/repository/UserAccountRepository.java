package com.project.boardproject.repository;

import com.project.boardproject.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long>{
}
