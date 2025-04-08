@steps:login
Feature: Log in
  In order to access online banking
  As a user
  I want to be able to log in

  Rule:  Should prompt the user to login when trying to access online banking without being logged in
  (instead of refusing access)

    Scenario: prompt to log in
      Given I am not logged in
      When I try to access my bank accounts
      Then I should be asked to login

  Rule: Should provide the user with access to online blanking after logging in successfully

    Scenario: logging in successfully
      Given I'm asked to login to access my accounts
      When I provide correct credentials
      And I should be given access to my accounts

  Rule: Should inform the user when failing to log in

    @manual
    Scenario: logging with incorrect credentials
      Given I'm asked to login to access Accounts
      When I provided incorrect credentials
      Then I should be informed the credentials were incorrect

