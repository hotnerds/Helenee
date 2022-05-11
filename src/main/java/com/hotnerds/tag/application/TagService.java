package com.hotnerds.tag.application;

import com.hotnerds.tag.domain.Tag;
import com.hotnerds.tag.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TagService {

    private final TagRepository tagRepository;

    public Tag findOrCreateTag(String name) {
        return tagRepository.findByName(name)
                .orElseGet(() -> createTag(name));
    }

    private Tag createTag(String name) {
        Tag tag = new Tag(name);

        return tagRepository.save(tag);
    }
}
