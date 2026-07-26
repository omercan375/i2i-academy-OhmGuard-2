package org.example.i2iacademyohmguard2.users;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;


public interface UsersRepo extends CrudRepository<UsersTable, UUID> {
    @Query("SELECT u FROM UsersTable u WHERE u.email=:email")
    UsersTable findByEmail(@Param("email") String email);
    @Query("SELECT u FROM UsersTable u WHERE u.id=:userId")
    UsersTable findByUserId(@Param("userId") UUID userId);
}

