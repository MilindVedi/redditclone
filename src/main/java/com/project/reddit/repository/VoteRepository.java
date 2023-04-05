package com.project.reddit.repository;

import com.project.reddit.entities.Post;
import com.project.reddit.entities.Users;
import com.project.reddit.entities.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, Users currentUser);

//    @Query("Select u from Users u where u.post")
//    Vote getUserByVote(Long userId, long postId);


    //    Long findVoteIdByPostIdAndUserId(Long postId, Long userId);
    Vote findByPostAndUser(Post post, Users user);

}
