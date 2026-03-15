package com.project_management.project_management.service;

import com.project_management.project_management.model.Tag;
import com.project_management.project_management.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(final TagRepository tagRepository){
        this.tagRepository = tagRepository;
    }
    public List<Tag> getTagsByName(List<String> tags){
        List<Tag> task_Tags = new ArrayList<>();
        for (String tagName : tags){
            Tag tag = tagRepository.findByTagName(tagName);
            if(tag != null) {
                task_Tags.add(tag);
            }
        }
        return task_Tags;
    }
}
