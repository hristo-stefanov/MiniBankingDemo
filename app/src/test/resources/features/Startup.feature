@steps:startup
Feature: Startup

 Scenario: Auto-login
  Given my login credentials have been saved
  When I launch the app
  Then I should be shown the Accounts and Roundups report

 Scenario: Login prompt
  Given my login credentials have not been saved
   When I launch the app
   Then I should be prompted to submit my login credentials

 @manual
 Scenario: Login credential submission
  Given I am shown a login form
  When I submit my credentials
  Then my credentials should be saved
  And I should be shown the Accounts and Roundups report

#  TODO define the Accounts and Roundups report