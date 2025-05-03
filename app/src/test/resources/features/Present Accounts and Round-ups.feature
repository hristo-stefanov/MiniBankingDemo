Feature: Show Accounts and Roundups


  Rule: The suggested round-up amount is the sum of the difference between each transaction's
  rounded-up amount and its original value.
  FEEL expression:  sum(for t in transactions return ceiling(t) - t)

    @steps:roundUpCalculation
    Scenario: Suggested round-up is calculated
      Given the following eligible transactions, with these amounts:
        | 4.35   |
        | 5.20   |
        | 0.87   |
      When the suggested round-up amount is calculated
      Then the result should be 1.58

#  TODO introduce "for a week" and consider spending transaction vs eleigible transaction

  Rule: Eligible transactions for suggested roundup should be outbound, settled, and from
  an external source

    Scenario Outline: Transaction eligibility for round-up suggestion is calculated
      Given I have a transaction from <source> with <status> and <direction>
      When the eligibility for roundup suggestion is calculated
      Then the eligibility should be evaluated as <eligibility>
      Examples:
        | source   | status  | direction | eligibility | notes                    |
        | external | settled | outbound  | eligible    | all criteria are covered |
        | external | settled | inbound   | ineligible  | not outbound             |

  Rule: TODO - separeate somehow from the rule above

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

