package com.ss.scrumptious_auth.dao;

import com.ss.scrumptious_auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
//import java.util.UUID;
// removing uuid for now, may add back later

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String userName);
}
