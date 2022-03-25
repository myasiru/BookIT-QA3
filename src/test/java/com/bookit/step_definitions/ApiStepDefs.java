package com.bookit.step_definitions;

import com.bookit.pages.SelfPage;
import com.bookit.utilities.BookItApiUtils;
import com.bookit.utilities.ConfigurationReader;
import com.bookit.utilities.DBUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.junit.Assert.*;


public class ApiStepDefs {
    String token;
    Response response;
    String emailGlobal;

    @Given("I logged Bookit api using {string} and {string}")
    public void i_logged_Bookit_api_using_and(String email, String password) {

        token = BookItApiUtils.generateToke(email, password);
        emailGlobal=email;
    }

    @When("I get the current user information from api")
    public void i_get_the_current_user_information_from_api() {
        //send get request to retrieve current user info

        response = given().accept(ContentType.JSON)
                .and()
                .header("Authorization", token)
                .when().get(ConfigurationReader.get("qa3api.uri") + "/api/users/me");
    }

    @Then("status code should be {int}")
    public void status_code_should_be(int statusCode) {
        assertEquals(statusCode,response.statusCode());
    }

    @Then("the information about current user from api and database should match")
    public void theInformationAboutCurrentUserFromApiAndDatabaseShouldMatch() {
        //API/DB
        //get info from db
        String query="select id, firstname, lastname, role from users\n" +
                "where email='"+emailGlobal+"';";

        Map<String, Object> rowMap = DBUtils.getRowMap(query);
        System.out.println("rowMap = " + rowMap);

        long expectedID= (long) rowMap.get("id");
        String expectedFirstname= (String) rowMap.get("firstname");
        String expectedLastname= (String) rowMap.get("lastname");
        String expectedRole= (String) rowMap.get("role");

        //get info from api
        JsonPath jsonPath=response.jsonPath();

        long actualID=jsonPath.getLong("id");
        String actualFirstname=jsonPath.getString("firstName");
        String actualLastname=jsonPath.getString("lastName");
        String actualRole= jsonPath.getString("role");

        //compare
        assertEquals(expectedID,actualID);
        assertEquals(expectedFirstname,actualFirstname);
        assertEquals(expectedLastname,actualLastname);
        assertEquals(expectedRole,actualRole);
    }

    @Then("UI,API and Database user information must be match")
    public void uiAPIAndDatabaseUserInformationMustBeMatch() {
        //API/DB
        //get info from db
        String query="select id, firstname, lastname, role from users\n" +
                "where email='"+emailGlobal+"';";

        Map<String, Object> rowMap = DBUtils.getRowMap(query);
        System.out.println("rowMap = " + rowMap);

        long expectedID= (long) rowMap.get("id");
        String expectedFirstname= (String) rowMap.get("firstname");
        String expectedLastname= (String) rowMap.get("lastname");
        String expectedRole= (String) rowMap.get("role");

        //get info from api
        JsonPath jsonPath=response.jsonPath();

        long actualID=jsonPath.getLong("id");
        String actualFirstname=jsonPath.getString("firstName");
        String actualLastname=jsonPath.getString("lastName");
        String actualRole= jsonPath.getString("role");

        //compare API - DB
        assertEquals(expectedID,actualID);
        assertEquals(expectedFirstname,actualFirstname);
        assertEquals(expectedLastname,actualLastname);
        assertEquals(expectedRole,actualRole);

        //GET INFORMATION FROM UI
        SelfPage selfPage=new SelfPage();
        String actualUIFullName = selfPage.name.getText();
        String actualUIRole = selfPage.role.getText();

        //UI vs DB
        String expectedFullName=expectedFirstname+" "+expectedLastname;

        assertEquals(expectedFullName,actualUIFullName);
        assertEquals(expectedRole,actualUIRole);

        //UI vs API
        //Create a fullname for api
        String APIFullName=actualFirstname+" "+actualLastname;

        assertEquals(APIFullName,actualUIFullName);
        assertEquals(actualRole,actualUIRole);
    }
}