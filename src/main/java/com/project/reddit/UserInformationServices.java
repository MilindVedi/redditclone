package com.project.reddit;

import com.project.reddit.entities.Users;
import com.project.reddit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserInformationServices implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(email);


        if (user == null) {
            throw new UsernameNotFoundException("Invalid email or password.");
        } else if (user.getEmail().equals("bhara433@gmail.com")) {
            return User.withUsername(user.getUsername()).password(user.getPassword()).roles("ADMIN").build();
        } else {
            return User.withUsername(user.getUsername()).password(user.getPassword()).roles("USER").build();
        }


    }

}

