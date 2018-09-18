package main

import (
	"encoding/json"
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
	"strconv"
	"time"
	"github.com/usoftchina/usoftchain-sample/chaincode/utils"
)

// 库存智能合约
type StockContract struct {
}

//// 账户
//type Account struct {
//	Num  string `json:"num"`
//	Name string `json:"name"`
//}

// 物料资料
type Product struct {
	// 编号、原厂型号
	Num string `json:"num"`
	// 规格
	Spec string `json:"spec"`
	// 组别、类目
	Group string `json:"group"`
	// 描述
	Desc string `json:"desc"`
	// 品牌
	Brand string `json:"brand"`
	// 单位
	Unit string `json:"unit"`
	// 创建者
	Creator string `json:"creator"`
	// docType
	DocType string `json:"docType"`
}

// 仓库
type Warehouse struct {
	Name    string `json:"name"`
	Desc    string `json:"desc"`
	Address string `json:"address"`
	Owner   string `json:"owner"`
	// 仓位
	Locations map[string]string `json:"locations"`
	// docType
	DocType string `json:"docType"`
}

// 库存批次
type Batch struct {
	Num           string  `json:"num"`
	Owner         string  `json:"owner"`
	ProductNum    string  `json:"productNum"`
	Quantity      float64 `json:"quantity"`
	WarehouseName string  `json:"warehouseName"`
	LocationName  string  `json:"locationName"`
	// 来源
	PreNum string `json:"preNum"`
	// 入库日期
	Indate int64 `json:"indate"`
	// 类型: 初始化init,交易trade,制造make
	BatchType string `json:"batchType"`
	// docType
	DocType string `json:"docType"`
}

var (
	defaultLocation     = "default"
	defaultLocationDesc = "默认仓位"
)

// 初始化
func (s *StockContract) Init(stub shim.ChaincodeStubInterface) peer.Response {
	return shim.Success(nil)
}

// 执行操作
func (s *StockContract) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	function, args := stub.GetFunctionAndParameters()
	// Route to the appropriate handler function to interact with the ledger appropriately
	/*if function == "createAccount" { // 创建账户
		return s.createAccount(stub, args)
	} else */if function == "createWarehouse" { // 创建仓库
		return s.createWarehouse(stub, args)
	} else if function == "createLocation" { // 创建仓位
		return s.createLocation(stub, args)
	} else if function == "createProduct" { // 创建物料
		return s.createProduct(stub, args)
	} else if function == "initStock" { // 库存初始化
		return s.initStock(stub, args, "init")
	} else if function == "completeStock" { // 完工入库
		return s.initStock(stub, args, "make")
	} else if function == "transferStock" { // 库存转移
		return s.transferStock(stub, args)
	} else if function == "queryBatch" { // 批次查询
		return s.queryBatch(stub, args)
	} else if function == "queryBatchHistory" { // 批次操作历史查询
		return s.queryBatchHistory(stub, args)
	} else if function == "queryBatchesByOwner" { // 查询所属批次
		return s.queryBatchesByOwner(stub, args)
	} else if function == "queryMyBatches" { // 查询我的批次
		return s.queryMyBatches(stub, args)
	} else if function == "queryAllBatches" { // 全部批次查询
		return s.queryAllBatches(stub)
	} else if function == "queryWarehouse" { // 仓库查询
		return s.queryWarehouse(stub, args)
	} else if function == "queryAllWarehouses" { // 全部仓库查询
		return s.queryAllWarehouses(stub)
	} else if function == "queryProduct" { // 物料查询
		return s.queryProduct(stub, args)
	} else if function == "queryAllProducts" { // 全部物料查询
		return s.queryAllProducts(stub)
	}/* else if function == "queryAccount" { // 账户查询
		return s.queryAccount(stub, args)
	}*/
	return shim.Error(fmt.Sprintf("Invalid Stock Contract function name %s, available functions (createWarehouse|createLocation|createProduct|initStock|completeStock|transferStock|queryBatch|queryBatchHistory|queryAllBatches|queryWarehouse|queryAllWarehouses|queryProduct|queryAllProducts)", function))
}

