package com.project.reddit.controller;

import com.project.reddit.entities.Comment;
import com.project.reddit.entities.Post;
import com.project.reddit.entities.Users;
import com.project.reddit.repository.PostRepository;
import com.project.reddit.repository.UserRepository;
import com.project.reddit.serviceImplementaion.CommentServiceimp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
public class CommentController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentServiceimp commentService;

    @RequestMapping("/search/comments")
    public String getCommentsBySearch(@RequestParam("search") String search, Model model) {
        List<Comment> comments = commentService.getCommentsBySearch(search.toLowerCase());
        model.addAttribute("comments", comments);
        System.out.println(comments.size());
        model.addAttribute("search", search);
        return "searchComments";

    }

    @RequestMapping("/search/comments/sort")
    public String getCommentsBySearchAndSort(@RequestParam("search") String search, @RequestParam("sort") String sort, Model model) {
        List<Comment> comments = null;
        if (sort.equals("asc")) {
            comments = commentService.getCommentsBySearchAndSort(search);
        } else if (sort.equals("desc")) {
            comments = commentService.getCommentsBySearchAndSortDesc(search);
        }
        model.addAttribute("comments", comments);

        return "Homepage";


    }

    @RequestMapping("/comment/{postId}")
    public String saveComment(@PathVariable("postId") long postId, @RequestParam("comment") String comment, Model model) {
        Comment commentObj = new Comment();
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByUsername(userName);
        commentObj.setUser(user);
        commentObj.setText(comment);
        commentObj.setCreatedDate(Instant.now());
        Post post = postRepository.getById(postId);
        commentObj.setPost(post);
        List<Comment> comment1 = post.getComment();
        comment1.add(commentObj);
        post.setComment(comment1);
        postRepository.save(post);

//        entities.addAttribute("comments",commentObj);
        model.addAttribute("post", post);
        List<Comment> allComments = post.getComment();
        List<Comment> topLevelComments = new ArrayList<>();

        for (Comment tempComment : allComments) {
            if (tempComment.getParentComment() == null) {
                topLevelComments.add(tempComment);
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

        model.addAttribute("time", time);
        model.addAttribute("comments", topLevelComments);

        return "viewpage";
    }

    @RequestMapping("/comment/delete/{commentId}")
    public String deleteComment(@PathVariable("commentId") long id, RedirectAttributes redirectAttributes) {
        commentService.deleteComment(id);
        return "redirect:/";
    }

    @RequestMapping("/comment/edit/{commentId}")
    public String editComment(@PathVariable("commentId") long id, Model model) {
        Comment comment = commentService.getCommentById(id);
        model.addAttribute("comment", comment);

        return "editComment";

    }

    @PostMapping("/comment/saveEditedComment")
    public String saveEditedComment(@ModelAttribute Comment comment) {
        System.out.println(comment.getId() + "------>" + comment.getText());

        Comment tempComment = commentService.getCommentById(comment.getId());
        tempComment.setText(comment.getText());
        commentService.save(tempComment);
        Long postId = tempComment.getPost().getPostId();
        System.out.println(postId);
        return "redirect:/viewpage/" + postId;
    }

    @GetMapping("/comment/reply/{commentId}")
    public String replyComment(@PathVariable("commentId") long commentId) {
        Comment comment = commentService.getCommentById(commentId);
        Long postId = comment.getPost().getPostId();
        System.out.println(commentId);
        String commentIdString = String.valueOf(commentId);
        System.out.println(commentIdString);
        return "redirect:/viewpage/" + postId + "?commentId=" + commentIdString;
    }

    @PostMapping("/comment/saveRepliedComment/{commentId}")
    public String saveRepliedComment(@PathVariable("commentId") long commentId,
                                     @RequestParam("text") String text) {
        Comment comment = commentService.getCommentById(commentId);
        commentService.saveReply(text, comment);

        long postId = comment.getPost().getPostId();

        return "redirect:/viewpage/" + postId;
    }
}




