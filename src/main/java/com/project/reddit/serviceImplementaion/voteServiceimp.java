package com.project.reddit.serviceImplementaion;

import com.project.reddit.entities.Post;
import com.project.reddit.entities.Users;
import com.project.reddit.entities.Vote;
import com.project.reddit.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class voteServiceimp {

    @Autowired
    private VoteRepository voteRepository;

    public Vote getVoteByPostIdAndUserId(Post post, Users user) {
        return voteRepository.findByPostAndUser(post, user);

    }
}
