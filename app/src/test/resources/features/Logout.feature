@steps:logout
Feature: Log out
  In order to protect my financial information and assets from unauthorised access
  and to be able log in with different credentials
  As a bank client
  I want to be able to log out

  Rule: User specific information should disappear when logging out
  and the user should be asked to log in

    Scenario: logging out
      Given I am seeing my account information
      When I log out
      Then my account information should be hidden
      And I should be asked to login