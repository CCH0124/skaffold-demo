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

## PostgreSQL on K8s
1. NFS Server
從 WSL 安裝，請勿在 `/mnt/{c、d}` 進行掛載，因為硬碟格式非 ext4，每個節點安裝 `apt-get install nfs-common`
```bash=
docker run --name nfs-server -itd --privileged --restart unless-stopped -e READ_ONLY -e SHARED_DIRECTORY=/data -v $PWD/dynamic:/data -p 2049:2049 itsthenetwork/nfs-server-alpine:12
```

>第一個方法未成功

2. 建立一台 nfs-server 虛擬機
或是建立一台 nfs-server，按照此檔案的 [script](infra/nfs/Vagrantfile) 運行即可。並在每個節點安裝
```bash
sudo apt-get install nfs-common -y
```

最後 `apply` [檔案](/k8s/nfs-storage)，`api-server` 需添加 `- --feature-gates=RemoveSelfLink=false` 參數，才可運行，可參考此[討論](https://github.com/kubernetes-sigs/nfs-subdir-external-provisioner/issues/25)。

postgresql 使用 `kubegres` 方案，進行佈署，其相關資訊可參考此[鏈結](https://www.kubegres.io/doc/getting-started.html)，相關配置[yaml](/k8s/pg)

## Skaffold Architecture

## [Kube-context Activation](https://skaffold.dev/docs/environment/kube-context/) And [Profiles](https://skaffold.dev/docs/environment/profiles/)
當要與 Kubernetes 集群交互時，需要配置 `kube-context`。透過 `kube-context` 選擇 Kubernetes 集群、Kubernetes 用戶和預設 namespace。預設下，Skaffold 使用 `kube-config` 文件中的當前 `kube-context`。

本實驗環境是 WSL2 存取虛擬機中的集群。WSL2 安裝 `kubectl`，並依照以下方式做即可操控遠方群集，但通常不應該使用 kubernetes 的 admin 來做。

```bash
$ vagrant ssh master-skaffold
# 複製 .kube/config 至 WSL 的 .kube 下
$ KUBECONFIG=skaffold:~/.kube/skaffold kubectl config view # 這邊是貼至 .kube/skaffold 檔案中
apiVersion: v1
clusters:
- cluster:
    certificate-authority-data: DATA+OMITTED
    server: https://192.168.56.30:6443
  name: kubernetes
contexts:
- context:
    cluster: kubernetes
    user: kubernetes-admin
  name: kubernetes-admin@kubernetes
current-context: kubernetes-admin@kubernetes
kind: Config
preferences: {}
users:
- name: kubernetes-admin
  user:
    client-certificate-data: REDACTED
    client-key-data: REDACTED
$ KUBECONFIG=~/.kube/skaffold:~/.kube/config kubectl config view --flatten > config # 透過 --flatten 可顯示完整資訊，並將其合併至 config 檔案
$ kubectl config  get-contexts # 會看到我新增的群集
CURRENT   NAME                          CLUSTER      AUTHINFO           NAMESPACE
*         kubernetes-admin@kubernetes   kubernetes   kubernetes-admin
$ kubectl get node -o wide # WSL 就可以控制遠端(VM)的群集了
NAME              STATUS   ROLES                  AGE   VERSION   INTERNAL-IP     EXTERNAL-IP   OS-IMAGE             KERNEL-VERSION      CONTAINER-RUNTIME
master-skaffold   Ready    control-plane,master   14h   v1.23.0   192.168.56.30   <none>        Ubuntu 20.04.4 LTS   5.4.0-107-generic   cri-o://1.23.2
node1-skaffold    Ready    <none>                 14h   v1.23.0   192.168.56.31   <none>        Ubuntu 20.04.4 LTS   5.4.0-107-generic   cri-o://1.23.2
node2-skaffold    Ready    <none>                 14h   v1.23.0   192.168.56.32   <none>        Ubuntu 20.04.4 LTS   5.4.0-107-generic   cri-o://1.23.2
$ kubectl config  get-contexts -o name # 獲取當前 context 名字，這將運用在 skaffold 的 kubeContext 字段
kubernetes-admin@kubernetes
```

為了讓在本地開發的專案能夠佈署在遠端集群上我們定義了以下資訊，當然這段可以用 `skaffold dev --kube-context <myrepo>` 取代。下面配置目的是為了，設置部署到不同的 Kubernetes 上下文中。

```yaml
...
profiles:
- name: dev-cluster
  activation:
  - env: ENV=dev # 當此變數存在則觸發此 profile
  - kubeContext: kubernetes-admin@kubernetes
    command: dev
```

對於 Skaffold 配置檔來說，`kube-context` 有雙重作用
- Skaffold 配置檔可以由字段 `profiles.activation.kubeContext` 中 `kube-context` 自動存取
- Skaffold 配置檔可能由字段 `profiles.deploy.kubeContext` 中 `kube-context` 存取
  
>無法更改正在運行的 skaffold dev 的 kube-context。要更改，需要重新運行 skaffold dev

Skaffold 配置檔可為不同的 Kubernetes 上下文定義 build、test 和 deploy。不同的上下文通常不同環境，例如 prod 或 dev。為此須透過 `profiles` 字段進行定義，其有六個部分
- name: profile 名稱
- build
- test
- deploy
- patches
- activation

profiles 中定義的內容是可以覆蓋外部定義的內容。

當定義好之後我們可以如下運行
```bash
$ skaffold dev -p dev-cluster
```

接著透過起一個有 `curl` 指令的容器，針對該 API 服務的 `service` 資源進行存取，並得到以下結果
```bash
 curl http://tutorial-api-service.default.svc.cluster.local:8080/hello
Hello Skaffold! From host: tutorial-api-85446658dd-8pvzr/10.0.2.197
```