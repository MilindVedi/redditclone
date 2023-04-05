package com.project.reddit.serviceImplementaion;

import com.project.reddit.entities.Comment;
import com.project.reddit.repository.CommentRepository;
import com.project.reddit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class CommentServiceimp {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> getCommentsBySearch(String search) {
        return commentRepository.commentsSearchBytext(search);
    }


    public List<Comment> getCommentsBySearchAndSort(String search) {
        return commentRepository.commentSearchAndSort(search);
    }

    public List<Comment> getCommentsBySearchAndSortDesc(String search) {
        return commentRepository.commentSearchAndSortDesc(search);
    }

    public Comment getCommentById(long id) {
        return commentRepository.findById(id).get();
    }

    public void save(Comment tempComment) {
        commentRepository.save(tempComment);
    }

    public void deleteComment(long id) {
        commentRepository.deleteById(id);
    }


    public void saveReply(String text, Comment parentComment) {
        Comment replyComment = new Comment();
        replyComment.setText(text);
        replyComment.setPost(parentComment.getPost());
        replyComment.setCreatedDate(Instant.now());
        replyComment.setUser(userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()));

        List<Comment> comments = parentComment.getChildComments();
        comments.add(replyComment);

        parentComment.setChildComments(comments);
        replyComment.setParentComment(parentComment);
        commentRepository.save(parentComment);

    }
}
