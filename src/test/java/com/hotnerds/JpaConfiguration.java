package com.hotnerds;

import com.hotnerds.diet.domain.repository.DietRepository;
import com.hotnerds.food.domain.repository.FoodRepository;
import com.hotnerds.post.domain.repository.PostRepository;
import com.hotnerds.tag.domain.repository.TagRepository;
import com.hotnerds.user.domain.repository.UserRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackageClasses = {UserRepository.class, PostRepository.class,
        FoodRepository.class, TagRepository.class, DietRepository.class})
@EnableJpaAuditing
@TestConfiguration
public class JpaConfiguration {
}
