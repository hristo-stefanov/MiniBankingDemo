Feature: Show Accounts and Roundups


  Rule: Suggested round-up amount is the sum of eligible transaction amouts

#  TODO defines "suggested roundup"
  # TODO (the one where) all transactions are eligible
    @steps:roundUpCalculation
    Scenario: Example
      Given the following transactions in an account
        | -4.35 |
        | -5.20 |
        | -0.87 |
      When the round up amount is calculated
      Then the result will be 1.58

#  TODO introduce "for a week"

  Rule: Eligible transactions for suggested roundup should be outbound, settled, and from
  an external source

    @manual
    Scenario Outline: Transaction eligibility for roundup suggestion
      Given I have a transaction from <source> with <status> and <direction>
      When the eligibility for roundup suggestion is calculated
      Then the result should be <eligibility>
      Examples:
        | source   | status  | direction | eligibility | notes                    |
        | external | settled | outbound  | eligible    | all criteria are covered |
        | external | settled | inbound   | ineligible  | not outbound             |

  Rule: TODO - separeate somehow from the rule above

    @manual
    Scenario: Presenting "Accounts and Round-ups"
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

