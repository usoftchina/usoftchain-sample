## 单机调试

```shell
   # 登录开发环境192.168.0.174
   go get github.com/usoftchina/usoftchain-sample
   cd go/src/github.com/usoftchina/usoftchain-sample/chaincode-docker-devmode/
   docker-compose -f docker-compose-simple.yaml up -d
   docker exec -it chaincode bash
     cd stock
     go build
     CORE_PEER_ADDRESS=peer:7051 CORE_CHAINCODE_ID_NAME=stockcontract:0 ./stock
   docker exec -it cli bash
     peer chaincode install -p chaincodedev/chaincode/stock -n stockcontract -v 0
     peer chaincode instantiate -n stockcontract -v 0 -c '{"Args":["init"]}' -C myc
     peer chaincode invoke -n stockcontract -c '{"Args":["createAccount","91441900760628116P","东莞市新宁仓储有限公司"]}' -C myc
     peer chaincode invoke -n stockcontract -c '{"Args":["createAccount","91440300319521190W","深圳市华商龙商务互联科技有限公司"]}' -C myc
     peer chaincode invoke -n stockcontract -c '{"Args":["createAccount","91440300778798789B","天派电子(深圳)有限公司"]}' -C myc
     peer chaincode invoke -n stockcontract -c '{"Args":["createWarehouse","91441900760628116P","新宁","新宁仓","广东省东莞市长安镇乌沙第六工业区海滨路23号"]}' -C myc
     peer chaincode invoke -n stockcontract -c '{"Args":["createLocation","新宁","1-1"]}' -C myc
     peer chaincode invoke -n stockcontract -c '{"Args":["createWarehouse","91440300778798789B","天派材料良品仓","天派材料良品仓","深圳市宝安区福永镇新和村新兴工业园6区A1栋"]}' -C myc
     peer chaincode invoke -n stockcontract -c '{"Args":["createLocation","天派材料良品仓","1-1"]}' -C myc
     peer chaincode invoke -n stockcontract -c '{"Args":["createProduct","EMVA500ADA470MF80G","EMVA500ADA470MF80G","电容","铝电解电容","贵弥功NCC","PCS"]}' -C myc
     peer chaincode invoke -n stockcontract -c '{"Args":["initStock","91440300319521190W","EMVA500ADA470MF80G","新宁","1-1","1000.00"]}' -C myc
     # "{\"num\":\"201809039811064342\",\"accountNum\":\"91440300319521190W\",\"productNum\":\"EMVA500ADA470MF80G\",\"quantity\":1000,\"warehouseName\":\"\346\226\260\345\256\201\",\"locationName\":\"1-1\",\"preNum\":\"\",\"Indate\":\"2018-09-03T13:25:06.654982388Z\",\"stockType\":\"init\"}"
     peer chaincode invoke -n stockcontract -c '{"Args":["transferStock","91440300778798789B","天派材料良品仓","1-1","201809039811064342","800.00"]}' -C myc
     # "{\"num\":\"201809039812049459\",\"accountNum\":\"91440300778798789B\",\"productNum\":\"EMVA500ADA470MF80G\",\"quantity\":800,\"warehouseName\":\"\345\244\251\346\264\276\346\235\220\346\226\231\350\211\257\345\223\201\344\273\223\",\"locationName\":\"1-1\",\"preNum\":\"201809039811064342\",\"Indate\":\"2018-09-03T13:26:44.315009078Z\",\"stockType\":\"trade\"}"
     peer chaincode invoke -n stockcontract -c '{"Args":["queryStock","201809039811064342"]}' -C myc
     # "{\"num\":\"201809039811064342\",\"accountNum\":\"91440300319521190W\",\"productNum\":\"EMVA500ADA470MF80G\",\"quantity\":200,\"warehouseName\":\"\346\226\260\345\256\201\",\"locationName\":\"1-1\",\"preNum\":\"\",\"Indate\":\"2018-09-03T13:25:06.654982388Z\",\"stockType\":\"init\"}"
     peer chaincode invoke -n stockcontract -c '{"Args":["queryStock","201809039812049459"]}' -C myc
     # "{\"num\":\"201809039812049459\",\"accountNum\":\"91440300778798789B\",\"productNum\":\"EMVA500ADA470MF80G\",\"quantity\":800,\"warehouseName\":\"\345\244\251\346\264\276\346\235\220\346\226\231\350\211\257\345\223\201\344\273\223\",\"locationName\":\"1-1\",\"preNum\":\"201809039811064342\",\"Indate\":\"2018-09-03T13:26:44.315009078Z\",\"stockType\":\"trade\"}"

```

## 多节点集群环境

### 1.节点配置

| IP        | Host   |
| --------   | :-----: |
| 192.168.0.176  | zookeeper0 |
| 192.168.0.177  | zookeeper1   |
| 192.168.0.178  | zookeeper2   |

| IP        | Host   |
| --------   | :-----: |
| 192.168.0.176  | kafka0 |
| 192.168.0.177  | kafka1   |
| 192.168.0.178  | kafka2   |
| 192.168.0.179  | kafka3   |

| IP        | Host   |  Organization  |
| --------   | -----:  | :----:  |
| 192.168.0.176  | orderer.example.com | Orderer |
| 192.168.0.177  | peer0.org1.example.com   | Org1 |
| 192.168.0.178  | peer1.org1.example.com   | Org1 |
| 192.168.0.179  | peer0.org2.example.com   | Org2 |
| 192.168.0.180  | peer1.org2.example.com   | Org2 |



### 2.环境配置
```shell
#先安装go、docker环境
go get github.com/hyperledger/fabric
go get github.com/usoftchina/usoftchain-sample
```

| IP        | Open Ports  |
| --------   | :----:  |
| 192.168.0.176  | 2181,2888,3888,7050,9092 |
| 192.168.0.177  | 2181,2888,3888,7051,7052,7053,9092 |
| 192.168.0.178  | 2181,2888,3888,7051,7052,7053,9092 |
| 192.168.0.179  | 7051,7052,7053,9092 |
| 192.168.0.180  | 7051,7052,7053 |

### 3.启动节点

```shell
#登录192.168.0.176
cd go/src/github.com/usoftchina/usoftchain-sample/e2e_cli/
./network_setup.sh up
```

### 4.创建channel
