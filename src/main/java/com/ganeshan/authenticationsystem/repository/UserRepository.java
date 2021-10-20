package com.ganeshan.authenticationsystem.repository;

import com.ganeshan.authenticationsystem.model.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM USER WHERE EMAIL= :email")
    public UserEntity emailExists(String email);

    @Query(nativeQuery = true, value = "SELECT * FROM USER WHERE USERNAME =:username")
    public UserEntity usernameExists(String username);

    public Optional<UserEntity> findByUsername(String username);
}
