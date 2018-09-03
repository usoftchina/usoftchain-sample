## 调试

```shell
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
     # return stock record
     peer chaincode invoke -n stockcontract -c '{"Args":["transferStock","91440300778798789B","天派材料良品仓","1-1",(stock record number),"800.00"]}' -C myc
     # return stock record
     peer chaincode invoke -n stockcontract -c '{"Args":["queryStock",(stock record number)]}' -C myc

```