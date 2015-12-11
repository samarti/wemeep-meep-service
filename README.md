# WeMeep Meeps Service
### Description
This service handles all the meep info and their content. It's built over Docker containers and uses Java Spark Web framework and MongoDB for storage.
### Setup
#### Docker
Simply:
```
docker-compose up -d
```
#### Environment variables
Set:
```
```

#### Exposed ports
```
- API: 4567
- DB: 27017, 28017
- Genghis: 5000
```

### WebService
The web service exposes the following methods:

- Create a meep with `POST` at

```
http://host:8080/meeps/

Data: { body: { sender:<someUserId>, message:<someString>, ... } }
Returns the meep id
```
- Get a meep with `GET` at

```
http://host:8080/meeps/{id}

Returns the meep data
```
- Get a meep comments with `GET` at

```
http://host:8080/meeps/{id}/comments
```

- Add a comment with `POST` at

```
http://host:8080/meeps/{id}/comments

Data: { body: { sender:<someUserId>, message:<someString>, ... } }
```
### Data model
#### Objects
##### Meep
|Field   |Values   |
|---|---|
| sender  | String  |
| message  | String  |
| id  | Number  |
| type | String |
| receipts | Number|
| facebookId | Number |
| public | Boolean |
| isRoot | Boolean |
| picture | URL |
| updatedAt | Timestamp |
| createdAt | Timestamp |

##### User
|Field   |Values   |
|---|---|
| username  | String  |
| email  | String  |
| id  | Number  |
| password | String |
| twitterId | Number|
| facebookId | Number |
| gcmId | String |
| public | Boolean |
| picture | URL |
| updatedAt | Timestamp |
| createdAt | Timestamp |

## TODO
- Add authentication to the database
- Protect the API
- Check for SQLInjection
