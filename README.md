# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

![Phase 2 Flow](phase-2.png)
## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Diagram
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygYKQH+iMyzKfMizfGpOx7EsVwqsGGqYDp8LiTU9QAEIhjABJElgWlJpUolpnhwwwHpYw5qoeaGYWxbQPUejriihJqGA5mNgxtmCm+XYwAgQnihimXCW58Wkmlu40vuDJMlOBlziVd5LsKMBihKboynKZbvEqN7VQ6XkdlQSIbu6W6WeqMDboY3WgfU7TxN4myStMl7QEgABeKAMZ5ybIKmMDpgAjP5gXBaFBZjEWJb1D4816otK27DAdFNqNgrDvyY4Tigz7xOel7Xs9i6VMuIZPpeWjyI93W2fUgl5f+CCAY8wHjdUun6URhljN8FFUfWRl3ehdneZt2EwLh+FoAFKPlp8RmwZeWPITjVymPRjHeH4-heCg6AxHEiQc1zUO+FgompUjpYNNIEb8RG7QRt0PRyaoCnDJjiHoKYnmVBDAlCYLtlffBqtoKSKX2X1uWCzlOunvlJJg1St4vTAjJgB9+uUYbVW8jVpsusa33aDA0AwD2faYEN1lpSBou1FNM1zRR12rXjmGE2Avl7WTB38kd0GnZFCqXfEie3fda3OmN9udfSRgoNwx6Xm7dOewuAq1fUHC10yhgUWD9n+lQEBIGXEn4-Aqc4X5melyzzH+Ci67+Ng4oavxaIwAA4kqGjC33Yvr9Lcv2Eqyu04bycfvD-qrzkm85nrKtIcbl-wr65e1NfYC32oOVol-qg22AQq5cnoO2rs7V2D90DN33PeOqDV1yQMKOHEakdEYj0mtNCAs1mSF2LufDaJQ07bScBncmQVs75lzhFUsF0E5QGWqtRKzNUGVy9o7Z2H8OpsMXD7dKcAIB9hQOABSAAeOA8R+Q8nKL3TWz96jIBvlvDIqgAI2WflHdBMAyFHxzAWBo4wdEoAAJLSALDtcIwRAggk2PEXUKA3ScjMiCZIoA1QOMgtTZYhiABySojIXBgJ0TSotBQ+WJpPbRW89EGKVCYsxFirHLBsXY9xVN0bOIQK41JaNvg+L8ejAJQSkozzZhwAA7G4JwKAnAxAjMEOAXEABs8A3obwrEUVOItNFSQ6IfY+hc6ZZjyXMYJI9tJyMiXMXxaTzJrEQaYZBajYQYTQQ5GAzl1wAPwQTQhvlSZkMOpQ8KZ1ZQuViu5JhTYTav07LUOAb0v4YkPHIFAX8AFAM7CAquo5wEN0QdAmq-04HigQafJCYdVQR2Aas-0scsHxwWvQm62yx67OIaQrOuYjknWoedXBSLGG42YdC1hLd6jPPRI8gFXUgX1EBm0uYXCW4aN6r7L+MATEyIeMs0s9yjwoGUQBDEhiTFPx5Sy5GYwRWmPqOYyxuN1o7NTCTfaXjYkypgHKwIRKHpMTZpYWumVZqxCQAkMABq+zwoAFKDzJuy-wLiQBqg6YQrpazmjMhkj0QxJ8DZISzNgTJBqoD8MylANY0rRmJlkTyl46wg2UFDdAPYAB1FgRiZY9EcvxBQcAADSuT1XxPlRcOZYK1aLJNrvJyLktmKtRVtdM+zMUhWxXnUs0UYDnPipc5K6jeH1AAFa2secO8UbyYaALtjIUBPymQQPLUbJlMC271RBX7P1FbIUoOhdWmAcLsH4oYcPRMBDG0kNVeQrFYUcUnNoYi49vbp2-VHE7JkjzpXUr+kKeo8CnbtRfSyvqhiPQQqsjuz5MLSyBjNPKMM4L61hIaOmYI3QW052OfnE0sGYC1nrDqk9g5SWlQVNgEGrylQYjQCgTYP1Z2wNqMyMjLyGViEA6svqA8h5cq1lxvt4qoMvCjSss9RMVVT2KZ4VmAQvDAHlCas1sn5SIGDLAYA2BA2EDyAUEaO8bnuollLGWctjDnxjV+eoKm8B6zFV+N1fUQDcDwBiD5rKK4zu+eSpzUAXPLsBT+mAKbiDdz7K1VQqkE1QHCxu92SFbT0Y477PjPGJnJarfp3SwnR4+XEwFRmzMgA

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
