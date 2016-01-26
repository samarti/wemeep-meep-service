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
- Get close meeps with `GET`, secret or public, at

```
http://host:8080/meeps?radius=<some km dist>&lat=<lat>&longi=<long>&secret=<boolean>

If secret:true, you must provide an id also

Returns meeps close to the position on a radius
```

- Create a meep with `POST` at

```
http://host:8080/meeps

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
http://host:8080/meeps/{id}/comments?limit=<number>&offset=<number>
```

- Add a comment with `POST` at

```
http://host:8080/meeps/{id}/comments

Data: { body: { sender:<someUserId>, message:<someString>, ... } }
```
- Get a meep registrees with `GET` at

```
http://host:8080/meeps/{id}/registrees

Returns the meep registrees as an array
```
- Change registree situation with `PUT`

```
http://host:8080/meeps/{id}/registrees

Data: { body: { type:<"add" or "remove">, ids:[id: <registree id>, id: <registree id>, ...]} }
```
- Search for close public meeps containing the `query` on some hashtag with `GET` at

```
http://host:8080/searchmeep?radius=<some km dist>&lat=<lat>&longi=<long>&query=<string>

Returns meeps id's close to the position
```
- Add a view with `PUT` at
```
http://host:8080/meeps/{id}/newview
```
- Add a like with `PUT` at
```
http://host:8080/meeps/{id}/newlike
```
### Data model
#### Objects
##### Meep
|  Field      |  Values   |
| :---------- | :-------- |
| senderName      | String    |
| senderId    | String    |
| message     | String    |
| objectId          | String    |
| type        | String    |
| receipts    | Array<Comment>    |
| isPublic      | Boolean   |
| picture     | URL       |
| lat     | Long       |
| longi     | Long       |
| commentCounter | Integer |
| likeCounter | Integer |
| viewCounter | Integer |
| updatedAt   | Timestamp |
| createdAt   | Timestamp |
| registrees   | Array<User> |
| hashtags  | Array<String> |

##### Comment
|Field   |Values   |
|---|---|
| senderName  | String  |
| senderId   | String |
| message     | String    |
| updatedAt | Timestamp |
| createdAt | Timestamp |

## TODO
- Add authentication to the database
- Protect the API
- Check for SQLInjection
