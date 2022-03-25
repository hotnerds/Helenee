package com.hotnerds.tag.domain;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {
    public static final int MAX_TAG_NAME_LENGTH = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = MAX_TAG_NAME_LENGTH)
    private String name;

    public Tag(Long id, String name) {
        validateTagName(name);
        this.id = id;
        this.name = name;
    }

    public Tag(String name) {
        this(null, name);
    }

    public static void validateTagName(String name) {
        if(Objects.isNull(name)
                || name.isBlank()
                || name.length() > MAX_TAG_NAME_LENGTH) {
            throw new BusinessException(ErrorCode.TAG_NAME_NOT_VALID_EXCEPTION);
        }
    }
}
