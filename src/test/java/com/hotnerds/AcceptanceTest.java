package com.hotnerds;

import com.hotnerds.utils.DatabaseCleaner;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static io.restassured.RestAssured.UNDEFINED_PORT;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @LocalServerPort
    protected int port;

    @BeforeEach
    void init() throws Exception {
        if(RestAssured.port == UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleaner.afterPropertiesSet();
        }
        RestAssured.baseURI = "http://localhost";
        databaseCleaner.clean();
    }
}
