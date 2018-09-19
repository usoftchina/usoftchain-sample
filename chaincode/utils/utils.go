package utils

import (
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"fmt"
	"bytes"
	"errors"
	"encoding/pem"
	"crypto/x509"
	"strings"
	"math/rand"
	"time"
	"strconv"
	"encoding/json"
)

var (
	formatDate = "20060102"
)

/**
 获取当前智能合约操作成员
 */
func GetCreator(stub shim.ChaincodeStubInterface) (name string, err error) {
	creatorByte, _ := stub.GetCreator()
	certStart := bytes.IndexAny(creatorByte, "-----BEGIN")
	if certStart == -1 {
		err = errors.New("No certificate found")
		return
	}
	certText := creatorByte[certStart:]
	bl, _ := pem.Decode(certText)
	if bl == nil {
		err = errors.New("Could not decode the PEM structure")
		return
	}
	cert, err := x509.ParseCertificate(bl.Bytes)
	if err != nil {
		return
	}
	name = cert.Subject.CommonName
	return
}

/**
 格式化当前智能合约操作成员名称
 */
func GetCreatorName(stub shim.ChaincodeStubInterface) (string, error) {
	name, err := GetCreator(stub)
	if err != nil {
		return "", err
	}
	memberName := name[(strings.Index(name, "@") + 1):strings.LastIndex(name, ".example.com")]
	return memberName, nil
}

/**
产生日期顺序的唯一流水号
 */
func RandNumber() string {
	r := rand.New(rand.NewSource(time.Now().UnixNano()))
	batchNum := time.Now().Format(formatDate) + strconv.FormatInt(time.Now().Unix(), 10)[4:] + strconv.Itoa(r.Intn(8999)+1000)
	return batchNum
}

/**
历史数据的JsonArray格式
 */
func GetHistoryListResult(resultsIterator shim.HistoryQueryIteratorInterface) ([]byte, error) {
	defer resultsIterator.Close()
	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}
		// Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten {
			buffer.WriteString(",")
		}
		item, err := json.Marshal(queryResponse)
		if err != nil {
			return nil, err
		}
		buffer.Write(item)
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")
	return buffer.Bytes(), nil
}

func GetListResult(resultsIterator shim.StateQueryIteratorInterface) ([]byte, error) {
	defer resultsIterator.Close()
	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}
		// Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten {
			buffer.WriteString(",")
		}
		item, err := json.Marshal(queryResponse)
		if err != nil {
			return nil, err
		}
		buffer.Write(item)
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")
	return buffer.Bytes(), nil
}

/**
按DocType查询全部
必须是CouchDB才行
 */
func GetListResultByDocType(stub shim.ChaincodeStubInterface, docType string) ([]byte, error) {
	queryString := fmt.Sprintf("{\"selector\":{\"docType\":\"%s\"}}", docType)
	resultsIterator, err := stub.GetQueryResult(queryString)
	if err != nil {
		return nil, err
	}
	return GetListResult(resultsIterator)
}
