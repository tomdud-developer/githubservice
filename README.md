
# GitHub Service

Service to retrieve information about not forked user repositories with branches names and last commits SHA

**Java 17 + Spring Boot 3 + Reactive WebFlux**
# Link to app
### I deployed app on AWS EC2
http://54.171.216.37:8080/swagger-ui/index.html

or try http://54.171.216.37:8080/api/v1/github/repositories/tomdud-developer
## Swagger UI
After deployed you can get access to Swagger-UI http://localhost:8080/swagger-ui.html

## API Reference

#### Get GitHub user repositories
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
```
#### Running, there is two modes available:
- without Personal GitHub token **60** requests/hour
- with Personal GitHub token **1000** requests/hour 
```bash
.\gradlew bootRun
.\gradlew bootRun --args='--token=GITHUB_TOKEN'
```
(**info**: If _username_ has 26 not forked repositories there will be 1 + 26 = 27 requests to GitHub api)


On linux before run gradlew:
```bash
chmod +x gradlew
```

## Running Tests

To run tests, run the following command after cloning app

```bash
  .\gradlew test -i
```
![tests.png](assets%2Ftests.png)
![coverage.png](assets%2Fcoverage.png)

## Screens
![goodResponse.png](assets%2FgoodResponse.png)
![wrongHeader.png](assets%2FwrongHeader.png)
![userNotFound.png](assets%2FuserNotFound.png)


## Authors

- [@tomadud-developer](https://www.github.com/tomadud-developer)

