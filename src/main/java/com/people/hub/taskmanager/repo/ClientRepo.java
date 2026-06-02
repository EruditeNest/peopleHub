package com.people.hub.taskmanager.repo;

import com.people.hub.taskmanager.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepo extends JpaRepository<Client, Long> {

    boolean existsByEmail(String email);

    boolean existsByContact(String contact);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByContactAndIdNot(String contact, Long id);
}
