package com.pablodias.todolist.user;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @PostMapping("/")
    public void create(@RequestBody UserModel userModel) {
        System.out.println(userModel.getName());
    }
}
