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
     peer chaincode invoke -n mycc -c '{"Args":["createAccount","91441900760628116P","东莞市新宁仓储有限公司"]}' -C myc
     peer chaincode invoke -n stockcontract -c '{"Args":["createAccount","91441900760628116P","东莞市新宁仓储有限公司"]}' -C myc
     peer chaincode invoke -n stockcontract -c '{"Args":["createAccount","91441900760628116P","东莞市新宁仓储有限公司"]}' -C myc
```