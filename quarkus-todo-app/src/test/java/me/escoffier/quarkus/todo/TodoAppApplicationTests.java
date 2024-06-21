package me.escoffier.quarkus.todo;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class TodoAppApplicationTests {


    @Test
    public void test() throws Exception {
        Todo todo = new Todo();
        todo.title = "test";

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

        todo.title = "fixed";
        todo.completed = true;
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
