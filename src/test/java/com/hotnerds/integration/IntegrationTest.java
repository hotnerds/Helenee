package com.hotnerds.integration;

import com.hotnerds.utils.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public abstract class IntegrationTest {

    @Autowired
    protected DatabaseCleaner databaseCleaner;

    @BeforeEach
    void init() {
        databaseCleaner.clean();
    }
}
