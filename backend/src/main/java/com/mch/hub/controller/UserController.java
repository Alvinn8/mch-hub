package com.mch.hub.controller;

import com.mch.hub.dto.RepositoryDto;
import com.mch.hub.dto.UserDto;
import com.mch.hub.service.RepositoryService;
import com.mch.hub.service.UserService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final RepositoryService repositoryService;

    public UserController(UserService userService, RepositoryService repositoryService) {
        this.userService = userService;
        this.repositoryService = repositoryService;
    }

    @GetMapping
    public List<UserDto> listUsers() {
        return userService.findAll();
    }

    @GetMapping("/{username}")
    public UserDto getUser(@PathVariable String username) {
        return userService.getByUsername(username);
    }

    @GetMapping("/{username}/repos")
    public List<RepositoryDto> listUserRepos(@PathVariable String username) {
        var user = userService.getEntityByUsername(username);
        return repositoryService.listByUser(user);
    }
}
