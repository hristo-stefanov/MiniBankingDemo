Feature: Session persistence
  In order to save time and effort
  As a user
  I want not to be asked to login each time I use the app

  Background:
    Given I am registered for online banking

  Scenario: session persistence
    Given I was logged before exiting the app
    When I launch the app
    Then I should be given access to the online banking

  @draft
  Scenario: ??? too trivial ???
    Given I was logged out before exitting the app
    When I launch the app
    Then I should be asked to log in