/**
创建账户
{"Args":["createAccount",num,name]}
{"Args":["createAccount","91441900760628116P","东莞市新宁仓储有限公司"]}
*/
//func (s *StockContract) createAccount(stub shim.ChaincodeStubInterface, args []string) peer.Response {
//	if len(args) != 2 {
//		return shim.Error("Incorrect number of arguments. Expecting 2")
//	}
//	accountBytes, err := stub.GetState(args[0])
//	if err != nil {
//		return shim.Error(err.Error())
//	}
//	if accountBytes != nil {
//		return shim.Error(fmt.Sprintf("The account of %s already exists", args[0]))
//	}
//	account := Account{Num: args[0], Name: args[1]}
//	accountBytes, err = json.Marshal(account)
//	if err != nil {
//		return shim.Error(err.Error())
//	}
//	err = stub.PutState(account.Num, accountBytes)
//	if err != nil {
//		return shim.Error(err.Error())
//	}
//	return shim.Success(nil)
//}

/**
创建仓库
{"Args":["createWarehouse",name,desc,address]}
{"Args":["createWarehouse","新宁","新宁仓","广东省东莞市长安镇乌沙第六工业区海滨路23号"]}
*/
func (s *StockContract) createWarehouse(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 3 {
		return shim.Error("Incorrect number of arguments. Expecting 3")
	}
	creator, err := utils.GetCreatorName(stub)
	if err != nil {
		return shim.Error(err.Error())
	}
	key := "warehouse:" + args[0]
	warehouseBytes, err := stub.GetState(key)
	if err != nil {
		return shim.Error(err.Error())
	}
	if warehouseBytes != nil {
		return shim.Error("The warehouse already exists")
	}
	defaultLocations := map[string]string{defaultLocation: defaultLocationDesc}
	warehouse := Warehouse{
		Owner:     creator,
		Name:      args[0],
		Desc:      args[1],
		Address:   args[2],
		Locations: defaultLocations,
		DocType:   "warehouse",
	}
	warehouseBytes, err = json.Marshal(warehouse)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(key, warehouseBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(nil)
}

/**
创建仓位
{"Args":["createLocation",warehouseName,locationName,(locationDesc)]}
{"Args":["createLocation","新宁","1-1","1-1"]}
*/
func (s *StockContract) createLocation(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 2 && len(args) != 3 {
		return shim.Error("Incorrect number of arguments. Expecting 2 or 3")
	}
	creator, err := utils.GetCreatorName(stub)
	if err != nil {
		return shim.Error(err.Error())
	}
	warehouseName := args[0]
	locationName := args[1]
	locationDesc := locationName
	if len(args) == 3 {
		locationDesc = args[2]
	}
	key := "warehouse:" + warehouseName
	warehouseBytes, err := stub.GetState(key)
	if err != nil {
		return shim.Error(err.Error())
	}
	if warehouseBytes == nil {
		return shim.Error(fmt.Sprintf("The warehouse of %s does not exist", warehouseName))
	}
	warehouse := Warehouse{}
	err = json.Unmarshal(warehouseBytes, &warehouse)
	if err != nil {
		return shim.Error(err.Error())
	}
	if warehouse.Owner != creator {
		return shim.Error(fmt.Sprintf("You are not the owner of %s", warehouseName))
	}
	_, exists := warehouse.Locations[locationName]
	if exists {
		return shim.Error(fmt.Sprintf("The location of %s already exists", locationName))
	}
	warehouse.Locations[locationName] = locationDesc
	warehouseBytes, err = json.Marshal(warehouse)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(key, warehouseBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(nil)
}

/**
创建物料
{"Args":["createProduct",num,spec,group,desc,brand,unit]}
{"Args":["createProduct","EMVA500ADA470MF80G","EMVA500ADA470MF80G","电容","铝电解电容","贵弥功NCC","PCS"]}
*/
func (s *StockContract) createProduct(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 6 {
		return shim.Error("Incorrect number of arguments. Expecting 6")
	}
	key := "product:" + args[0]
	productBytes, err := stub.GetState(key)
	if err != nil {
		return shim.Error(err.Error())
	}
	if productBytes != nil {
		return shim.Error("The product already exists")
	}
	product := Product{
		Num:     args[0],
		Spec:    args[1],
		Group:   args[2],
		Desc:    args[3],
		Brand:   args[4],
		Unit:    args[5],
		Creator: utils.GetCreatorName(stub),
		DocType: "product",
	}
	productBytes, err = json.Marshal(product)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(key, productBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(nil)
}

// key-value保存库存数量，方便查询
func queryStockQuantityByKey(stub shim.ChaincodeStubInterface, key string) (quantity float64, err error) {
	quantityBytes, err := stub.GetState(key)
	if err != nil {
		return
	}
	if quantityBytes == nil {
		quantity = 0
	} else {
		quantity, err = strconv.ParseFloat(string(quantityBytes), 64)
	}
	return
}

func modifyStockQuantityByKey(stub shim.ChaincodeStubInterface, key string, changedQuantity float64) (err error) {
	quantity, err := queryStockQuantityByKey(stub, key)
	if err != nil {
		return
	}
	quantity += changedQuantity
	return stub.PutState(key, []byte(strconv.FormatFloat(quantity, 'g', 30, 64)))
}

// 查询仓库物料库存
func queryWarehouseProductStock(stub shim.ChaincodeStubInterface, warehouseName string, productNum string) (quantity float64, err error) {
	key, err := stub.CreateCompositeKey("warehouseProductStock", []string{warehouseName, productNum})
	if err != nil {
		return
	}
	return queryStockQuantityByKey(stub, key)
}

// 查询账户物料库存
func queryOwnerProductStock(stub shim.ChaincodeStubInterface, owner string, productNum string) (quantity float64, err error) {
	key, err := stub.CreateCompositeKey("ownerProductStock", []string{owner, productNum})
	if err != nil {
		return
	}
	return queryStockQuantityByKey(stub, key)
}

// 修改仓库物料库存
func modifyWarehouseProductStock(stub shim.ChaincodeStubInterface, warehouseName string, productNum string, qty float64) error {
	key, err := stub.CreateCompositeKey("warehouseProductStock", []string{warehouseName, productNum})
	if err != nil {
		return err
	}
	return modifyStockQuantityByKey(stub, key, qty)
}

// 修改账户物料库存
func modifyOwnerProductStock(stub shim.ChaincodeStubInterface, owner string, productNum string, qty float64) error {
	key, err := stub.CreateCompositeKey("ownerProductStock", []string{owner, productNum})
	if err != nil {
		return err
	}
	return modifyStockQuantityByKey(stub, key, qty)
}

// 账户批次slice
func addOwnerBatch(stub shim.ChaincodeStubInterface, owner string, batchNum string) error {
	key := "ownerBatches:" + owner
	batchesBytes, err := stub.GetState(key)
	if err != nil {
		return err
	}
	var batches []string
	if batchesBytes != nil {
		err = json.Unmarshal(batchesBytes, &batches)
		if err != nil {
			return err
		}
	}
	batches = append(batches, batchNum)
	batchesBytes, err = json.Marshal(batches)
	if err != nil {
		return err
	}
	return stub.PutState(key, batchesBytes)
}

/**
无来源入库：库存初始化、完工入库
{"Args":["initStock","NCC24","新宁","1-1","1000.00"]}
*/
func (s *StockContract) initStock(stub shim.ChaincodeStubInterface, args []string, batchType string) peer.Response {
	if len(args) != 4 {
		return shim.Error("Incorrect number of arguments. Expecting 4")
	}
	creator, err := utils.GetCreatorName(stub)
	if err != nil {
		return shim.Error(err.Error())
	}
	productBytes, err := stub.GetState("product:" + args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	if productBytes == nil {
		return shim.Error(fmt.Sprintf("The product of %s does not exist", args[0]))
	}
	product := Product{}
	err = json.Unmarshal(productBytes, &product)
	if err != nil {
		return shim.Error(err.Error())
	}
	warehouseBytes, err := stub.GetState("warehouse:" + args[1])
	if err != nil {
		return shim.Error(err.Error())
	}
	if warehouseBytes == nil {
		return shim.Error(fmt.Sprintf("The warehouse of %s does not exist", args[1]))
	}
	warehouse := Warehouse{}
	err = json.Unmarshal(warehouseBytes, &warehouse)
	if err != nil {
		return shim.Error(err.Error())
	}
	locationName := args[2]
	if len(locationName) != 0 {
		_, exists := warehouse.Locations[locationName]
		if !exists {
			return shim.Error(fmt.Sprintf("The location of %s does not exist", locationName))
		}
	} else {
		locationName = defaultLocation
	}
	quantity, err := strconv.ParseFloat(args[3], 64)
	if err != nil {
		return shim.Error(err.Error())
	}
	batch := Batch{
		Num:           utils.RandNumber(),
		Owner:         creator,
		ProductNum:    product.Num,
		Quantity:      quantity,
		WarehouseName: warehouse.Name,
		LocationName:  locationName,
		Indate:        time.Now().UnixNano() / 1000000,
		BatchType:     batchType,
		DocType:       "batch",
	}
	batchBytes, err := json.Marshal(batch)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(batch.Num, batchBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = modifyWarehouseProductStock(stub, batch.WarehouseName, batch.ProductNum, quantity)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = modifyOwnerProductStock(stub, batch.Owner, batch.ProductNum, quantity)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = addOwnerBatch(stub, batch.Owner, batch.Num)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(batchBytes)
}

/**
库存转移：出货、采购验收
{"Args":["transferStock",toAccount,toWarehouseName,toLocationName,fromStockNum,quantity]}
{"Args":["transferStock","91440300MA5DC1WL1W","格力","1-1","201809039382631161","1000.00"]}
*/
func (s *StockContract) transferStock(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 5 {
		return shim.Error("Incorrect number of arguments. Expecting 5")
	}
	toAccount := args[0]
	toWarehouseName := args[1]
	toLocationName := args[2]
	fromBatchNum := args[3]
	quantityStr := args[4]
	toWarehouseBytes, err := stub.GetState("warehouse:" + toWarehouseName)
	if err != nil {
		return shim.Error(err.Error())
	}
	if toWarehouseBytes == nil {
		return shim.Error(fmt.Sprintf("The warehouse of %s does not exist", toWarehouseName))
	}
	toWarehouse := Warehouse{}
	err = json.Unmarshal(toWarehouseBytes, &toWarehouse)
	if err != nil {
		return shim.Error(err.Error())
	}
	if len(toLocationName) != 0 {
		_, exists := toWarehouse.Locations[toLocationName]
		if !exists {
			return shim.Error(fmt.Sprintf("The location of %s does not exists", toLocationName))
		}
	} else {
		toLocationName = defaultLocation
	}
	quantity, err := strconv.ParseFloat(quantityStr, 64)
	if err != nil {
		return shim.Error(err.Error())
	}
	if quantity <= 0 {
		return shim.Error(fmt.Sprintf("Illegal argument, quantity should be greater than 0"))
	}
	fromBatchBytes, err := stub.GetState(fromBatchNum)
	if err != nil {
		return shim.Error(err.Error())
	}
	if fromBatchBytes == nil {
		return shim.Error(fmt.Sprintf("The batch of %s does not exist", fromBatchNum))
	}
	fromBatch := Batch{}
	err = json.Unmarshal(fromBatchBytes, &fromBatch)
	if err != nil {
		return shim.Error(err.Error())
	}
	if fromBatch.Quantity < quantity {
		return shim.Error(fmt.Sprintf("Insufficient quantity of batch, %0.4f < %0.4f", fromBatch.Quantity, quantity))
	}
	fromBatch.Quantity = fromBatch.Quantity - quantity
	fromBatchBytes, err = json.Marshal(fromBatch)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(fromBatchNum, fromBatchBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	toBatch := Batch{
		Num:           utils.RandNumber(),
		Owner:         toAccount,
		ProductNum:    fromBatch.ProductNum,
		Quantity:      quantity,
		WarehouseName: toWarehouseName,
		LocationName:  toLocationName,
		Indate:        time.Now().UnixNano() / 1000000,
		PreNum:        fromBatch.Num,
		BatchType:     "trade",
		DocType:       "batch",
	}
	toBatchBytes, err := json.Marshal(toBatch)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(toBatch.Num, toBatchBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = modifyWarehouseProductStock(stub, fromBatch.WarehouseName, fromBatch.ProductNum, -quantity)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = modifyOwnerProductStock(stub, fromBatch.Owner, fromBatch.ProductNum, -quantity)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = modifyWarehouseProductStock(stub, toBatch.WarehouseName, toBatch.ProductNum, quantity)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = modifyOwnerProductStock(stub, toBatch.Owner, toBatch.ProductNum, quantity)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = addOwnerBatch(stub, toBatch.Owner, toBatch.Num)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(toBatchBytes)
}

/**
批次查询
{"Args":["queryBatch",batchNum]}
{"Args":["queryBatch","201809039382631161"]}
*/
func (s *StockContract) queryBatch(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}
	batchBytes, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	if batchBytes == nil {
		return shim.Error(fmt.Sprintf("The batch of %s does not exist", args[0]))
	}
	return shim.Success(batchBytes)
}

/**
批次操作历史查询
{"Args":["queryBatchHistory",batchNum]}
{"Args":["queryBatchHistory","201809039382631161"]}
*/
func (s *StockContract) queryBatchHistory(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}
	it, err := stub.GetHistoryForKey(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	if it == nil {
		return shim.Error(fmt.Sprintf("The batch of %s does not exist", args[0]))
	}
	hisBytes, err := utils.GetHistoryListResult(it)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(hisBytes)
}

///**
//账户查询
//{"Args":["queryAccount",num]}
//{"Args":["queryAccount","91441900760628116P"]}
//*/
//func (s *StockContract) queryAccount(stub shim.ChaincodeStubInterface, args []string) peer.Response {
//	if len(args) != 1 {
//		return shim.Error("Incorrect number of arguments. Expecting 1")
//	}
//	accountBytes, err := stub.GetState(args[0])
//	if err != nil {
//		return shim.Error(err.Error())
//	}
//	if accountBytes == nil {
//		return shim.Error(fmt.Sprintf("The account of %s does not exist", args[0]))
//	}
//	return shim.Success(accountBytes)
//}

/**
仓库查询
{"Args":["queryWarehouse",name]}
{"Args":["queryWarehouse","新宁"]}
*/
func (s *StockContract) queryWarehouse(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}
	warehouseBytes, err := stub.GetState("warehouse:" + args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	if warehouseBytes == nil {
		return shim.Error(fmt.Sprintf("The warehouse of %s does not exist", args[0]))
	}
	return shim.Success(warehouseBytes)
}

/**
全部仓库查询
{"Args":["queryAllWarehouses"]}
{"Args":["queryAllWarehouses"]}
*/
func (s *StockContract) queryAllWarehouses(stub shim.ChaincodeStubInterface) peer.Response {
	warehouseBytes, err := utils.GetListResultByDocType(stub, "warehouse")
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(warehouseBytes)
}

/**
物料查询
{"Args":["queryProduct",num]}
{"Args":["queryProduct","EMVA500ADA470MF80G"]}
*/
func (s *StockContract) queryProduct(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}
	productBytes, err := stub.GetState("product:" + args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	if productBytes == nil {
		return shim.Error(fmt.Sprintf("The product of %s does not exist", args[0]))
	}
	return shim.Success(productBytes)
}

/**
全部物料查询
{"Args":["queryAllProducts"]}
{"Args":["queryAllProducts"]}
*/
func (s *StockContract) queryAllProducts(stub shim.ChaincodeStubInterface) peer.Response {
	productBytes, err := utils.GetListResultByDocType(stub, "product")
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(productBytes)
}

/**
所属批次查询
{"Args":["queryBatchesByOwner",owner]}
{"Args":["queryBatchesByOwner","新宁"]}
*/
func (s *StockContract) queryBatchesByOwner(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}
	batchesBytes, err := stub.GetState("ownerBatches:" + args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(batchesBytes)
}

/**
我的批次查询
{"Args":["queryMyBatches"]}
*/
func (s *StockContract) queryMyBatches(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 0 {
		return shim.Error("Incorrect number of arguments. Expecting 0")
	}
	owner, err := utils.GetCreatorName(stub)
	if err != nil {
		return shim.Error(err.Error())
	}
	return s.queryBatchesByOwner(stub, []string{owner})
}

/**
全部批次查询
{"Args":["queryAllBatches"]}
{"Args":["queryAllBatches"]}
*/
func (s *StockContract) queryAllBatches(stub shim.ChaincodeStubInterface) peer.Response {
	batchBytes, err := utils.GetListResultByDocType(stub, "batch")
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(batchBytes)
}

func main() {
	if err := shim.Start(new(StockContract)); err != nil {
		fmt.Printf("Error creating new Stock Contract: %s", err)
	}
}
