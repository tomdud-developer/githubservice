
# GitHub Service

Service to retrieve information about user repositories with branches names and last commits SHA

# Link to app
### I deployed app on AWS EC2
http://52.51.48.40:8080/swagger-ui/index.html


or try http://52.51.48.40:8080/api/v1/github/repositories/tomdud-developer
## Swagger UI
After deployed you can get access to Swagger-UI http://localhost:8080/swagger-ui.html

## API Reference

#### Get item
Return information about user repositories with branches names and last commits SHA

```http
  GET api/v1/github/repositories/{username}
```

| Parameter  | Type     | Description                   |
|:-----------|:---------|:------------------------------|
| `username` | `string` | **Required**. GitHub username |




## Deployment

Download Java 17, properly set JAVA_HOME envarionment variable.

#### Clone and run
```bash
git clone https://github.com/tomdud-developer/githubservice
cd githubservice
.\gradlew bootRun
```
On linux before run gradlew
```bash
chmod +x gradlew
```

## Running Tests

To run tests, run the following command after cloning app

```bash
  .\gradlew test
```


## Authors

- [@tomadud-developer](https://www.github.com/tomadud-developer)

