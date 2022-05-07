>Jib can create an image *without a Docker daemon*. This means that you don't have to install and configure Docker and create or maintain a Dockerfile.



[docker build and Jib build flow](https://www.linkedin.com/pulse/docker-image-build-vs-jib-eran-shaham/)

![](https://media-exp1.licdn.com/dms/image/C4D12AQHfTG7HCQdNjg/article-inline_image-shrink_1000_1488/0/1582204715176?e=1657152000&v=beta&t=75AtP83X2m0u13zSZJYuTOI-fSzm2oy1TRsETkr35wU) 

[jib 使用](https://github.com/GoogleContainerTools/jib/blob/master/jib-maven-plugin/README.md)

在 local 端可以如下執行
```bash
mvn compile jib:build -Djib.to.auth.username=${USERNAME} -Djib.to.auth.password=${PASSWORD} -Dimage=registry.hub.docker.com/cch0124/spring-tutorial-api
```

## Spring Best Practices
1. Wait for container lifecycle processes to finish

Kubernetes 再刪除一個應用程式時，過程會影響多個流程，像是 hook 關閉、註銷服務、從負載均衡器中刪除該實例等等。該過程是並行發生的，流量還是有機會路由到已開始進行關閉處理的 POD。
可以透過 `lifecycle.preStop` 配置睡眠時間，避免錯誤路由發生。一旦 `preStop` 完成，`SIGTERM` 將被發送到容器並開始正常關閉，允許任何剩餘的正在進行的請求完成。

>當 Kubernetes 向 POD 發送 `SIGTERM` 信號時，它會等待一個 termination grace period（默認為 30 秒）。如果容器時間後仍在運行，則會向它們發送 `SIGKILL` 強制刪除。如果 POD 的關閉時間超過 30 秒，這可能是增加了 `spring.lifecycle.timeout-per-shutdown-phase`，可透過在 YAML 中設置 `terminateGracePeriodSeconds` 來增加 termination grace period。

2. Add readiness and liveness probes

我們可藉由探針(probe)來知道容器內部狀態訊息。會根據定義在 yaml 中的配置，`kubelet` 會調用這些探測器並對結果做出反應。Spring boot 中 `actuator` 會從 `ApplicationAvailability` 收集`Liveness` 和 `Readiness` 訊息，分別對應這兩個指標 `LivenessStateHealthIndicator` 和 `ReadinessStateHealthIndicator`，指標會在端點 `/actuator/health` 上呈現分別是 `/actuator/health/liveness` 和 `/actuator/health/readiness`。

在 kubernetes 中可以用 `livenessProbe` 和 `readinessProbe` 來設計。

3. [Enable graceful shutdown](https://www.amitph.com/spring-boot-graceful-shutdown/)

正在執行的流程不受影響，可繼續完成已有請求的處理，但是停止接受新請求。

## API Spec
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