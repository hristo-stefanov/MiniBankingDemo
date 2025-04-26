Feature: Startup

 Scenario: Auto-login
  Given my login credentials have been saved
  When I start the app
  Then I should be shown the Accounts and Roundups report

 Scenario: Login
  Given my login credentials have not been saved
  When I start the app
  Then I should be shown a login form

 Scenario: Post-login
  Given I am shown a login form
  When I submit my credentials
  Then my credentials should be saved
  And I should be shown the Accounts and Roundups report

#  TODO define the Accounts and Roundups report