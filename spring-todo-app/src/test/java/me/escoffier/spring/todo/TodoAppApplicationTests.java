package me.escoffier.spring.todo;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
class TodoAppApplicationTests {


	@Container
	static final PostgreSQLContainer<?> DATABASE = new PostgreSQLContainer<>("postgres:15-bullseye")
			.withDatabaseName("quarkus_test")
			.withUsername("quarkus_test")
			.withPassword("quarkus_test")
			.withExposedPorts(5432);

	@DynamicPropertySource
	static void neo4jProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", DATABASE::getJdbcUrl);
	}

	@Autowired
	private WebApplicationContext context;

	@LocalServerPort
	int randomServerPort;

	@BeforeEach
	public void setup() {
		RestAssuredMockMvc.webAppContextSetup(context);
		RestAssured.port = randomServerPort;
		RestAssured.requestSpecification = new RequestSpecBuilder()
				.setContentType(ContentType.JSON)
				.setAccept(ContentType.JSON)
				.build();
	}

	@Test
	public void test() throws Exception {
		Todo todo = new Todo();
		todo.setTitle("test");
		todo.setCompleted(false);

		given()
				.get("/api")
				.then()
				.statusCode(200)
				.assertThat()
				.body("size()", is(4));

		String id = given()
				.header("content-type", "application/json")
				.body(todo)
				.post("/api")
				.then()
				.statusCode(201)
				.extract().body().jsonPath().getString("id");

		given()
				.get("/api")
				.then()
				.statusCode(200)
				.assertThat()
				.body("size()", is(5));

		given()
				.get("/api/{id}", id)
				.then()
				.statusCode(200)
				.body("title", is("test"))
				.body("completed", is(false));

		todo.setTitle("fixed");
		todo.setCompleted(true);
		given()
				.header("content-type", "application/json")
				.body(todo)
				.patch("/api/{id}", id)
				.then()
				.statusCode(200);

		given()
				.get("/api/{id}", id)
				.then()
				.statusCode(200)
				.assertThat()
				.body("title", is("fixed"))
				.body("completed", is(true));

		given()
				.delete("/api/{id}", id)
				.then()
				.statusCode(204);

		given()
				.get("/api")
				.then()
				.statusCode(200)
				.assertThat()
				.body("size()", is(4));
	}

}
