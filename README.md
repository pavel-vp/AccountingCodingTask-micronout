# AccountingCodingTask-micronout
A simple implementation of accounting service using Micronout framework.
In this application, I used non-blocking approach with a task queue. Every POST called method will add a new task in queue and return its identifier. Later, we can call "status" RESP API method to know the status of the task.
Inside application there is a Thread, that manages the task queue and executing tasks one by one, in the order they have been added in the queue. Therefore, the process that changes the data in the system is only one - there is no blocking issue.

## API

Deposit some Amount to an Account -
POST /accounting/deposit?account=1&amount=100

Returns an operation identificator of LONG type.

Get status of operation -
GET /accounting/status?operationid=1

Returns a status of thin operation:
INPROGRESS - operation is still has not finished
SUCCESS - operation has finished successfully
ERROR - operation was failed (no money was transfered)

Transfer some Amount from AccountFrom to an AccountTo -
POST /accounting/transfer?accountfrom=1&accountto=2&amount=100

Returns an operation identificator of LONG type.

Get amount on the account -
GET /accounting/amount?account=1

Returns an amount on the account of BigDecimal type.


## Testing

Functional unit test in AccountingTest.java class, trying to imitate big amount of concurrent requests to server.
Finally, checks total amount of money on all accounts in Repo.

## Running

Start builds and run tests :
./gradlew build

Start web-server on http://localhost:8080/ :
./gradlew run

