package com.hotnerds.unit.tag.domain.repository;

import com.hotnerds.tag.domain.Tag;
import com.hotnerds.tag.domain.repository.TagRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TagRepositoryTest {

    @Autowired
    TagRepository tagRepository;

    @DisplayName("태그를 저장할 수 있다.")
    @Test
    void 태그_저장_성공() {
        //given
        Tag tag = new Tag("name");

        //when
        Tag savedTag = tagRepository.save(tag);

        //then
        assertAll(
                () -> assertThat(savedTag.getId()).isNotNull(),
                () -> assertThat(savedTag.getName()).isEqualTo("name"));
    }

    @DisplayName("태그를 이름으로 조회할 수 있다.")
    @Test
    void 태그_이름으로_조회_성공() {
        //given
        Tag tag = new Tag("name");
        tagRepository.save(tag);

        //when
        Tag findTag = tagRepository.findByName(tag.getName()).get();

        //then
        assertAll(
                () -> assertThat(findTag.getId()).isNotNull(),
                () -> assertThat(findTag.getName()).isEqualTo(tag.getName()));
    }
}