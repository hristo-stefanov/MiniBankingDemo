@steps:autologin
Feature: Session persistence

  In order to save time and effort
  As a user
  I want to be automatically logged in with my last credentials when I start the app

  Rule: Should auto-login the user when launching the app with the last used credentials

    Scenario: auto-logging in
      Given I was logged in before exiting the app
      When I launch the app to access Accounts
      Then I should be logged in

