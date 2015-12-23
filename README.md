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
- Get close meeps with `GET` at

```
http://host:8080/meeps?radius=<some km dist>&lat=<lat>&longi=<long>

Returns meeps close to the position on a radius
```

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
|  Field      |  Values   |
| :---------- | :-------- |
| sender      | String    |
| message     | String    |
| id          | String    |
| type        | String    |
| receipts    | Number    |
| isPublic      | Boolean   |
| picture     | URL       |
| lat     | Long       |
| longi     | Long       |
| updatedAt   | Timestamp |
| createdAt   | Timestamp |
| comments    | Array<Comment> |

##### Comment
|Field   |Values   |
|---|---|
| username  | String  |
| userId   | String |
| message     | String    |
| id  | String  |
| updatedAt | Timestamp |
| createdAt | Timestamp |

## TODO
- Add authentication to the database
- Protect the API
- Check for SQLInjection
