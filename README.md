Help-api is a service for serving static help pages for JetBrains products.
This project was implemented as a homework task for the final stage of the interview.

## Overview

### Stack
- [Ktor](https://github.com/ktorio/ktor) - for serving and routing HTTP requests
- [Hoplite](https://github.com/sksamuel/hoplite) - for loading configuration files
- [Exposed](https://github.com/JetBrains/Exposed) - as an ORM framework for working with sequel database
- [PostgreSQL](https://github.com/postgres/postgres) - as a completion config storage. 
  It stores actual versions and default pages for products
- [AWS S3 sdk](https://github.com/aws/aws-sdk-java) - 
for working with S3 compatible storage which stores static help pages

### How it works
All requests for the url starting with `/help` are routed to the same handler.
Then the path is parsed into components: product name, version and static page name using a regular expression.

The URL may not give complete information about the requested page,
so we need to support the concept of current versions of product and default pages.
E.g. for URL `/help/idea` we want to determine that product `"idea"` has a default version `"2022.1"` 
and chose default page to show - `"getting-started.html"`.
Current versions and default pages must be configurable.
In the current implementation, this information is stored in a relational database (PostgreSQL).

We also want to support the redirect functionality.
In the same example with `/help/idea` we want to redirect the user to a specific page `/help/idea/getting-started.html`. 
Thus, we want to supplement the URL with the static page name, while continuing to omit the version.

In the end, after all the routing, we know the specific page that the user requested.
S3 compatible storage is used to store static files.
You can use different S3 providers without code changes thanks to a unified S3 API. 
I used [Minio](https://docs.min.io/docs/minio-quickstart-guide.html) while testing the project locally.

### Schema

![schema](https://user-images.githubusercontent.com/12125190/169696007-563fba1c-f082-415a-a4c3-0ea1187263c8.png)

### How to add a new static page
First, you need to upload the file to S3 storage.
The folder structure is as follows. 
At the first level files are separated by product name.
On the second level - by version.
So if you want to upload a new file for Intellij IDEA version 2022.1 you need to make sure that folder `/idea/2022.1/` exists
and put file there.

![s3_tree](https://user-images.githubusercontent.com/12125190/169695547-3d65adec-6566-41ee-8767-2d1ea3b01759.png)

Secondly, if a new product is added then you need to go to Postgres 
and add routing rules: the current version for the product and default pages for each version of the product.


## How to run

### Build
```
./gradlew clean build
```
### Run
```
java -Dpostgres.user.password=<postgres_password> -Daws.accessKeyId=<aws_access_key_id> -Daws.secretAccessKey=<aws_secret_access_key> -jar build/libs/help-api-1.0-SNAPSHOT-standalone.jar
```

## Growth points
- Store page for 404 not found also in S3
- Introduce DI (e.g. [Kodein](https://github.com/Kodein-Framework/Kodein-DI))
- Reuse same S3 objects for different product versions in order to reduce space usage
- Use new AWS async SDK for Kotlin with Coroutines support when 1.0 is released - [link](https://blog.jetbrains.com/kotlin/2022/01/the-new-aws-sdk-for-kotlin-with-coroutines-support/)
