Feature: Interact with ATM machine

  Scenario: As a customer I expect to be able to withdraw bills from the ATM machine
    Given I have 1000 Euros in my account
    When I withdraw 90 Euros
    Then I expect the following set of bills
      | 1 | FIFTY  |
      | 2 | TWENTY |

  Scenario: As a customer I expect a faulty atm to crash on the first attempt
    Given I have 1000 Euros in my account
    And the ATM is faulty
    When I withdraw 90 Euros with fees
    Then I expect the ATM to crash

  Scenario: As a customer I expect not to be charged a fee after the ATM crashed
    Given I have 1000 Euros in my account
    And the ATM crashed on my first attempt
    When I withdraw 90 Euros with fees
    Then I expect 910 euros in my account

  Scenario: As a customer I expect to get an error message when the amount I enter is not possible
    Given the customer is at the ATM
    When he fills in the amount of 15
    Then the customer needs to be shown a message 'The amount is not available.'