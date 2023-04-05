package com.project.reddit.repository;

import com.project.reddit.entities.Subreddit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public
interface SubredditRepository extends JpaRepository<Subreddit, Long> {


    public Subreddit findByName(String name);


    @Query("SELECT s FROM Subreddit s WHERE lower(s.name) LIKE CONCAT('%',:search,'%')")
    public List<Subreddit> findBySearch(String search);

}
