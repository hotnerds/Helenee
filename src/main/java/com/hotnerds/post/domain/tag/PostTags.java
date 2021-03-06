package com.hotnerds.post.domain.tag;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.post.domain.Post;
import com.hotnerds.tag.domain.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTags {
    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.PERSIST,
            orphanRemoval = true
    )
    private List<PostTag> postTags;

    public void addTag(Post post, Tag tag) {
        PostTag postTag = new PostTag(post, tag);
        if(postTags.contains(postTag)) {
            throw new BusinessException(ErrorCode.DUPLICATED_TAG_EXCEPTION);
        }

        postTags.add(postTag);
    }

    public void removeTag(Tag tag) {
        PostTag wantRemoveTag = postTags.stream()
                .filter(postTag -> postTag.isSameTag(tag))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.TAG_NOT_FOUND_EXCEPTION));

        postTags.remove(wantRemoveTag);
    }

    public List<String> getAllTagNames() {
        return postTags.stream()
                .map(PostTag::getTagName)
                .collect(toList());
    }

    public void clear() {
        postTags.clear();
    }

    public static PostTags empty() {
        return new PostTags(new ArrayList<>());
    }
}
