Feature: Present Accounts and Roundups

  @manual
  Scenario: "Accounts and Round-ups" is presented
    Given I have the following accounts
      | account num | currency | balance |
      | 1           | GBP      | 100     |
      | 2           | EUR      | 2000    |
    And the suggested round-up for each is
      | account num | suggested round-up |
      | 1           | 1.23               |
      | 2           | 2.56               |
    When I'm presented with "Accounts and Round-ups"
    Then the following information should be included
      | account num | currency | balance | suggested round-up |
      | 1           | GBP      | 100     | 1.23               |
      | 2           | EUR      | 2000    | 2.56               |

