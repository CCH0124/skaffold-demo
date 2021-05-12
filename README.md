
在初始化(skaffold init)時，需先準備好 Dockerfile、K8s 佈署資源。然後初始化後會生成 `skaffold.yaml` 檔案。下面是修正過，有新增一些關鍵字

```yaml=
 cat skaffold.yaml
apiVersion: skaffold/v2beta15
kind: Config
metadata:
  name: hello-api
build:
  tagPolicy:
    gitCommit:
      variant: AbbrevCommitSha
  artifacts:
  - image: cch0124/hello-api
    context: .
    docker:
      dockerfile: Dockerfile
  local:
    push: true
deploy:
  kubectl:
    manifests:
    - k8s/deploy.yaml
    - k8s/service.yaml
    defaultNamespace: default # 預設為 default
```


```shell=
skaffold build # build、tag、push
```

但是 push 必須登入 docker hub，目前看來預設是這樣，除非有自架的倉庫，需要可參考此[鏈結](https://skaffold.dev/docs/environment/image-registries/)。

鍵置完後要佈署(skaffold deploy)，發現會有 tag 問題，因為 tag 是隨機這樣導致佈署都要變更 image。我們使用 `--file-output` 方式記錄建置的結果

```shell=
export STATE=$(git rev-list -1 HEAD --abbrev-commit)
skaffold build --file-output build-$STATE.json

# Or

skaffold build -q | skaffold deploy --build-artifacts -
```
該輸出的 json 檔會有 `imageName` 和 `Tag` 資訊，隨後我們將它餵入 `skaffold deploy`，即可佈署

```shell=
skaffold deploy -a build-$STATE.json
```

這樣我們減少對 Tag 的控管。佈署後其建立以下結果

```shell=
$ kubectl get pods
NAME                             READY   STATUS    RESTARTS   AGE
hello-rest-6c467745b6-dw4lk   1/1     Running   0          82s
hello-rest-6c467745b6-mtgnr   1/1     Running   0          82s
hello-rest-6c467745b6-zt98r   1/1     Running   0          82s
```

最後透過 `skaffold delete` 清除資源。

有關 Tag 資訊的[官方鏈結](https://skaffold-staging.web.app/docs/pipeline-stages/taggers/)。

## profile

區分不同開發環境

```yaml=
apiVersion: skaffold/v2beta15
kind: Config
metadata:
  name: hello-api
build:
  tagPolicy:
    gitCommit:
      variant: AbbrevCommitSha
  artifacts:
  - image: cch0124/hello-api
    context: .
    docker:
      dockerfile: Dockerfile
  local:
    push: true
deploy:
  kubectl:
    manifests:
    - k8s/deploy.yaml
    - k8s/service.yaml
    defaultNamespace: default
profiles:
  - name: prod
    activation:
    - kubeContext: k3d-prod
    build:
      tagPolicy:
        sha256: {}
      artifacts:
        - image: cch0124/hello-api-prod
          context: .
          docker:
            dockerfile: Dockerfile
            cacheFrom:
            - "maven:3.5.2-jdk-8-alpine"
            - "adoptopenjdk/openjdk11:jre-11.0.10_9-alpine"
  - name: stage
    activation:
    - kubeContext: k3d-stage
    build:
      artifacts:
        - image: cch0124/hello-api-stage
          custom:
            dockerfile: Dockerfile
            buildCommand: docker build .
            dependencies:
              dockerfile:
                path: Dockerfile
              paths:
                - "src/**/*"
                - "pom.xml"
              ignore:
                - k8s/**/*.yaml
      local:
        push: false
```

`DockerArtifact` 和 `CustomArtifact` 只能選一個來做設定。相關的自訂義可參考此[鏈接](https://skaffold.dev/docs/pipeline-stages/builders/custom/#dependencies-from-a-command)

`sha256: {}` 會將 `tag` 標記為 `latest`。雖然指定了 `kubeContext: k3d-prod` 但是還是無法佈署至該 `context` 下的群集，反而會自動搜尋當前的 context。需要多定義 `profiles.deploy.kubeContext` 指定 `deploy` 所要部署的 `context`，相關資源可參考此官方[文章](https://skaffold.dev/docs/environment/kube-context/)，如果以指令帶入的話可以這樣操作 `skaffold run --kubeconfig /etc/deploy/config`

```yaml=
profiles:
  - name: prod
    activation:
    - kubeContext: k3d-prod
    build:
      tagPolicy:
        sha256: {}
      artifacts:
        - image: cch0124/hello-api-prod
          context: .
          docker:
            dockerfile: Dockerfile
            cacheFrom:
            - "maven:3.5.2-jdk-8-alpine"
            - "adoptopenjdk/openjdk11:jre-11.0.10_9-alpine"
    deploy:
      kubeContext: k3d-prod
```

以 stage 的 `profile` 環境來看，`custom` 字段無法為 `image`  生成 `tagPolicy` 中定義的 `tag`。`dockerfile` 無法和 `buildCommand` 共同使用。須修正成如下


```yaml=
- name: stage
    activation:
    - kubeContext: k3d-stage
    build:
      artifacts:
        - image: cch0124/hello-api-stage
          custom:
            buildCommand: docker build -t cch0124/hello-api-stage:latest .
            dependencies:
              paths:
                - "src/**/*"
                - "pom.xml"
              ignore:
                - k8s/**/*.yaml
      local:
        push: false
```

也可以自訂義一個 shell script 來建置 image，在 `buildCommand` 中定義它怎麼執行，可參考此[鏈接](https://skaffold.dev/docs/tutorials/custom-builder/)。

最後的實驗完整 yaml 檔
```yaml=
apiVersion: skaffold/v2beta15
kind: Config
metadata:
  name: hello-api
build:
  tagPolicy:
    gitCommit:
      variant: AbbrevCommitSha
  artifacts:
  - image: cch0124/hello-api
    context: .
    docker:
      dockerfile: Dockerfile # 指定位置
  local:
    push: true
deploy:
  kubectl:
    manifests:
    - k8s/deploy.yaml
    - k8s/service.yaml
    defaultNamespace: default
profiles:
  - name: prod
    activation:
    - kubeContext: k3d-prod
    build:
      tagPolicy:
        sha256: {}
      artifacts:
        - image: cch0124/hello-api-prod
          context: .
          docker:
            dockerfile: Dockerfile
            cacheFrom:
            - "maven:3.5.2-jdk-8-alpine"
            - "adoptopenjdk/openjdk11:jre-11.0.10_9-alpine"
    deploy:
      kubeContext: k3d-prod
  - name: stage
    activation:
    - kubeContext: k3d-stage
    build:
      artifacts:
        - image: cch0124/hello-api-stage
          custom:
            buildCommand: docker build -t cch0124/hello-api-stage:latest .
            dependencies:
              paths:
                - "src/**/*"
                - "pom.xml"
              ignore:
                - k8s/**/*.yaml
      local:
        push: false
```

目前有遇到一個問題，當 `profile` 定義了不同環境時，image 的名稱不一樣時，會影響到 Kubernetes yaml 所定義的 image，要不統一要不就要將其變成環境變數使用。


關於清除建置的 Image 可參考此[官方鏈結](https://skaffold.dev/docs/pipeline-stages/cleanup/#image-pruning)。目前感覺只能用於 `dev` 的環境下。


接著是串接 gitlab CI/CD 來運行

代補充

官方對於 CI/CD 的[操作說明](https://skaffold.dev/docs/workflows/ci-cd/)



## 可參考資源
- [infracloud - skaffold-usecases](https://www.infracloud.io/blogs/skaffold-usecases/)
- [skaffold-gitlab-ci-cd](https://dev.to/thakkaryash94/kubernetes-auto-deployment-using-okteto-skaffold-gitlab-ci-cd-c84)
