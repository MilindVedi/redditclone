package com.project.reddit.serviceImplementaion;

import com.project.reddit.Service.PostService;
import com.project.reddit.entities.Post;
import com.project.reddit.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class postServiceimp implements PostService {
    @Autowired
    private PostRepository postRepository;

    public void deletePostbyID(Long id) {
        postRepository.deleteById(id);
    }


    @Override
    public Set<Post> getAllPosts() {
        return postRepository.getpostsAll();
    }

    public List<Post> getPostsBySearch(String search) {
        return postRepository.getpostsBySearch(search);
    }

    public List<Post> getSearchbySort(String search) {
        return postRepository.getsearchbySort(search);
    }

    public List<Post> getSearchbySortDesc(String search) {
        return postRepository.getsearchbySortDesc(search);
    }

    public Post getPostById(long id) {
        return postRepository.findById(id).get();
    }

    public void saveEditedPost(Post post, String file) {
        Post tempPost = postRepository.findById(post.getPostId()).get();
        tempPost.setPostName(post.getPostName());
        tempPost.setDescription(post.getDescription());
        if (file != null) {
            tempPost.setUrl(file);
        }
        postRepository.save(tempPost);
    }


}
