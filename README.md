# Simple money transfer service

## Build
For ordinary build
```
./gradlew build
```

For building fat jar
```
./gradlew fatJar
```

## Running (far jar)
```
java -jar build/libs/fat-jar-1.1-SNAPSHOT.jar
```
Also it is possible to set custom port:
```
java -jar build/libs/fat-jar-1.1-SNAPSHOT.jar 9811
```

## Handlers
```
POST /accounts
GET /accounts/:id
POST /transfer
```


## Examples
```
curl -v --data-binary '{"accountId": 2, "amount": 10}' http://localhost:4567/accounts
curl -v http://localhost:4567/accounts/2
curl -v --data-binary '{"sourceId": 1, "destinationId": 2, "amount": 5}' http://localhost:4567/transfer
```
