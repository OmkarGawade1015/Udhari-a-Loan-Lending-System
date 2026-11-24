# Postman setup

Import `Udhari.postman_collection.json` into Postman. The collection defaults to `http://localhost:8080` and includes the nine required API endpoints.

Set borrower and admin credentials only in a local Postman environment. Register the borrower first. Bootstrap the admin through the `UDHARI_ADMIN_EMAIL` and `UDHARI_ADMIN_PASSWORD` values in the ignored project `.env` file, then use Login for each role and copy each returned token into `borrowerToken` or `adminToken`. Copy `loanId` and `repaymentId` from responses before sending dependent requests. These variables are intentionally blank; the collection contains no default passwords or tokens.
