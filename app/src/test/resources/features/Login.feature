@debug
Feature: Log in
  In order to access online banking
  As a bank client
  I want to be able to log in

  Rule:
  The user must be logged in to access any online banking function

    Scenario Outline: prompt users to log in (instead of refusing access)
      Given I am not logged in
      When I try to access "<Service>"
      Then I should be asked to login
      Examples:
        | Service  |
        | Accounts |
# TODO
#        | Saving goals |

    Scenario: logging in provides access
      Given I'm asked to login to access Accounts
      When I provided correct credentials
      Then I should access the online banking



