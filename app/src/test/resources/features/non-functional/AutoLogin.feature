Feature: Session persistence
  In order to save time and effort
  As a user
  I want not to be asked to login each time I use the app

  Rule: Should auto-login the user when launching the app with the last used credentials

  Background:
    Given I am registered for online banking

  Scenario: auto-logging in
    Given I was logged in before exiting the app
    When I launch the app to access Accounts
    Then I should be logged in