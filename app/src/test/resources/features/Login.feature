Feature: Log in
  In order to access online banking
  As a bank client
  I want to be able to log in

  Rule:
    The user must be logged in to access any online banking functions

    Background:
      Given I am registered for online banking

    Scenario Outline: prompt users to log in (instead of refusing access)
      Given I am not logged in
      When I try to access "<Service>"
      Then I should be asked to login
      Examples:
    | Service  |
    | Accounts |
# TODO
#        | Saving goals |

    Scenario: logging on successfully
      Given I'm asked to login to access Accounts
      When I provide correct credentials
      And I should be given access to my accounts

@draft
      Scenario: logging with incorrect credentials
        Given I'm asked to login to access Accounts
        When I provided incorrect credentials
        Then I should be informed the credentials were incorrect
