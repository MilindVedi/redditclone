package com.project.reddit.repository;

import com.project.reddit.entities.Post;
import com.project.reddit.entities.Subreddit;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllBySubreddit(Subreddit subreddit);

    List<Post> findByUser(User user);

//    @Query("SELECT p FROM Post p order by p.createdDate desc limit 10")
//    public Set<Post> getpostsAll();
//
//    @Query(value = "SELECT * FROM post WHERE to_tsvector('english',post_name) @@ to_tsquery('english',:name)",nativeQuery = true)
//    List<Post> getpostsBySearch(String name);
//    @Query(value = "SELECT * FROM post WHERE to_tsvector('english',post_name) @@ to_tsquery('english',:search) order by createdDate",nativeQuery = true)
//    List<Post> getsearchbySort(String search);
//    @Query(value = "SELECT * FROM post WHERE to_tsvector('english',post_name) @@ to_tsquery('english',:search) order by createdDate desc",nativeQuery = true)
//    List<Post> getsearchbySortDesc(String search);

    @Query("SELECT p FROM Post p order by p.createdDate desc limit 10")
    public Set<Post> getpostsAll();

    @Query("SELECT p FROM Post p  WHERE lower(p.postName) LIKE CONCAT('%',:name,'%')")
    List<Post> getpostsBySearch(String name);

    @Query("SELECT p FROM Post p WHERE lower(p.postName) LIKE CONCAT('%',:search,'%') order by p.createdDate")
    List<Post> getsearchbySort(String search);

    @Query("SELECT p FROM Post p WHERE lower(p.postName) LIKE CONCAT('%',:search,'%') order by p.createdDate desc")
    List<Post> getsearchbySortDesc(String search);


    @Query("SELECT p FROM Post p  order by p.createdDate desc")
    Set<Post> getSearchbySort();
}