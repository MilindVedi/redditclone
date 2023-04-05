package com.project.reddit.repository;

import com.project.reddit.entities.Comment;
import com.project.reddit.entities.Post;
import com.project.reddit.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(Users user);

    @Query("SELECT c FROM Comment c WHERE lower(c.text) LIKE CONCAT('%',:name,'%')")
    List<Comment> commentsSearchBytext(String name);

    @Query("SELECT c FROM Comment c WHERE lower(c.text) LIKE CONCAT('%',:search,'%') order by c.createdDate")
    List<Comment> commentSearchAndSort(String search);

    @Query("SELECT c FROM Comment c WHERE lower(c.text) LIKE CONCAT('%',:search,'%') order by c.createdDate desc")
    List<Comment> commentSearchAndSortDesc(String search);

}