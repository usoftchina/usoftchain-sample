package main

import (
	"encoding/json"
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
	"math/rand"
	"strconv"
	"time"
)

// 库存智能合约
type StockContract struct {
}

// 账户
type Account struct {
	Num  string `json:"num"`
	Name string `json:"name"`
}

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
}

// 仓库
type Warehouse struct {
	Name    string `json:"name"`
	Desc    string `json:"desc"`
	Address string `json:address`
	Owner   string `json:owner`
	// 仓位
	Locations map[string]string `json:"locations"`
}

// 库存交易操作
type Stock struct {
	Num           string  `json:"num"`
	AccountNum    string  `json:"accountNum"`
	ProductNum    string  `json:"productNum"`
	Quantity      float64 `json:"quantity"`
	WarehouseName string  `json:"warehouseName"`
	LocationName  string  `json:"locationName"`
	// 来源
	PreNum string `json:"preNum"`
	// 入库日期
	Indate int64 `json:indate`
	// 类型: 初始化init,交易trade,制造make
	StockType string `json:"stockType"`
}

var (
	formatDate          = "20060102"
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
	if function == "createAccount" { // 创建账户
		return s.createAccount(stub, args)
	} else if function == "createWarehouse" { // 创建仓库
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
	} else if function == "queryStock" { // 库存查询
		return s.queryStock(stub, args)
	} else if function == "queryAccount" { // 账户查询
		return s.queryAccount(stub, args)
	} else if function == "queryWarehouse" { // 仓库查询
		return s.queryWarehouse(stub, args)
	}
	return shim.Error(fmt.Sprintf("Invalid Stock Contract function name %s, available functions (createAccount|createWarehouse|createLocation|createProduct|initStock|completeStock|transferStock|queryStock|queryAccount|queryWarehouse)", function))
}

/**
创建账户
{"Args":["createAccount",num,name]}
{"Args":["createAccount","91441900760628116P","东莞市新宁仓储有限公司"]}
*/
func (s *StockContract) createAccount(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}
	accountBytes, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	if accountBytes != nil {
		return shim.Error(fmt.Sprintf("The account of %s already exists", args[0]))
	}
	account := Account{Num: args[0], Name: args[1]}
	accountBytes, err = json.Marshal(account)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(account.Num, accountBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(nil)
}

