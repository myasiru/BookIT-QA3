package com.bookit.utilities;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class BookItApiUtils {

    public static String generateToke(String email, String password){

        Response response = given().and().queryParam("email", email)
                .and().queryParam("password", password)
                .when().get(ConfigurationReader.get("qa3api.uri") + "/sign");

        String token=response.path("accessToken");

        String finalToken="Bearer "+token;
        System.out.println("finalToken = " + finalToken);
        return finalToken;
    }
}
