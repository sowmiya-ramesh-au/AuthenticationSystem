package com.ganeshan.authenticationsystem.repository;

import com.ganeshan.authenticationsystem.model.Token;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends CrudRepository<Token, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM TOKEN WHERE TOKEN= :token")
    Token findByToken(String token);

    @Query(nativeQuery = true, value = "DELETE FROM TOKEN WHERE TOKEN= :token")
    void removeByToken(String token);
}
