package com.project_management.project_management.repository;

import com.project_management.project_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {

    @Query("select u from User u left join fetch u.subscription where u.id = :id")
    User getUserByIdAndSubscription(@Param("id") String id);

    User getUserById(String id);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

}
