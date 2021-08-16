package com.jetbrains.testcontainersdemo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
public class CustomerIntegrationTests {

    @Autowired
    private CustomerDao customerDao;

    // docker run -e MYSQL_USERNAME=... -e MYSQL_PASSWORD=.. mysql:8.0.26
    @Container
    private static MySQLContainer mySQLContainer = (MySQLContainer) new MySQLContainer("mysql:8.0.26")
            .withReuse(true);// utilser les container si il existe dèja

    /*
        @Container
        private static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:lastes");

        @Container
        private static GenericContainer genericContainer = new GenericContainer("myimage:mytag");
    */

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @Test
    void when_using_a_clean_db_this_should_be_empty() {
        List<Customer> customers = customerDao.findAll();
        assertThat(customers).hasSize(2);
    }
}
