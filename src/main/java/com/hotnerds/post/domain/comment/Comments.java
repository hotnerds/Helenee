package com.hotnerds.post.domain.comment;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import lombok.Getter;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Comments {
    @OneToMany(
            mappedBy = "post",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE
    )
    private List<Comment> comments = new ArrayList<>();

    public Comments(List<Comment> comments) {
        this.comments = comments;
    }

    public Comment add(Comment comment) {
        if (comments.contains(comment)) {
            throw new BusinessException(ErrorCode.COMMENT_DUPLICATED_EXCEPTION);
        }
        comments.add(comment);
        return comment;
    }

    public void remove(Long commentId) {
        Comment comment = getOneComment(commentId);
        comments.remove(comment);
    }

    public void update(Long commentId, String content) {
        Comment comment = getOneComment(commentId);
        comment.updateContent(content);
    }

    protected Comment getOneComment(Long commentId) {
        return comments.stream()
                .filter(c -> c.getId().equals(commentId))
                .findAny()
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
    }
}
