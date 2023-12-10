package edu.example.repository;

import edu.example.model.Role;
import edu.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

    Optional<User> findByUsername(String username);

    List<User> findByRole(Role role);

    boolean existsByUsername(String username);
}
