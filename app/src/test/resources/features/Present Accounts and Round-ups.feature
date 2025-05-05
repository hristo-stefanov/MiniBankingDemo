Feature: Present Accounts and Roundups

  @manual
  Scenario: "Accounts and Round-ups" is presented
    Given I have the following accounts
      | number | currency | balance |
      | 1      | GBP      | 100.10  |
      | 2      | EUR      | 2000.20 |
    And the suggested round-up for each is
      | number | suggested round-up |
      | 1      | 1.23               |
      | 2      | 2.56               |
    When I'm presented with Accounts and Round-ups
    Then the following information should be included
      | number | currency | balance | suggested round-up |
      | 1      | GBP      | 100.10  | 1.23               |
      | 2      | EUR      | 2000.20 | 2.56               |

