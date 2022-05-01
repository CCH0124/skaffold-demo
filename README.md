>Jib can create an image *without a Docker daemon*. This means that you don't have to install and configure Docker and create or maintain a Dockerfile.

|Methods	|Urls	|Actions|
|---|---|---|
|POST	|/api/tutorials	|create new Tutorial|
|GET	|/api/tutorials	|retrieve all Tutorials|
|GET	|/api/tutorials/{id}	|retrieve a Tutorial by id|
|PUT	|/api/tutorials/{id}	|update a Tutorial by id|
|DELETE	|/api/tutorials/{id}	|delete a Tutorial by id|
|DELETE	|/api/tutorials	|delete all Tutorials|
|GET	|/api/tutorials/published|	find all published Tutorials|
|GET	|/api/tutorials?title=[keyword]|	find all Tutorials which title contains keyword|


1. /api/tutorials
```nginx
POST http://127.0.0.1:8080/api/tutorials
```
body
```json
{
    "title": "Spring boot",
    "description": "description"
}
```

DB

```sql
test=# select * from tutorials ;
                  id                  | description | published |    title
--------------------------------------+-------------+-----------+-------------
 36a49353-ee28-427c-8c44-052c63c9eb66 | description | f         | Spring boot
(1 row)
```

2. /api/tutorials/{id}

```
PUT http://127.0.0.1:8080/api/tutorials/36a49353-ee28-427c-8c44-052c63c9eb66
```

body
```json
{
    "title": "Spring boot",
    "description": "description",
    "published": true
}
```

response
```json
{
    "id": "36a49353-ee28-427c-8c44-052c63c9eb66",
    "title": "Spring boot",
    "description": "description",
    "published": true
}
```

DB
```sql
test=# select * from tutorials ;
                  id                  | description | published |    title
--------------------------------------+-------------+-----------+-------------
 36a49353-ee28-427c-8c44-052c63c9eb66 | description | t         | Spring boot
(1 row)
```

3. /api/tutorials
```
GET http://127.0.0.1:8080/api/tutorials
```

Response

```json
[
    {
        "id": "36a49353-ee28-427c-8c44-052c63c9eb66",
        "title": "Spring boot",
        "description": "description",
        "published": true
    },
    {
        "id": "b244b91a-57d5-49c5-8ad6-da59b0d483a3",
        "title": "Spring boot camel",
        "description": "camel",
        "published": false
    }
]
```

4. /api/tutorials/{id}

```
GET http://127.0.0.1:8080/api/tutorials/b244b91a-57d5-49c5-8ad6-da59b0d483a3
```

Response
```json
{
    "id": "b244b91a-57d5-49c5-8ad6-da59b0d483a3",
    "title": "Spring boot camel",
    "description": "camel",
    "published": false
}
```

5. /api/tutorials/published

```
GET /api/tutorials/published
```

Response
```json
[
    {
        "id": "36a49353-ee28-427c-8c44-052c63c9eb66",
        "title": "Spring boot",
        "description": "description",
        "published": true
    }
]
```

6. /api/tutorials?title=[keyword]

```
GET http://127.0.0.1:8080/api/tutorials?title=boot
```

Response

```json
[
    {
        "id": "36a49353-ee28-427c-8c44-052c63c9eb66",
        "title": "Spring boot",
        "description": "description",
        "published": true
    },
    {
        "id": "b244b91a-57d5-49c5-8ad6-da59b0d483a3",
        "title": "Spring boot camel",
        "description": "camel",
        "published": false
    }
]
```

7. /api/tutorials/{id}

```
DELETE http://127.0.0.1:8080/api/tutorials/36a49353-ee28-427c-8c44-052c63c9eb66
```

Respones

code 204 No content

DB

```sql
test=# select * from tutorials ;
                  id                  | description | published |       title
--------------------------------------+-------------+-----------+-------------------
 b244b91a-57d5-49c5-8ad6-da59b0d483a3 | camel       | f         | Spring boot camel
(1 row)
```

8. /api/tutorials

```
DELETE http://127.0.0.1:8080/api/tutorials
```

Respones

code 204 No content

DB

```sql
test=# select * from tutorials ;
 id | description | published | title
----+-------------+-----------+-------
(0 rows)
```