/**
创建仓库
{"Args":["createWarehouse",owner,name,desc,address]}
{"Args":["createWarehouse","91441900760628116P","新宁","新宁仓","广东省东莞市长安镇乌沙第六工业区海滨路23号"]}
*/
func (s *StockContract) createWarehouse(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 4 {
		return shim.Error("Incorrect number of arguments. Expecting 4")
	}
	warehouseBytes, err := stub.GetState(args[1])
	if err != nil {
		return shim.Error(err.Error())
	}
	if warehouseBytes != nil {
		return shim.Error("The warehouse already exists")
	}
	defaultLocations := map[string]string{defaultLocation: defaultLocationDesc}
	warehouse := Warehouse{
		Owner:     args[0],
		Name:      args[1],
		Desc:      args[2],
		Address:   args[3],
		Locations: defaultLocations,
	}
	warehouseBytes, err = json.Marshal(warehouse)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(warehouse.Name, warehouseBytes)
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
	warehouseName := args[0]
	locationName := args[1]
	locationDesc := locationName
	if len(args) == 3 {
		locationDesc = args[2]
	}
	warehouseBytes, err := stub.GetState(warehouseName)
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
	_, exists := warehouse.Locations[locationName]
	if exists {
		return shim.Error(fmt.Sprintf("The location of %s already exists", locationName))
	}
	warehouse.Locations[locationName] = locationDesc
	warehouseBytes, err = json.Marshal(warehouse)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(warehouse.Name, warehouseBytes)
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
	productBytes, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	if productBytes != nil {
		return shim.Error("The product already exists")
	}
	product := Product{
		Num:   args[0],
		Spec:  args[1],
		Group: args[2],
		Desc:  args[3],
		Brand: args[4],
		Unit:  args[5],
	}
	productBytes, err = json.Marshal(product)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(product.Num, productBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(nil)
}

// 产生唯一流水号
func randNumber() string {
	r := rand.New(rand.NewSource(time.Now().UnixNano()))
	batchNum := time.Now().Format(formatDate) + strconv.FormatInt(time.Now().Unix(), 10)[4:] + strconv.Itoa(r.Intn(8999)+1000)
	return batchNum
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
	key := fmt.Sprintf(`"warehouseName:"%s","productNum":"%s"`, warehouseName, productNum)
	return queryStockQuantityByKey(stub, key)
}

// 查询账户物料库存
func queryAccountProductStock(stub shim.ChaincodeStubInterface, accountNum string, productNum string) (quantity float64, err error) {
	key := fmt.Sprintf(`"accountNum:"%s","productNum":"%s"`, accountNum, productNum)
	return queryStockQuantityByKey(stub, key)
}

// 修改仓库物料库存
func modifyWarehouseProductStock(stub shim.ChaincodeStubInterface, warehouseName string, productNum string, qty float64) error {
	key := fmt.Sprintf(`"warehouseName:"%s","productNum":"%s"`, warehouseName, productNum)
	return modifyStockQuantityByKey(stub, key, qty)
}

// 修改账户物料库存
func modifyAccountProductStock(stub shim.ChaincodeStubInterface, accountNum string, productNum string, qty float64) error {
	key := fmt.Sprintf(`"accountNum:"%s","productNum":"%s"`, accountNum, productNum)
	return modifyStockQuantityByKey(stub, key, qty)
}

/**
无来源入库：库存初始化、完工入库
{"Args":["initStock","914403006658721616","NCC24","新宁","1-1","1000.00"]}
*/
func (s *StockContract) initStock(stub shim.ChaincodeStubInterface, args []string, stockType string) peer.Response {
	if len(args) != 5 {
		return shim.Error("Incorrect number of arguments. Expecting 5")
	}
	productBytes, err := stub.GetState(args[1])
	if err != nil {
		return shim.Error(err.Error())
	}
	if productBytes == nil {
		return shim.Error(fmt.Sprintf("The product of %s does not exist", args[1]))
	}
	product := Product{}
	err = json.Unmarshal(productBytes, &product)
	if err != nil {
		return shim.Error(err.Error())
	}
	warehouseBytes, err := stub.GetState(args[2])
	if err != nil {
		return shim.Error(err.Error())
	}
	if warehouseBytes == nil {
		return shim.Error(fmt.Sprintf("The warehouse of %s does not exist", args[2]))
	}
	warehouse := Warehouse{}
	err = json.Unmarshal(warehouseBytes, &warehouse)
	if err != nil {
		return shim.Error(err.Error())
	}
	locationName := args[3]
	if len(locationName) != 0 {
		_, exists := warehouse.Locations[locationName]
		if !exists {
			return shim.Error(fmt.Sprintf("The location of %s does not exist", locationName))
		}
	} else {
		locationName = defaultLocation
	}
	quantity, err := strconv.ParseFloat(args[4], 64)
	if err != nil {
		return shim.Error(err.Error())
	}
	stock := Stock{
		Num:           randNumber(),
		AccountNum:    args[0],
		ProductNum:    product.Num,
		Quantity:      quantity,
		WarehouseName: warehouse.Name,
		LocationName:  locationName,
		Indate:        time.Now().Unix(),
		StockType:     stockType,
	}
	stockBytes, err := json.Marshal(stock)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(stock.Num, stockBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = modifyWarehouseProductStock(stub, stock.WarehouseName, stock.ProductNum, quantity)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = modifyAccountProductStock(stub, stock.AccountNum, stock.ProductNum, quantity)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(stockBytes)
}

/**
库存转移：出货、采购验收
{"Args":["transferStock",toAccountNum,toWarehouseName,toLocationName,fromStockNum,quantity]}
{"Args":["transferStock","91440300MA5DC1WL1W","格力","1-1","201809039382631161","1000.00"]}
*/
func (s *StockContract) transferStock(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 5 {
		return shim.Error("Incorrect number of arguments. Expecting 5")
	}
	toAccountNum := args[0]
	toWarehouseName := args[1]
	toLocationName := args[2]
	fromStockNum := args[3]
	quantityStr := args[4]
	toAccountBytes, err := stub.GetState(toAccountNum)
	if err != nil {
		return shim.Error(err.Error())
	}
	if toAccountBytes == nil {
		return shim.Error(fmt.Sprintf("The account of %s does not exist", toAccountNum))
	}
	toWarehouseBytes, err := stub.GetState(toWarehouseName)
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
	fromStockBytes, err := stub.GetState(fromStockNum)
	if err != nil {
		return shim.Error(err.Error())
	}
	if fromStockBytes == nil {
		return shim.Error(fmt.Sprintf("The stock of %s does not exist", fromStockNum))
	}
	fromStock := Stock{}
	err = json.Unmarshal(fromStockBytes, &fromStock)
	if err != nil {
		return shim.Error(err.Error())
	}
	if fromStock.Quantity < quantity {
		return shim.Error(fmt.Sprintf("Insufficient quantity of batch, %0.4f < %0.4f", fromStock.Quantity, quantity))
	}
	fromStock.Quantity = fromStock.Quantity - quantity
	fromStockBytes, err = json.Marshal(fromStock)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(fromStockNum, fromStockBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	toStock := Stock{
		Num:           randNumber(),
		AccountNum:    toAccountNum,
		ProductNum:    fromStock.ProductNum,
		Quantity:      quantity,
		WarehouseName: toWarehouseName,
		LocationName:  toLocationName,
		Indate:        time.Now().Unix(),
		PreNum:        fromStock.Num,
		StockType:     "trade",
	}
	toStockBytes, err := json.Marshal(toStock)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(toStock.Num, toStockBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = modifyWarehouseProductStock(stub, fromStock.WarehouseName, fromStock.ProductNum, -quantity)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = modifyAccountProductStock(stub, fromStock.AccountNum, fromStock.ProductNum, -quantity)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = modifyWarehouseProductStock(stub, toStock.WarehouseName, toStock.ProductNum, quantity)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = modifyAccountProductStock(stub, toStock.AccountNum, toStock.ProductNum, quantity)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(toStockBytes)
}

/**
库存查询
{"Args":["queryStock",stockNum]}
{"Args":["queryStock","201809039382631161"]}
*/
func (s *StockContract) queryStock(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}
	stockBytes, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	if stockBytes == nil {
		return shim.Error(fmt.Sprintf("The stock of %s does not exist", args[0]))
	}
	return shim.Success(stockBytes)
}

/**
账户查询
{"Args":["queryAccount",num]}
{"Args":["queryAccount","91441900760628116P"]}
*/
func (s *StockContract) queryAccount(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}
	accountBytes, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	if accountBytes == nil {
		return shim.Error(fmt.Sprintf("The account of %s does not exist", args[0]))
	}
	return shim.Success(accountBytes)
}

/**
仓库查询
{"Args":["queryWarehouse",name]}
{"Args":["queryWarehouse","新宁"]}
*/
func (s *StockContract) queryWarehouse(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}
	warehouseBytes, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error(err.Error())
	}
	if warehouseBytes == nil {
		return shim.Error(fmt.Sprintf("The warehouse of %s does not exist", args[0]))
	}
	return shim.Success(warehouseBytes)
}

func main() {
	if err := shim.Start(new(StockContract)); err != nil {
		fmt.Printf("Error creating new Stock Contract: %s", err)
	}
}
