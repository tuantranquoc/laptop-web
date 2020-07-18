package com.example.demo.service;

import com.example.demo.dto.AccessToken;
import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserDTOCreate;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserRoleRepository;
import com.example.demo.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.demo.note.StaticVariables.*;
@Service
public final class UserDetailsService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public UserDetailsService(UserRoleRepository userRoleRepository, UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    private String getRole(int userId) {
        // problem is here
        // what problem is>? it is the id of the user_role entity
        UserRole userRole = userRoleRepository.findFirstByUserId(userId);
        Role role = roleRepository.findByRoleId(userRole.getRoleId());
        return role.getRole();
    }

    boolean signUp(UserDTOCreate userDTOCreate) {
        if (userRepository.findByUserName(userDTOCreate.getUserName()) != null) {
            return false;
        }
        String encrypt = passwordEncoder.encode(userDTOCreate.getPassword());
        User user = new User();

        user.setUserName(userDTOCreate.getUserName());
        user.setPassword(encrypt);
        user.setMail(userDTOCreate.getMail());
        userRepository.save(user);

        Role role = new Role();
        role.setRole(userDTOCreate.getRole());
        roleRepository.save(role);

        UserRole userRole = new UserRole();
        userRole.setRoleId(role.getRoleId());
        userRole.setUserId(user.getUserId());
        userRoleRepository.save(userRole);
        return true;
    }

    List<UserDTO> findAllUser() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOS = new ArrayList<>();
        for (User user : users) {
            userDTOS.add(modelMapper.mapUser(user, getRole(user.getUserId())));
        }
        return userDTOS;
    }

    UserDTO findOneUser(int id) {
        if (!userRepository.findById(id).isPresent()) {
            return null;
        }
        User user = userRepository.findByUserId(id);
        return modelMapper.mapUser(user, getRole(id));
    }

    UserDTO findCurrentUser(HttpSession session) {

        User user = userRepository.findByUserName(Session.findById(session.getId()));
        return modelMapper.mapUser(user, getRole(user.getUserId()));
    }

    boolean delete(int userId) {
        if (userRepository.findByUserId(userId) == null) {
            return false;
        }
        List<UserRole> userRoles = userRoleRepository.findAllByUserId(userId);
        for (UserRole userRole : userRoles) {
            roleRepository.delete(roleRepository.getOne(userRole.getRoleId()));
            userRoleRepository.delete(userRoleRepository.getOne(userRole.getRoleId()));
        }


        userRepository.deleteById(userId);
        return true;
    }

    // method to swap ROLE
    // has another role => ADMIN OR USER is able to swap
    //This one for OAuth 2.0
    AccessToken accessToken(int clientId,HttpSession session) {
        AccessToken accessToken = new AccessToken();
        accessToken.setClientId(clientId);
        CODE = findCurrentUser(session).toString();
        accessToken.setAccessToken(passwordEncoder.encode(CODE));
        accessToken.setCalendar(Calendar.getInstance());
        return accessToken;
    }

    Object getUserResource(AccessToken accessToken, Calendar calendar,HttpSession session) {
        if (passwordEncoder.matches(CODE, accessToken.getAccessToken()) && (calendar.getTimeInMillis() - accessToken.getCalendar().getTimeInMillis() <= 30000)) {
            return findCurrentUser(session);
        }
        return TOKEN_EXPIRED;
    }

    void changeUserName(String userName) {
        User user = userRepository.findByUserName(CURRENT_USER);
        user.setUserName(userName);
        userRepository.save(user);
    }

}
