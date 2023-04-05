package com.project.reddit.controller;

import com.project.reddit.entities.Comment;
import com.project.reddit.entities.Post;
import com.project.reddit.entities.Subreddit;
import com.project.reddit.entities.Users;
import com.project.reddit.repository.PostRepository;
import com.project.reddit.repository.SubredditRepository;
import com.project.reddit.repository.UserRepository;
import com.project.reddit.serviceImplementaion.postServiceimp;
import com.project.reddit.serviceImplementaion.storageService;
import com.project.reddit.serviceImplementaion.subrediditServiceim;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;
import java.io.IOException;
import java.time.*;
import java.util.*;

@Controller
public class PostController {

    @Autowired
    private storageService service;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private subrediditServiceim subredditService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private postServiceimp postService;


    @Autowired
    private SubredditRepository subredditRepository;

    @GetMapping("/userPosts/{userName}/{dp}")
    public String community(@PathVariable("userName") String userName,
                            @PathVariable(value = "dp", required = false) Integer dp,
                            Model model) {
        Set<Post> allPosts = postService.getAllPosts();
        System.out.println("userName ->" + userName);
        ArrayList<Post> posts = new ArrayList<>();
        for (Post post : allPosts){
            if(post.getUser().getUsername().equals(userName)){
                posts.add(post);
                System.out.println(post.getPostName());
            }
        }
        model.addAttribute("posts",posts);
        Users user = userRepository.findByUsername(userName);
        System.out.println("-->" + dp + user.getUrl());
        model.addAttribute("user",user);
        model.addAttribute("dp", dp);




        return "userPosts";
    }



    @RequestMapping("/createpost")
    public String createPost(Model model) {
        List<Subreddit> subreddits = subredditRepository.findAll();
        model.addAttribute("subreddits", subreddits);

        return "createPost";

    }

    @PostMapping("/savePost")
    public String savePost(@RequestParam("subreddit") String subreddit,
                           @RequestParam("postName") String postName,
                           @RequestParam(value = "url", required = false) String url,
                           @RequestParam("description") String description,
                           @RequestParam(value = "image", required = false) MultipartFile file) throws IOException {
        Post post = new Post();


        post.setPostName(postName);
        post.setDescription(description);
        post.setCreatedDate(Instant.now());

        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        Users postUser = userRepository.findByUsername(user);
        post.setUser(postUser);
        if (post.getDescription().length() > 120) {
            post.setExcerpt(post.getDescription().substring(0, 120));
        } else {
            post.setExcerpt(post.getDescription());
        }
        Subreddit bysubredditName = subredditService.findBysubredditName(subreddit);
        post.setSubreddit(bysubredditName);
        if (!file.isEmpty()) {
            String fileName = service.uploadFile(file);
            System.out.println(fileName.substring(16, fileName.length()));
            String s = "https://redditclonestore.s3.ap-south-1.amazonaws.com/" + fileName.substring(16, fileName.length());
            post.setUrl(s);
        }
        postRepository.save(post);


        return "redirect:/";


    }


    @RequestMapping("/")
    public String getPostsAll(Model model) {
        Set<Post> posts = postService.getAllPosts();

        Date date = new Date();

        for (Post post : posts) {
            Date createdDate = Date.from(post.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant());
            long diff = date.getTime() - createdDate.getTime();
            long minutes = diff / (60 * 1000);
            String time = "";
            if (minutes >= 60L && minutes < (60 * 24)) {
                minutes = minutes / 60;
                time += minutes + " Hours Ago";
            } else if (minutes >= (60 * 24) && minutes < (60 * 24 * 30)) {
                minutes = minutes / (60 * 24);
                time += minutes + " Days Ago";
            } else {
                time += minutes + " Minutes Ago";
                model.addAttribute("minutes", minutes);
            }
            post.setTimeDifference(time);
            postRepository.save(post);
        }

        model.addAttribute("posts", posts);

        return "homepage";

    }

