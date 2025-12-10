package com.luqi.weblog.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "minio.endpoint=http://localhost:9000",
    "minio.accessKey=test",
    "minio.secretKey=test",
    "minio.bucketName=test"
})
class WeblogWebApplicationTests {

    @Test
    void contextLoads() {
    }

}