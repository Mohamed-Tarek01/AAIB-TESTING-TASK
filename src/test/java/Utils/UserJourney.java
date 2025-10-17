package Utils;

import Pojo.User;
import TestData.Data;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;


@Epic("User Journey")
@Feature("User CRUD Operations")
public class UserJourney {

    private RequestSpecification requestSpec;
    private ResponseSpecification responseSpec;
    private User userRequestBody;
    private String userID;
    private String loggedInUserId;

    @BeforeClass
    public void setup() {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(Data.BaseUri)
                .addHeader("Content-Type", "application/json")
                .addHeader("x-api-key", Data.apiKey)
                .build();

        responseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .build();

        userRequestBody = new User();
        userRequestBody.setEmail(Data.fakeEmail);
        userRequestBody.setUsername(Data.fakeUserName);
        userRequestBody.setPassword(Data.fakePassword);
    }

    // ---------------------- CREATE USER ----------------------
    @Test(priority = 1)
    @Story("Create User")
    public void createUser() {
        Response response = given()
                .spec(requestSpec)
                .body(userRequestBody)
                .when()
                .post("/users/register")
                .then()
                .log().body()
                .statusCode(201)
                .extract().response();

        JsonPath js = response.jsonPath();
        userID = js.getString("id");
        Assert.assertNotNull(userID, "User ID should not be null after registration");
    }

    // ---------------------- LOGIN USER ----------------------
    @Test(priority = 2, dependsOnMethods = "createUser")
    @Story("User Login")
    public void loginUser() {
        Response response = given()
                .spec(requestSpec)
                .body(userRequestBody)
                .when()
                .post("/users/login")
                .then()
                .log().body()
                .statusCode(201)
                .extract().response();

        JsonPath js = response.jsonPath();
        loggedInUserId = js.getString("id");
    }

    // ---------------------- UPDATE USER ----------------------
    @Test(priority = 3, dependsOnMethods = "loginUser")
    @Story("Update User")
    public void updateUser() {
        // Update email
        userRequestBody.setEmail(Data.fakeEmail + "mm");

        given()
                .spec(requestSpec)
                .body(userRequestBody)
                .when()
                .patch("/users/" + userID)
                .then()
                .spec(responseSpec)
                .log().body()
                .statusCode(200);
    }

    // ---------------------- GET USER ----------------------
    @Test(priority = 4, dependsOnMethods = "updateUser")
    @Story("Get User Data")
    public void getUserAndVerify() {
        Response response = given()
                .spec(requestSpec)
                .log().uri()
                .when()
                .get("/users/" + userID)
                .then()
                .log().body()
                .statusCode(200)
                .extract().response();

        JsonPath js = response.jsonPath();
        String updatedEmail = js.getString("email");
        Assert.assertEquals(updatedEmail, Data.fakeEmail + "mm", "Email should be updated correctly");
    }

    // ---------------------- DELETE USER ----------------------
    @Test(priority = 5, dependsOnMethods = "getUserAndVerify")
    @Story("Delete User")
    public void deleteUser() {
        given()
                .spec(requestSpec)
                .log().uri()
                .when()
                .delete("/users/" + userID)
                .then()
                .statusCode(204);
    }

    // ---------------------- VERIFY DELETED USER ----------------------
    @Test(priority = 6, dependsOnMethods = "deleteUser")
    @Story("Verify User Deletion")
    public void verifyUserIsDeleted() {
        given()
                .spec(requestSpec)
                .log().uri()
                .when()
                .get("/users/" + userID)
                .then()
                .statusCode(404);
    }
}
