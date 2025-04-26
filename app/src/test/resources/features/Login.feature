@steps:login
Feature: Log in

  In order to use app features that require access to my banking data
  As a user
  I want to be able to log in

  Scenario: the app is launched without having saved credentials
    Given I am not logged in
    When I try to access my bank accounts
    Then I should be asked to login

  @steps:autologin
  Scenario: the app is launhed when the saved credentials are valid
    Given I was logged in before exiting the app
    When I launch the app to access Accounts
    Then I should be logged in

  @manual
  Scenario: the app is launched when the saved credentials are invalid
    Given I was logged in before exiting the app
    And the app keeps an invalid token
    When I launch the app to access Accounts
    Then I should be asked to login

  Scenario: the user provides valid credentials
    Given I'm asked to login to access my accounts
    When I provide correct credentials
    And I should be given access to my accounts

  @manual
  Scenario: the user provides invalid credentials
    Given I'm asked to login to access Accounts
    When I provided incorrect credentials
    Then I should be informed the credentials were incorrect
