# Bank Bridge Service:

There are two version of the API:

- `/v1/banks/all` - implementation is based on the static file, which is locally available
- `/v2/banks/all` - new version of the API, which will need to read the data from the remote servers

#### Application Enpoints :
| Description | Links |
| ------ | ------ |
| Get all Bank Details v1 | http://localhost:8080/v1/banks/all |
| Get all Bank Details v2 |  http://localhost:8080/v2/banks/all |


The response from the API looks like this:
```json
[
  {
    "name": "Credit Sweets",
    "id": "5678"
  },
  {
    "name": "Banco de espiritu santo",
    "id": "9870"
  },
  {
    "name": "Royal Bank of Boredom",
    "id": "1234"
  }
]
```

## Running the application

Maven is used to build project and manage dependencies. 

#### Installing the dependencies and build the project 

```sh
$ mvn clean install
```
Above command will also run the unit tests.

To skip tests use following command:

```sh
$ mvn clean install -DskipTests=true
```

To run the project use:

```sh
$ mvn exec:java
```

## Cucumber tests
To run cucumber integration tests please make sure that the Bank Bridge and the MockRemotes services are up and running.
Then use following command to run cucumber tests:

```sh
$ mvn -Dtest=CucumberRunner test
```

