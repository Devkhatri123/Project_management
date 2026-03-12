package com.project_management.project_management.service;

import com.project_management.project_management.model.User;
import com.project_management.project_management.model.UserDetailsImpl;
import com.project_management.project_management.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserByIdAndSubscription(username); // id
        if(user == null){
            throw new UsernameNotFoundException("user does not exist");
        }
        log.info("user loaded from DB against extracted id from Jwt subject: {}",  username);
        return new UserDetailsImpl(user);
    }
}
