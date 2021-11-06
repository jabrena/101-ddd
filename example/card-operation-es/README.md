# card-operation-es

## How to run

When running locally, you have to have a mongodb started in a replica-set model.
This is necessary in order to support transactions.
This can be easily obtained for local dev mode using the docker-compose.yml found in src/docker folder.

### Start docker-compose

```
docker compose -f src/docker/docker-compose.yml up
```

### Start Spring specifying 'local' profile

Specify the following argument when running app: '-Dspring.profiles.active=local'


