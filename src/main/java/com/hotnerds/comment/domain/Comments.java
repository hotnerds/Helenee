package com.hotnerds.comment.domain;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Comments {
    @OneToMany(
            mappedBy = "post",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE
    )
    private List<Comment> comments = new ArrayList<>();

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

    public Comment getOneComment(Long commentId) {
        return comments.stream()
                .filter(c -> c.getId().equals(commentId))
                .findAny()
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND_EXCEPTION));
    }

    public static Comments empty() {
        return new Comments(new ArrayList<>());
    }
}
