package com.project.reddit.serviceImplementaion;

import com.project.reddit.Service.subredditService;
import com.project.reddit.entities.Subreddit;
import com.project.reddit.repository.SubredditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class subrediditServiceim implements subredditService {


    @Autowired
    private SubredditRepository repository;


    public Subreddit findBysubredditName(String subreddit) {
        return repository.findByName(subreddit);
    }

    public List<Subreddit> findSubredditsBySearch(String search) {
        return repository.findBySearch(search);
    }

    public Subreddit findById(long id) {
        return repository.findById(id).get();
    }

    public List<Subreddit> findAll() {
        return repository.findAll();
    }
}
