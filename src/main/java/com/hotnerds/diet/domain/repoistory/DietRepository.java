package com.hotnerds.diet.domain.repoistory;

import com.hotnerds.diet.domain.Diet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DietRepository extends JpaRepository<Diet, Long> {
}
