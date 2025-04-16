@steps:saveRoundUps
Feature: Save Round-ups

  In order to achieve my savings goals
  As a user
  I want to be helped to save round-ups from my spending toward those goals

  Rule: Should see the round-up amount for a week for each account

#    @draft
#    Scenario: see account's round-up
#      Given I have opened the following accounts:
#        | number | currency | round-up |
#        | 123    | GBP      | 1.23     |
#        | 456    | EUR      | 4.56     |
#      When I view my accounts
#      Then I should see the round-up for each account

  Rule: Should be able to choose the account whose round-up to save
  Rule: Should be able to choose which savings goal to transfer the round-up to
  Rule: Should be able to create new savings goal

    Rule: TODO
  # TODO what about (The one where) the user is offered to save round-ups
    Scenario: The app helps users to save round-ups
      Given the calculated round-up for my account is 1.0
      When I view this account
      Then I should be offered to save "£1.00"
      And I should be able to transfer the offered amount to a savings goal