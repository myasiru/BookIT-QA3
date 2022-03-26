#get name,role,team,batch,campus information from ui,database and api, compare them
# you might get in one shot from ui and database, but might need multiple api requests to get those information

Feature: User's all information verification with 3 point
  Scenario: verify all information about logged user
    Given I logged Bookit api using "sbirdbj@fc2.com" and "asenorval"
    When I get the current user's name and role informations from api
    And I get the current user's team information from api
    And I get the current user's batches information from api
    And I get the current user's campus information from api
    Then all responses' status code should be 200

   @db
  Scenario: verify all information about logged user from api and database
    Given I logged Bookit api using "sbirdbj@fc2.com" and "asenorval"
    When I get the current user's name and role informations from api
    And I get the current user's team information from api
    And I get the current user's batches information from api
    And I get the current user's campus information from api
    Then the all information about current user from api and database should match

  @db @wip
  Scenario: three point verification (UI,DATABASE,API)
    Given user logs in using "sbirdbj@fc2.com" "asenorval"
    When user is on the my self page
    And I get the current user's all informations from UI
    And I logged Bookit api using "sbirdbj@fc2.com" and "asenorval"
    When I get the current user's name and role informations from api
    And I get the current user's team information from api
    And I get the current user's batches information from api
    And I get the current user's campus information from api
    Then UI,API and Database user all information must be match