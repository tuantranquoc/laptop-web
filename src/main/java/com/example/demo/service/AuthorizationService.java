package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserDTOLogin;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserRoleRepository;
import com.example.demo.session.HandleSession;
import com.example.demo.session.Session;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class AuthorizationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    public AuthorizationService(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, UserRoleRepository userRoleRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
    }

    public boolean login(HttpSession session, UserDTOLogin userDTOLogin) {
        String userName = userDTOLogin.getUserName();
        String password = userDTOLogin.getPassword();
        if (isValid(userName, password)) {
            HandleSession handleSession = new HandleSession();
            handleSession.setSessionId(session.getId());
            handleSession.setUserName(userName);
            Session.sessionList.add(handleSession);
           return true;
        }
        return false;
    }

    private boolean isValid(String username, String rawPassword) {
        if (!isUserExist(username)) {
            return false;
        }
        String realPassword = userRepository.findByUserName(username).getPassword();
        return passwordEncoder.matches(rawPassword, realPassword);
    }

    private boolean isUserExist(String userName) {
        return userRepository.findByUserName(userName) != null;
    }

   public UserDTO findCurrentUser(HttpSession session) {

       System.out.println(Session.findById(session.getId()));
        User user = userRepository.findByUserName(Session.findById(session.getId()));
        return modelMapper.mapUser(user, getRole(user.getUserId()));
    }

    private String getRole(int userId) {
        // problem is here
        // what problem is>? it is the id of the user_role entity
        UserRole userRole = userRoleRepository.findFirstByUserId(userId);
        Role role = roleRepository.findByRoleId(userRole.getRoleId());
        return role.getRole();
    }
}
