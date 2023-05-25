package ru.university.mikita.restjournal.login.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.university.mikita.restjournal.login.model.BusinessRoleType;
import ru.university.mikita.restjournal.login.model.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @NonNull
    Optional<Role> findByName(@NonNull BusinessRoleType name);
}
