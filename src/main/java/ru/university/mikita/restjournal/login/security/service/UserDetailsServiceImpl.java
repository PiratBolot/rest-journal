package ru.university.mikita.restjournal.login.security.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.university.mikita.restjournal.login.model.User;
import ru.university.mikita.restjournal.login.repository.UserRepository;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user =
                userRepository.findByUsername(username)
                              .orElseThrow(() ->
                                      new UsernameNotFoundException(
                                              String.format("User not Found with username: %s", username)));
        return UserDetailsImpl.build(user);
    }

}