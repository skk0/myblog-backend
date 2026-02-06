package com.skk.blog.security;

import com.skk.blog.service.UserService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.skk.blog.entity.User user = userService.getByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .disabled(user.getStatus() == 0)
                .authorities("ROLE_ADMIN")
                .build();
    }
}
