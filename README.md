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
| IP        | Host   |  Organization  |
| --------   | -----:  | :----:  |
| 192.168.0.176  | orderer.example.com | Orderer |
| 192.168.0.177  | peer0.org1.example.com   | Org1 |
| 192.168.0.178  | peer1.org1.example.com   | Org1 |
| 192.168.0.179  | peer0.org2.example.com   | Org2 |
| 192.168.0.180  | peer1.org2.example.com   | Org2 |

### 2.环境配置
```shell
#go
wget https://studygolang.com/dl/golang/go1.10.3.linux-amd64.tar.gz
tar -C /usr/local/ -xzf go1.10.3.linux-amd64.tar.gz
#vi /etc/profile
go get github.com/hyperledger/fabric
#按官方教程编译
cd go/src/github.com/hyperledger/fabric/examples/e2e_cli
./generateArtifacts.sh
#将crypto-config和channel-artifacts文件夹scp -rp到其他4台服务器
go get github.com/usoftchina/usoftchain-sample
#usoftchain-sample/e2e_cli的yaml是基于docker-compose-cli.yaml修改而来
#复制到对应节点的go/src/github.com/hyperledger/fabric/examples/e2e_cli目录
```

### 3.启动节点

```shell
#登录192.168.0.176
cd go/src/github.com/hyperledger/fabric/examples/e2e_cli/
docker-compose -f docker-compose-orderer.yaml up -d
#分别登录192.168.0.177,178,179,180
cd go/src/github.com/hyperledger/fabric/examples/e2e_cli/
docker-compose -f docker-compose-peer.yaml up -d
```

### 4.创建channel
