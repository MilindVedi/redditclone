package com.project.reddit.controller;

import com.project.reddit.entities.Post;
import com.project.reddit.entities.Subreddit;
import com.project.reddit.entities.Users;
import com.project.reddit.repository.PostRepository;
import com.project.reddit.repository.SubredditRepository;
import com.project.reddit.repository.UserRepository;
import com.project.reddit.serviceImplementaion.storageService;
import com.project.reddit.serviceImplementaion.subrediditServiceim;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
public class SubredditController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubredditRepository repository;
    @Autowired
    private subrediditServiceim service;
    @Autowired
    private PostRepository postServiceimp;
    @Autowired
    storageService storage;

    @GetMapping("/community/{id}")
    public String community(@PathVariable("id") long id, Model model) {
        Date date = new Date();
        Subreddit subreddit = service.findById(id);

        model.addAttribute("subreddit", subreddit);

        model.addAttribute("subredditName", subreddit.getName());
        return "communityPage";
    }

    @GetMapping("/createSubreddit")
    public String createSubreddit(Model model) {
        model.addAttribute("subreddit", service.findAll());

        return "createSubreddit";
    }


    @PostMapping("/createCommunity")
    public String createCommunituy(@RequestParam("name") String name,
                                   @RequestParam("description") String description,
                                   @RequestParam(value = "image", required = false) MultipartFile file)
            throws IOException {
        System.out.println(file);
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        Subreddit subreddit = new Subreddit();
        if (service.findBysubredditName(name) != null) {
            System.out.println("alredy exist");
        } else {
            Users user = userRepository.findByUsername(userName);
            subreddit.setUser(user);
            subreddit.setName(name);
            subreddit.setCreatedDate(Instant.now());
            subreddit.setDescription(description);

            String fileName = storage.uploadFile(file);
            String s = "https://redditclonestore.s3.ap-south-1.amazonaws.com/" + fileName.substring(16, fileName.length());
            subreddit.setUrl(s);
            repository.save(subreddit);

        }
        return "redirect:/createpost";
    }


    @RequestMapping("/search/subreddits")
    public String searchSubreddits(@RequestParam("search") String search, Model model) {
        List<Subreddit> subreddits = service.findSubredditsBySearch(search.toLowerCase());
        model.addAttribute("search", search);
        for (Subreddit subreddit : subreddits) {
            System.out.println(subreddit.getName());
        }
        model.addAttribute("subreddits", subreddits);
        System.out.println(subreddits.size());

        return "searchSubreddit";
    }


}
