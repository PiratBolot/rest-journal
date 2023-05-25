package ru.university.mikita.restjournal.login.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.university.mikita.restjournal.login.model.User;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @NonNull
    Optional<User> findByUsername(@NonNull String username);

    @NonNull
    Boolean existsByUsername(@NonNull String username);

    @NonNull
    Boolean existsByEmail(@NonNull String email);
}
