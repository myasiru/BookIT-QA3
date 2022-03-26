package com.bookit.step_definitions;

import com.bookit.pages.SelfPage;
import com.bookit.utilities.BookItApiUtils;
import com.bookit.utilities.ConfigurationReader;
import com.bookit.utilities.DBUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.junit.Assert.*;


public class ApiStepDefs {
    public String token;
    Response response;
    Response responseBatch;
    Response responseCampus;
    Response responseTeam;
    String emailGlobal;
    int userID;
    String APIName, APITeam, APICampus, APIRole;
    String DBName, DBRole, DBTeam, DBCampus;
    String UIName, UIRole, UITeam, UICampus,UIBatch;
    double APIBatch;
    int DBBatch, finalUIBatch;

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
//-------------------------------------------------------

    @And("I get the current user's name and role informations from api")
    public void iGetTheCurrentUserSNameAndRoleInformationsFromApi() {
        response = given().accept(ContentType.JSON)
                .and()
                .header("Authorization", token)
                .when().get(ConfigurationReader.get("qa3api.uri") + "/api/users/me");

        APIName=response.path("firstName")+" "+response.path("lastName");
        System.out.println("APIName = " + APIName);
        APIRole=response.path("role");
        System.out.println("APIRole = " + APIRole);
        userID=response.path("id");
        System.out.println("userID = " + userID);
    }

    @When("I get the current user's team information from api")
    public void iGetTheCurrentUserSTeamInformationFromApi() {
        responseTeam = given().accept(ContentType.JSON).and()
                .and()
                .header("Authorization", token)
                .when().get(ConfigurationReader.get("qa3api.uri") + "/api/teams/my");

        APITeam="before";

        Map<String ,Object> teamsMap=responseTeam.body().as(Map.class);
        List<Map<String ,Object>>membersList= (List<Map<String, Object>>) teamsMap.get("members");
        for (Map<String, Object> eachMember : membersList) {

            double idFromTeam= (double) eachMember.get("id");

            if (idFromTeam==userID){
                APITeam= (String) teamsMap.get("name");
                break;
            }
        }
        System.out.println("APITeam = " + APITeam);
    }

    @And("I get the current user's batches information from api")
    public void iGetTheCurrentUserSBatchesInformationFromApi() {
        responseBatch = given().accept(ContentType.JSON).and()
                .and()
                .header("Authorization", token)
                .when().get(ConfigurationReader.get("qa3api.uri") + "/api/batches/my");

        APIBatch=0;

        Map<String ,Object> batchesMap=responseBatch.body().as(Map.class);
        List<Map<String ,Object>>teamsList= (List<Map<String, Object>>) batchesMap.get("teams");

        for (Map<String, Object> eachTeam : teamsList) {

           List<Map<String ,Object>> membersList= (List<Map<String, Object>>) eachTeam.get("members");

            for (int i = 0; i < membersList.size(); i++) {
                double idFromBatch= (double) membersList.get(i).get("id");
                if (idFromBatch==userID){
                APIBatch= (double) batchesMap.get("number");
                break;
                }
            }
        }
        System.out.println("APIBatch = " + APIBatch);
    }

    @And("I get the current user's campus information from api")
    public void iGetTheCurrentUserSCampusInformationFromApi() {
        responseCampus = given().accept(ContentType.JSON).and()
                .and()
                .header("Authorization", token)
                .when().get(ConfigurationReader.get("qa3api.uri") + "/api/campuses/my");

        APICampus=responseCampus.path("location");
        System.out.println("APICampus = " + APICampus);
    }

    @Then("all responses' status code should be {int}")
    public void allResponsesStatusCodeShouldBe(int expectedStatusCode) {

        assertEquals(response.statusCode(),expectedStatusCode);
        assertEquals(responseTeam.statusCode(),expectedStatusCode);
        assertEquals(responseBatch.statusCode(),expectedStatusCode);
        assertEquals(responseCampus.statusCode(),expectedStatusCode);
    }


    @Then("the all information about current user from api and database should match")
    public void theAllInformationAboutCurrentUserFromApiAndDatabaseShouldMatch() {
        //API/DB
        //get info from db

        String query="select firstname||' '||lastname as fullname, role, t.name, t.batch_number, c.location\n" +
                "from users u join team t\n" +
                "on u.team_id = t.id\n" +
                "join campus c\n" +
                "on t.campus_id=c.id\n" +
                "where\n" +
                "u.email='"+emailGlobal+"';";

        Map<String, Object> rowMapDB = DBUtils.getRowMap(query);

            DBName= (String) rowMapDB.get("fullname");
            System.out.println("DBName = " + DBName);
            DBRole= (String) rowMapDB.get("role");
            System.out.println("DBRole = " + DBRole);
            DBTeam= (String) rowMapDB.get("name");
            System.out.println("DBTeam = " + DBTeam);
            DBBatch= (int) rowMapDB.get("batch_number");
            System.out.println("DBBatch = " + DBBatch);
            DBCampus= (String) rowMapDB.get("location");
            System.out.println("DBCampus = " + DBCampus);

            //db vs api
            assertEquals(DBName,APIName);
            assertEquals(DBRole,APIRole);
            assertEquals(DBTeam,APITeam);
            assertEquals(DBBatch,(int)APIBatch);
            assertEquals(DBCampus,APICampus);
    }

    @And("I get the current user's all informations from UI")
    public void iGetTheCurrentUserSAllInformationsFromUI() {
        SelfPage selfPage=new SelfPage();
        UIName= selfPage.name.getText();
        System.out.println("UIName = " + UIName);

        UIRole=selfPage.role.getText();
        System.out.println("UIRole = " + UIRole);

        UITeam=selfPage.team.getText();
        System.out.println("UITeam = " + UITeam);

        UIBatch= selfPage.batch.getText();
        finalUIBatch=Integer.parseInt(UIBatch.substring(1));
        System.out.println("finalUIBatch = " + finalUIBatch);

        UICampus=selfPage.campus.getText();
        System.out.println("UICampus = " + UICampus);
    }

    @Then("UI,API and Database user all information must be match")
    public void uiAPIAndDatabaseUserAllInformationMustBeMatch() {
    //api vs uı
        assertEquals(APIName,UIName);
        assertEquals(APIRole,UIRole);
        assertEquals(APITeam,UITeam);
        assertEquals((int)APIBatch,finalUIBatch);
        assertEquals(APICampus,UICampus);

    //db vs ui
        String query="select firstname||' '||lastname as fullname, role, t.name, t.batch_number, c.location\n" +
                "from users u join team t\n" +
                "on u.team_id = t.id\n" +
                "join campus c\n" +
                "on t.campus_id=c.id\n" +
                "where\n" +
                "u.email='"+emailGlobal+"';";

        Map<String, Object> rowMapDB = DBUtils.getRowMap(query);
        DBName= (String) rowMapDB.get("fullname");
        DBRole= (String) rowMapDB.get("role");
        DBTeam= (String) rowMapDB.get("name");
        DBBatch= (int) rowMapDB.get("batch_number");
        DBCampus= (String) rowMapDB.get("location");

        assertEquals(DBName,UIName);
        assertEquals(DBRole,UIRole);
        assertEquals(DBTeam,UITeam);
        assertEquals(DBBatch,finalUIBatch);
        assertEquals(DBCampus,UICampus);
    }
}