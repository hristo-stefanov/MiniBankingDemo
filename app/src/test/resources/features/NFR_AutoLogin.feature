Feature: Session persistence
  In order to save time and effort
  As a user
  I want not to be asked to login each time I use the app

  Background:
    Given I am registered for online banking

  Scenario: session persistence
    Given I was logged in before exiting the app
    When I launch the app to access Accounts
    Then I should be logged in