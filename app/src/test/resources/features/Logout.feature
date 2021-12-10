Feature:
  In order to protect my financial information and assets
  As a bank client
  I want to be able to log out

  Background:
    Given I am registered for online banking

  Scenario: logging out
    Given I am logged in
    When I log out
    Then my account information should disappear
    And I should be asked to login