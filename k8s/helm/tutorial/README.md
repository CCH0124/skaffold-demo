# tutorial

![Version: 0.1.0](https://img.shields.io/badge/Version-0.1.0-informational?style=for-the-badge)
![Type: application](https://img.shields.io/badge/Type-application-informational?style=for-the-badge) 
![AppVersion: 1.16.0](https://img.shields.io/badge/AppVersion-1.16.0-informational?style=for-the-badge) 

## tutorial Chart Information


[Homepage](https://github.com/CCH0124/skaffold-demo)

## Source Code

* <https://github.com/CCH0124/skaffold-demo>

## Maintainers

| Name | Email | Url |
| ---- | ------ | --- |
| Itachi | <test@gmail.com> | <https://google.com.tw> |


## Description

A Helm chart for Kubernetes


## Requirements

Kubernetes: `>=1.23.4-0`



## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| "ports.containerPort".http | string | `"8080"` |  |
| affinity | object | `{}` |  |
| autoscaling.enabled | bool | `false` |  |
| autoscaling.maxReplicas | int | `100` |  |
| autoscaling.minReplicas | int | `1` |  |
| autoscaling.targetCPUUtilizationPercentage | int | `80` |  |
| configAnnotations | object | `{}` |  |
| extraInitContainers | list | `[]` |  |
| fullnameOverride | string | `""` |  |
| image.pullPolicy | string | `"IfNotPresent"` |  |
| image.repository | string | `"nginx"` |  |
| image.tag | string | `""` | @default Chart version |
| imagePullSecrets | list | `[]` |  |
| ingress.annotations | object | `{}` |  |
| ingress.className | string | `""` |  |
| ingress.enabled | bool | `false` |  |
| ingress.hosts[0].host | string | `"chart-example.local"` |  |
| ingress.hosts[0].paths[0].path | string | `"/"` |  |
| ingress.hosts[0].paths[0].pathType | string | `"ImplementationSpecific"` |  |
| ingress.tls | list | `[]` |  |
| lifecycle | object | `{}` | application lifecycle |
| livenessProbe.failureThreshold | int | `5` |  |
| livenessProbe.httpGet.path | string | `"/actuator/health/liveness"` |  |
| livenessProbe.httpGet.port | int | `8080` |  |
| livenessProbe.httpGet.scheme | string | `"HTTP"` |  |
| livenessProbe.initialDelaySeconds | int | `60` |  |
| livenessProbe.periodSeconds | int | `20` |  |
| livenessProbe.timeoutSeconds | int | `3` |  |
| nameOverride | string | `""` |  |
| nodeSelector | object | `{}` |  |
| podAnnotations | object | `{}` |  |
| podSecurityContext | object | `{}` |  |
| postgresql.password | string | `""` |  |
| postgresql.url | string | `""` | postgresql connection url |
| postgresql.user | string | `""` |  |
| readinessProbe.httpGet.path | string | `"/actuator/health/readiness"` |  |
| readinessProbe.httpGet.port | int | `8080` |  |
| readinessProbe.httpGet.scheme | string | `"HTTP"` |  |
| readinessProbe.initialDelaySeconds | int | `60` |  |
| readinessProbe.periodSeconds | int | `20` |  |
| readinessProbe.successThreshold | int | `1` |  |
| readinessProbe.timeoutSeconds | int | `3` |  |
| replicaCount | int | `1` |  |
| resources | object | `{}` |  |
| securityContext | object | `{}` |  |
| service.annotations | object | `{}` |  |
| service.enableHttp | bool | `true` |  |
| service.enableHttps | bool | `true` |  |
| service.enabled | bool | `true` |  |
| service.externalIPs | list | `[]` |  |
| service.labels | object | `{}` |  |
| service.loadBalancerIP | string | `""` |  |
| service.loadBalancerSourceRanges | list | `[]` |  |
| service.nodePorts.http | string | `""` |  |
| service.nodePorts.https | string | `""` |  |
| service.ports.http | int | `80` |  |
| service.ports.https | int | `443` |  |
| service.targetPorts.http | string | `"http"` |  |
| service.targetPorts.https | string | `"https"` |  |
| service.type | string | `"ClusterIP"` |  |
| serviceAccount.annotations | object | `{}` |  |
| serviceAccount.create | bool | `true` |  |
| serviceAccount.name | string | `""` |  |
| strategy | object | `{}` |  |
| tolerations | list | `[]` |  |