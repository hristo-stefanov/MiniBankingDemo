Feature: Round-up

  Scenario: Example1
    Given the following transactions in my "Account 1"
      |-4.35|
      |-5.20|
      |-0.87|
    When I access "Account 1"
    Then I will be asked to save 1.58