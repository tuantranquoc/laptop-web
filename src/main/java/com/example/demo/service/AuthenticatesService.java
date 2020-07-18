package com.example.demo.service;


import com.example.demo.dto.UserDTOLogin;
import com.example.demo.model.Role;
import com.example.demo.model.UserRole;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserRoleRepository;
import com.example.demo.session.HandleSession;
import com.example.demo.session.Session;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Scanner;

import static com.example.demo.note.StaticVariables.*;


@Service
public final class AuthenticatesService {
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthenticatesService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, UserRoleRepository userRoleRepository, RoleRepository roleRepository, CustomerService customerService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;

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

    String hasRole(HttpSession session) {
        if (session.getAttribute("ROLE") == null) {
            System.out.println(1);
            return DENIED;
        }
        if (session.getAttribute("ROLE").equals(ROLE_ADMIN)) {
            return ROLE_ADMIN;
        }
        return ROLE_USER;
    }

    boolean login(UserDTOLogin userDTOLogin, HttpSession session) {
        String userName = userDTOLogin.getUserName();
        String password = userDTOLogin.getPassword();
        if (isValid(userName, password)) {
            String userRole = getRole(userRepository.findByUserName(userName).getUserId());
            session.setAttribute("NAME",userName);
            System.out.println(session.getAttribute("NAME").toString());
            if (userRole.equals(ROLE_ADMIN)) {
                session.setAttribute("ROLE", ROLE_ADMIN);


                //  session.setMaxInactiveInterval(3600);
                CURRENT_USER = userName;

                HandleSession handleSession = new HandleSession();
                handleSession.setSessionId(session.getId());
                handleSession.setUserName(userName);
                Session.sessionList.add(handleSession);
                return true;
            }
            session.setAttribute("ROLE", ROLE_USER);
            //  session.setMaxInactiveInterval(3600);
            CURRENT_USER = userName;

            HandleSession handleSession = new HandleSession();
            handleSession.setSessionId(session.getId());
            handleSession.setUserName(userName);
            Session.sessionList.add(handleSession);
            return true;
        }
        return false;
    }

    private String getRole(int userId) {
        // problem is here
        // what problem is>? it is the id of the user_role entity
        UserRole userRole = userRoleRepository.findFirstByUserId(userId);
        Role role = roleRepository.findByRoleId(userRole.getRoleId());
        return role.getRole();
    }

    private boolean hasNextRole() {
        return userRoleRepository.countAllByUserId(userRepository.findByUserName(CURRENT_USER).getUserId()) > 1;
    }

    // add new role when ROLE ADMIN or USER doesn't exist in DB
    private void addRole(String ROLE, int userId) {
        Role role = new Role();
        role.setRole(ROLE);
        roleRepository.save(role);

        UserRole userRole = new UserRole();
        userRole.setRoleId(role.getRoleId());
        userRole.setUserId(userId);
        userRoleRepository.save(userRole);
    }

    //boolean addRole: USER need to be accepted to add role ADMIN
    boolean addRole(HttpSession session) {
        if (hasNextRole()) {
            return false;
        }
        if (hasRole(session).equals(ROLE_ADMIN)) {
            addRole(ROLE_USER, userRepository.findByUserName(CURRENT_USER).getUserId());
            return true;
        }

        //NOTE: USER NEED ACCEPT TO ADD ROLE ADMIN
        Scanner inp = new Scanner(System.in);
        System.out.println("User: " + CURRENT_USER + " want to add ROLE_ADMIN!");
        System.out.println("Chose 1 to accept or chose random number to decline.");

        int chose = inp.nextInt();

        if (chose == 1) {
            addRole(ROLE_ADMIN, userRepository.findByUserName(CURRENT_USER).getUserId());
            return true;
        }
        return false;
    }

    boolean swapRole(HttpSession session) {
        if (hasRole(session).equals(DENIED)) {
            return false;
        }
        if (hasNextRole()) {
            if (hasRole(session).equals(ROLE_ADMIN)) {
                session.removeAttribute("ROLE");
                session.setAttribute("ROLE", ROLE_USER);
                return true;
            }
            session.removeAttribute("ROLE");
            session.setAttribute("ROLE", ROLE_ADMIN);
            return true;
        }
        return false;
    }



}
