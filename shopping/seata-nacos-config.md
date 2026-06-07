# Seata 分布式事务 Nacos 配置

> 将以下配置分别添加到对应模块的 Nacos 配置文件中

---

## 1. shopping_order_service-dev.yaml — 添加

```yaml
# seata配置
seata:
  registry:
    type: nacos
    nacos:
      server-addr: 192.168.66.100:8848
      application: seata-tc-server
      group: DEFAULT_GROUP
  tx-service-group: seata-demo
  service:
    vgroup-mapping:
      seata-demo: BJ
  data-source-proxy-mode: XA
```

## 2. shopping_pay_service-dev.yaml — 添加

```yaml
# seata配置
seata:
  registry:
    type: nacos
    nacos:
      server-addr: 192.168.66.100:8848
      application: seata-tc-server
      group: DEFAULT_GROUP
  tx-service-group: seata-demo
  service:
    vgroup-mapping:
      seata-demo: BJ
  data-source-proxy-mode: XA
```

## 3. shopping_order_customer_api-dev.yaml — 添加

```yaml
# seata配置
seata:
  registry:
    type: nacos
    nacos:
      server-addr: 192.168.66.100:8848
      application: seata-tc-server
      group: DEFAULT_GROUP
  tx-service-group: seata-demo
  service:
    vgroup-mapping:
      seata-demo: BJ
  data-source-proxy-mode: XA
```

---

> ⚠️ 注意：Nacos 地址已统一为 `192.168.66.100:8848`
