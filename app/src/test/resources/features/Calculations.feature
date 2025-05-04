Feature: Calculations

  Rule: The suggested round-up amount is the sum of the difference between each transaction's
  rounded-up amount and its original value.
  FEEL expression:  sum(for t in transactions return ceiling(t) - t)

    @steps:roundUpCalculation
    Scenario: Suggested round-up is calculated
      Given the following eligible transactions, with these amounts:
        | 4.35 |
        | 5.20 |
        | 0.87 |
      When the suggested round-up amount is calculated
      Then the result should be 1.58

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
