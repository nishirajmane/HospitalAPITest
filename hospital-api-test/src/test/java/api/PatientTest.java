package api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class PatientTest {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "http://localhost:3000";
    }

    @Test
    public void getAllPatients_shouldReturnAtLeast50Patients() {
        when()
            .get("/patients")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(50));  // Dynamic check
    }

    @Test
    public void getPatientById1_shouldReturnAaravSharma() {
        when()
            .get("/patients/1")
        .then()
            .statusCode(200)
            .body("name", equalTo("Aarav Sharma"))
            .body("age", equalTo(34))
            .body("disease", equalTo("Fever"));
    }

    @Test
    public void getPatientByInvalidId_shouldReturn404() {
        when()
            .get("/patients/9999")
        .then()
            .statusCode(404);
    }

    @Test
    public void createNewPatient_shouldReturn201() {
        given()
            .contentType(ContentType.JSON)
            .body("{ \"name\": \"Test Patient\", \"age\": 28, \"disease\": \"Flu\" }")
        .when()
            .post("/patients")
        .then()
            .statusCode(201)
            .body("name", equalTo("Test Patient"))
            .body("age", equalTo(28))
            .body("disease", equalTo("Flu"));
    }

    @Test
    public void updatePatientId10_shouldReturnUpdatedData() {
        given()
            .contentType(ContentType.JSON)
            .body("{ \"name\": \"Advik Kulkarni\", \"age\": 50, \"disease\": \"Recovered\" }")
        .when()
            .put("/patients/10")
        .then()
            .statusCode(200)
            .body("age", equalTo(50))
            .body("disease", equalTo("Recovered"));
    }

    @Test
    public void deletePatientDynamically_shouldReturn200or204() {
        Response response = given()
            .contentType(ContentType.JSON)
            .body("{ \"name\": \"Temp Patient\", \"age\": 33, \"disease\": \"Test\" }")
        .when()
            .post("/patients");

        String id = response.jsonPath().getString("id");

        when()
            .delete("/patients/" + id)
        .then()
            .statusCode(anyOf(is(200), is(204)));
    }

    @Test
    public void getPatients_shouldHaveRequiredFields() {
        when()
            .get("/patients")
        .then()
            .statusCode(200)
            .body("[0]", allOf(
                hasKey("id"),
                hasKey("name"),
                hasKey("age"),
                hasKey("disease")
            ));
    }

    @Test
    public void contentTypeShouldBeJson() {
        when()
            .get("/patients")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
    }

    @Test
    public void patientId1_ageShouldBeGreaterThan30() {
        when()
            .get("/patients/1")
        .then()
            .statusCode(200)
            .body("age", greaterThan(30));
    }

    @Test
    public void createPatientWithoutDisease_shouldSetNull() {
        given()
            .contentType(ContentType.JSON)
            .body("{ \"name\": \"No Disease\", \"age\": 40 }")
        .when()
            .post("/patients")
        .then()
            .statusCode(201)
            .body("disease", nullValue());
    }

    @Test
    public void verifyUniquePatientIds() {
        Response response = when().get("/patients");
        List<String> ids = response.jsonPath().getList("id", String.class);

        long unique = ids.stream().distinct().count();
        assert unique == ids.size();  // Ensures all patient IDs are unique
    }

    @Test
    public void checkPatientCountGreaterThan40() {
        when()
            .get("/patients")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(40));
    }

 
}
