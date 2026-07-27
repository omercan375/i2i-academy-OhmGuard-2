package org.example.i2iacademyohmguard2.users;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;


public interface UsersRepo extends CrudRepository<UsersTable, UUID> {

    @Query("SELECT u FROM UsersTable u WHERE u.email=:email")
    UsersTable findByEmail(@Param("email") String email);

    @Query("SELECT u FROM UsersTable u WHERE u.id=:userId")
    UsersTable findByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE UsersTable u SET u.email=:newEmail,u.version=u.version+1 where u.id=:userId and u.version=:version")
    int updateEmail(@Param("newEmail")String newEmail,@Param("userId") UUID token,@Param("version") int version);

    @Modifying
    @Query("UPDATE UsersTable u SET u.passwordHash=:newPassword,u.version=u.version+1 WHERE u.id=:userId and u.version=:version")
    int updatePassword(@Param("newPassword") String newPassword,@Param("userId") UUID token,@Param("version") int version);

    @Modifying
    @Query("UPDATE UsersTable u SET u.firstName=:newFirstName,u.lastName=:newLastName,u.version=u.version+1" +
            " WHERE u.id=:userId and u.version=:version")
    int updateName(@Param("newFirstName")  String newFirstName,@Param("newLastName") String newLastName,@Param("userId") UUID token,@Param("version") int version);


}

