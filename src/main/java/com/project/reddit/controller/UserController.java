package com.project.reddit.controller;

import com.project.reddit.dto.RegisterDto;
import com.project.reddit.entities.Users;
import com.project.reddit.Service.UserService;
import com.project.reddit.repository.UserRepository;
import com.project.reddit.serviceImplementaion.storageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostController postController;

    @Autowired
    UserService userService;
    @Autowired
    storageService storage;

    @GetMapping("/signup")
    public String signup(Model model) {
        RegisterDto user = new RegisterDto();
        model.addAttribute("user", user);
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("user") RegisterDto registerDto) {
        userService.signup(registerDto);

        return "signin";
    }

    @GetMapping("/signIn")
    public String signIn() {

        return "signin";
    }


    @RequestMapping("/checkUser")
    public String signIn(@RequestParam("email") String email, @RequestParam("password") String password) throws ChangeSetPersister.NotFoundException {

        Users user = userService.getUserByEmail(email);
        if (user.getPassword().equals(password)) {
            return "homePage";
        } else {
            System.out.println(user.getEmail());
            return "signIn";
        }

    }

    @GetMapping("accountVerification/{token}")
    public String verifyAccount(@PathVariable String token, Model model) {
        userService.verifyAccount(token);
        return postController.getBest(model);
    }

    @PostMapping("/saveProfilePicture/{userId}")
    public String saveProfilePicture(@PathVariable long userId,
                                     @RequestParam(value = "image", required = false) MultipartFile file)
            throws IOException {
        Users user = userRepository.getById(userId);
        String fileName = storage.uploadFile(file);
        System.out.println(fileName.substring(16, fileName.length()));
        String s = "https://redditclonestore.s3.ap-south-1.amazonaws.com/" + fileName.substring(16, fileName.length());
        user.setUrl(s);
        String userName = user.getUsername();
        userRepository.save(user);
        return "redirect:/userPosts/" + userName+"/"+0;
    }
}


