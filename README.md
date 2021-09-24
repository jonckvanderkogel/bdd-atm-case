## ATM simulation as a foundation for BDD practice
Strangely enough this application seems to contain some errors...

- Start application from IDE or using `mvn spring-boot:run`
- Use tool like GraphQL playground to query application on `http://localhost:8080/graphql`

##Example queries

```
query retrieveBalance {
  retrieveBalance(accountNr:"123") {
    amount
    balanceDate
  }
}

mutation {
  withdrawBills(withdrawBillsInput: {amount:100, accountNr: "123"}) {
    bill
    nr
  }
}

mutation {
  withdrawBillsWithFees(withdrawBillsInput: {amount:100, accountNr: "123"}) {
    bill
    nr
  }
}
```