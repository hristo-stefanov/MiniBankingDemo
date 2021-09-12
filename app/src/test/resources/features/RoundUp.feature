Feature: Round-up

  Scenario: Example1
    Given I have an account "12345678" in "GBP"
    And the following transactions in my account "12345678"
      |-4.35|
      |-5.20|
      |-0.87|
    When I access "12345678"
    Then I will be asked to save 1.58