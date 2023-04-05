package com.project.reddit.controller;

import com.project.reddit.entities.Post;
import com.project.reddit.entities.Users;
import com.project.reddit.entities.Vote;
import com.project.reddit.repository.PostRepository;
import com.project.reddit.repository.UserRepository;
import com.project.reddit.repository.VoteRepository;
import com.project.reddit.serviceImplementaion.voteServiceimp;
import org.springframework.beans.factory.annotation.Autowired;
import com.project.reddit.serviceImplementaion.postServiceimp;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VoteController {


    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private voteServiceimp voteServiceimp;
    @Autowired
    private postServiceimp postServiceimp;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/upvote/{postId}")
    public String upVote(@PathVariable("postId") Long postId, @RequestParam(value = "best", required = false) String best) {
        Post post = postRepository.getById(postId);
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        Users shahil = userRepository.findByUsername(user);
        Vote voteByUser = voteServiceimp.getVoteByPostIdAndUserId(post, shahil);

        if (voteByUser == null) {
            Vote vote = new Vote();
            vote.setUser(shahil);
            vote.setPost(post);
            vote.setVoteType(1);
            voteRepository.save(vote);
            Integer voteCount = post.getVoteCount();
            voteCount++;
            post.setVoteCount(voteCount);
            postRepository.save(post);
            if (best != null) {
                return "redirect:/" + best;

            } else {

                return "redirect:/";
            }
        } else if (voteByUser != null) {
            int voteType = voteByUser.getVoteType();
            if (voteType == -1) {
                Integer voteCount = post.getVoteCount();
                voteCount++;
                post.setVoteCount(voteCount);
                postRepository.save(post);
                voteByUser.setVoteType(1);
                voteRepository.save(voteByUser);
                if (best != null) {
                    return "redirect:/" + best;

                } else {

                    return "redirect:/";
                }
            }

        }


        return "redirect:/";
    }

    @GetMapping("/downvote/{postId}")
    public String downVote(@PathVariable("postId") long postId, @RequestParam(value = "best", required = false) String best) {
        Post post = postRepository.getById(postId);
        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        Users users = userRepository.findByUsername(user);
        Vote voteByUser = voteServiceimp.getVoteByPostIdAndUserId(post, users);
        System.out.println(voteByUser);
        if (voteByUser == null) {
            Vote vote = new Vote();
            vote.setUser(users);
            vote.setPost(post);
            vote.setVoteType(-1);
            voteRepository.save(vote);
            Integer voteCount = post.getVoteCount();
            voteCount--;
            post.setVoteCount(voteCount);
            postRepository.save(post);
            if (best != null) {
                return "redirect:/" + best;

            } else {

                return "redirect:/";
            }
        } else if (voteByUser != null) {
            int voteType = voteByUser.getVoteType();
            if (voteType == 1) {
                voteByUser.setVoteType(-1);
                Integer voteCount = post.getVoteCount();
                voteCount--;
                post.setVoteCount(voteCount);
                postRepository.save(post);
                if (best != null) {
                    return "redirect:/" + best;

                } else {

                    return "redirect:/";
                }
            }

        }
        return "redirect:/";
    }

    @GetMapping("/upvote/user/{postId}")
    public String upVoteByUser(@PathVariable("postId") Long postId, @RequestParam(value = "user") String userName) {
        Post post = postRepository.getById(postId);
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        Users shahil = userRepository.findByUsername(user);
        Vote voteByUser = voteServiceimp.getVoteByPostIdAndUserId(post, shahil);
        if (voteByUser == null) {
            Vote vote = new Vote();
            vote.setUser(shahil);
            vote.setPost(post);
            vote.setVoteType(1);
            voteRepository.save(vote);
            Integer voteCount = post.getVoteCount();
            voteCount++;
            post.setVoteCount(voteCount);
            postRepository.save(post);
            return "redirect:/userPosts/" + userName+"/"+0;
        } else {
            int voteType = voteByUser.getVoteType();
            if (voteType == -1) {
                Integer voteCount = post.getVoteCount();
                voteCount++;
                post.setVoteCount(voteCount);
                postRepository.save(post);
                voteByUser.setVoteType(1);
                voteRepository.save(voteByUser);
            }
            return "redirect:/userPosts/" + userName+"/"+0;

        }


    }

    @GetMapping("/downvote/user/{postId}")
    public String downVoteByUser(@PathVariable("postId") long postId, @RequestParam(value = "user") String userName) {
        Post post = postRepository.getById(postId);
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = userRepository.findByUsername(user);
        Vote voteByUser = voteServiceimp.getVoteByPostIdAndUserId(post, users);
        if (voteByUser == null) {
            Vote vote = new Vote();
            vote.setUser(users);
            vote.setPost(post);
            vote.setVoteType(-1);
            voteRepository.save(vote);
            Integer voteCount = post.getVoteCount();
            voteCount--;
            post.setVoteCount(voteCount);
            postRepository.save(post);


            return "redirect:/userPosts/" + userName+"/"+0;

        } else {
            int voteType = voteByUser.getVoteType();
            if (voteType == 1) {
                voteByUser.setVoteType(-1);
                Integer voteCount = post.getVoteCount();
                voteCount--;
                post.setVoteCount(voteCount);
                postRepository.save(post);
                voteByUser.setVoteType(1);
                voteRepository.save(voteByUser);


            }
            return "redirect:/userPosts/" + userName+"/"+0;
        }

    }
}