    @GetMapping("/editPost/{postId}")
    public String editPost(@PathVariable("postId") long id, Model model) {
        Post post = postService.getPostById(id);

        model.addAttribute("post", post);
        return "editPost";
    }

    @PostMapping("/saveEditedPost")
    public String saveEditedPost(@ModelAttribute("post") Post post,
                                 @RequestParam(value = "image", required = false) MultipartFile file) throws IOException {
        System.out.println(post.getPostId());
        String s = null;
        if (!file.isEmpty()) {
            String fileName = service.uploadFile(file);
            System.out.println(fileName.substring(16, fileName.length()));
            s = "https://redditclonestore.s3.ap-south-1.amazonaws.com/" + fileName.substring(16, fileName.length());
        }
        postService.saveEditedPost(post, s);

        return "redirect:/";
    }

    @GetMapping("/deletePost/{id}")
    public String deletePost(@PathVariable("id") long id) {
        postService.deletePostbyID(id);

        return "redirect:/";
    }

    @GetMapping("/search/posts")
    public String getSearch(@RequestParam("search") String search, Model model) {
        List<Post> posts = postService.getPostsBySearch(search.toLowerCase());
        model.addAttribute("search", search);
        model.addAttribute("posts", posts);
        return "searchpage";

    }

    @GetMapping("/search/sort/{asc}")
    public String getSearchbySort(@RequestParam("search") String search, @RequestParam("sort") String sort, Model model) {

        List<Post> posts = null;
        if (sort.equals("asc")) {
            posts = postService.getSearchbySort(search);
        } else if (sort.equals("desc")) {
            posts = postService.getSearchbySortDesc(search);
        }
        model.addAttribute("posts", posts);
        return "Homepage";

    }

    @RequestMapping("/viewpage/{postId}")
    public String view(@PathVariable("postId") long id, Model model,
                       @RequestParam(value = "commentId", required = false) String commentId) {
        Post post = postRepository.getById(id);

        List<Comment> allComments = post.getComment();
        long replyId = 0;
        if (commentId != null) {
            replyId = Long.parseLong(commentId);
        }

        System.out.println(replyId);

        List<Comment> topLevelComments = new ArrayList<>();
        for (Comment comment : allComments) {
            if (comment.getParentComment() == null) {
                topLevelComments.add(comment);
            }
        }

        Date date = new Date();
        Date createdDate = Date.from(post.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant());
        long diff = date.getTime() - createdDate.getTime();
        long minutes = diff / (60 * 1000);
        String time = "";
        if (minutes >= 60L && minutes < (60 * 24)) {
            minutes = minutes / 60;
            time += minutes + " Hours Ago";
        } else if (minutes >= (60 * 24) && minutes < (60 * 24 * 30)) {
            minutes = minutes / (60 * 24);
            time += minutes + " Days Ago";
        } else {
            time += minutes + " Minutes Ago";
            model.addAttribute("minutes", minutes);
        }
        post.setTimeDifference(time);
        postRepository.save(post);
        model.addAttribute("time", time);
        model.addAttribute("post", post);
        model.addAttribute("comments", topLevelComments);
        model.addAttribute("replyCommentId", replyId);

        return "viewpage";

    }

    @RequestMapping("/search/people")
    public String peopleSearch(@RequestParam("search") String search, Model model) {
        model.addAttribute("search", search);
        List<Users> allByUserName = userRepository.findAllByUserName(search.toLowerCase());
        model.addAttribute("people", allByUserName);

        return "peoplesearch";

    }

    @RequestMapping("/best")
    public String getBest(Model model){
        Set<Post> post = postRepository.getpostsAll();
        ArrayList posts = new ArrayList(post);
        Collections.sort(posts,(Post p1,Post p2)->{
            return p2.getVoteCount().compareTo(p1.getVoteCount());
        });

        String best="best";
        model.addAttribute("best",best);
        model.addAttribute("posts",posts);
        return "homepage";
    }


