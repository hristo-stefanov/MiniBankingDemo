@steps:autologin
Feature: Session persistence
  In order to save time and effort
  As a user
  I want not to be asked to login each time I use the app

  Rule: Should auto-login the user when launching the app with the last used credentials

    Scenario: auto-logging in
      Given I was logged in before exiting the app
      When I launch the app to access Accounts
      Then I should be logged in

  Rule: Should allow the user to retry auto-logging if a network error occurs

    @manual
    Scenario: there is no internet connection
      Given I was logged in before exiting the app
      And there is no internet connection
      When I launch the app to access Accounts
      Then I should be prompted to retry

  Rule: Should allow the user to provide credential if a login service error occurs

    Scenario: the app keeps an invalid token
      Given I was logged in before exiting the app
      And the app keeps an invalid token
      When I launch the app to access Accounts
      Then I should be asked to login