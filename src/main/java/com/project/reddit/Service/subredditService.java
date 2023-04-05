package com.project.reddit.Service;

import com.project.reddit.entities.Subreddit;

public interface subredditService {

    Subreddit findBysubredditName(String name);
}