    @RequestMapping("/hot")
    public String getHot( Model model){
        Set<Post> post = postRepository.getpostsAll();
        ArrayList posts = new ArrayList(post);

        Collections.sort(posts, (Post p1, Post p2) -> {
            int s1 = p1.getComment().size();
            int s2 = p2.getComment().size();
            System.out.println(s1 + "-" + s2);
            return s2-s1;
        });
        String best="hot";
        model.addAttribute("best",best);
        model.addAttribute("posts",posts);
        return "homepage";
    }


    @RequestMapping("/new")
    public String getNew(Model model){
        Set<Post> posts = postRepository.getSearchbySort();
        String best="new";

        model.addAttribute("best",best);
        model.addAttribute("posts",posts);
        return "homepage";
    }

    @RequestMapping("/top")
    public String getTop(Model model){
        Set<Post> post = postRepository.getpostsAll();
        ArrayList posts = new ArrayList(post);

        Collections.sort(posts, (Post p1, Post p2) -> {
            int s1 = p1.getComment().size() + p1.getVoteCount();
            int s2 = p2.getComment().size() + p2.getVoteCount();

            return s2-s1;
        });

        String best="top";
        model.addAttribute("best",best);
        model.addAttribute("posts",posts);
        return "homepage";
    }

    @RequestMapping("/search/posts/sort")
    public String getHotBySearch(Model model, @RequestParam("search") String search, @RequestParam("sort-by") String sort) {
        List<Post> postsBySearch = postService.getPostsBySearch(search);
        if (sort.equals("hot")) {

            ArrayList post = new ArrayList();
            for (Post post1 : postsBySearch) {
                if (post1.getVoteCount() > 10) {
                    post.add(post1);
                }
            }
            model.addAttribute("posts", post);
        } else if (sort.equals("top")) {
            ArrayList posts = new ArrayList();
            for (Post post1 : postsBySearch) {

                if (post1.getComment().size() > 10) {
                    System.out.println(post1.getComment().size());
                    posts.add(post1);
                }

            }
            System.out.println("4");
            model.addAttribute("posts", posts);
        } else if (sort.equals("most-comments")) {
            ArrayList posts = new ArrayList();
            for (Post post1 : postsBySearch) {

                if (post1.getComment().size() > 10) {
                    posts.add(post1);
                }
            }
            model.addAttribute("posts", posts);

        }
        model.addAttribute("search", search);
        return "searchpage";
    }

    @RequestMapping("/search/posts/time")
    public String searchByTime(Model model, @RequestParam("search") String search, @RequestParam("time")
    String time) {
        List<Post> postsBySearch = postService.getPostsBySearch(search);
        if (time.equals("1")) {
            model.addAttribute("search", search);
            model.addAttribute("posts", postsBySearch);
        } else if (time.equals("2")) {
            model.addAttribute("search", search);
            model.addAttribute("posts", postsBySearch);
        } else if (time.equals("3")) {
            ArrayList post = new ArrayList();
            LocalDate currentDate = LocalDate.now();
            Month currentMonth = currentDate.getMonth();
            for (Post post1 : postsBySearch) {
                Instant instant = post1.getCreatedDate();
                ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
                Month month = zonedDateTime.getMonth();
                if (month.equals(currentMonth)) {
                    post.add(post1);
                }

            }
            model.addAttribute("search", search);
            model.addAttribute("posts", post);
        }


        return "searchpage";
    }

    @RequestMapping("/sharePost/{postId}")
    public RedirectView sharePost(@PathVariable("postId") Long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String link = request.getHeader("referer");
        link = link + "viewpage/" + id;
        redirectAttributes.addFlashAttribute("url", link);
        RedirectView redirectView = new RedirectView();
        redirectView.addStaticAttribute("postId", id);
        redirectView.setUrl("/viewpage/{postId}");
        return redirectView;
    }


}
