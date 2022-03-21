package com.bookit.utilities;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class BookItApiUtils {

    public static String generateToken(String email, String password){
        Response response = given().queryParam("email", email)
                .and().queryParam("password", password)
                .when().get(ConfigurationReader.get("qa3api.uri") + "/sign");

        String token = response.path("accessToken");

        String finalToken = "Bearer "+ token;
        System.out.println("finalToken = " + finalToken);

        return finalToken;
    }

    public  static String[] getMyInfo(String email, String password){
        String[] myInfo = new String[3];
        for (int i = 0; i < myInfo.length; i++) {
            if(i==0){
                String url = ConfigurationReader.get("qa3api.uri")+"/api/teams/my";
                Response response = given().accept(ContentType.JSON)
                        .and()
                        .header("Authorization", generateToken(email,password))
                        .when()
                        .get(url);
                JsonPath jsonPath = response.jsonPath();
                myInfo[i]= jsonPath.getString("name");
            }
            if(i==1){
                String url = ConfigurationReader.get("qa3api.uri")+"/api/batches/my";
                Response response = given().accept(ContentType.JSON)
                        .and()
                        .header("Authorization", generateToken(email,password))
                        .when()
                        .get(url);
                JsonPath jsonPath = response.jsonPath();
                myInfo[i]= "#"+jsonPath.getString("number");
            }
            if(i==2){
                String url = ConfigurationReader.get("qa3api.uri")+"/api/campuses/my";
                Response response = given().accept(ContentType.JSON)
                        .and()
                        .header("Authorization", generateToken(email,password))
                        .when()
                        .get(url);
                JsonPath jsonPath = response.jsonPath();
                myInfo[i]= jsonPath.getString("location");
            }
        }

        return myInfo;
    }
}