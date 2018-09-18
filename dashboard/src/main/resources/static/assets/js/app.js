define(['ui.router', 'ngTable', 'toaster'], function () {
    'use strict';
    var app = angular.module('myApp', ['ui.router', 'ngTable', 'toaster']);
    app.init = function () {
        angular.bootstrap(document, ['myApp']);
    };
    app.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise('/');
        $stateProvider.state('index', {
            url: "/",
            views: {
                'global-view': {templateUrl: 'assets/tpl/blockchain.html'}
            },
            title: "首页"
        }).state('block', {
            url: "/block",
            views: {
                'global-view': {templateUrl: 'assets/tpl/block.html'}
            },
            title: "区块列表"
        }).state('transaction', {
            url: "/transaction",
            views: {
                'global-view': {templateUrl: 'assets/tpl/transaction.html'}
            },
            title: "交易列表"
        });
    }]);
    app.controller('MyCtrl', ['$scope', '$rootScope', '$http', 'toaster', function ($scope, $rootScope, $http, toaster) {
        $scope.blockchains = [];
        $scope.activeBlockchain = {};
        $scope.activeTabName = '首页';
        $http.get('/blockchain').success(function (data) {
            if (data.success) {
                $scope.blockchains = data.content;
                $scope.activeBlockchain = data.content[0];
            } else {
                toaster.pop('error', '错误', data.message);
            }
        });
        $scope.toggleBlockchain = function(item) {
            $scope.activeBlockchain = item;
        };

        $rootScope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
            $scope.activeTabName = toState.title;
        });
    }]);
    app.controller('BlockchainCtl', ['$scope', '$http', 'toaster', function ($scope, $http, toaster) {
        $scope.channel = {};
        $scope.$watch('activeBlockchain', function (newValue, oldValue) {
            if (newValue && newValue.channel) {
                $http.get('/channel?name=' + newValue.channel).success(function (data) {
                    if (data.success) {
                        $scope.channel = data.content;
                    } else {
                        toaster.pop('error', '错误', data.message);
                    }
                });
            }
        }, true);
    }]);
    app.controller('BlockCtl', ['$scope', '$http', 'ngTableParams', 'toaster', function ($scope, $http, ngTableParams, toaster) {
        $scope.tabelParams = new ngTableParams({
            page: 1,
            count: 25
        }, {
            total: 0,
            counts: [],
            getData: function ($defer, params) {
                if ($scope.activeBlockchain && $scope.activeBlockchain.channel) {
                    $http.get('/block', {
                        params: {
                            channel: $scope.activeBlockchain.channel,
                            page: params.url().page - 1,
                            size: params.url().count
                        }
                    }).success(function (data) {
                        if (data.success) {
                            params.total(data.content.totalElements);
                            $defer.resolve(data.content.content);
                        } else {
                            toaster.pop('error', '错误', data.message);
                        }
                    });
                }
            }
        });
        $scope.$watch('activeBlockchain', function (newValue, oldValue) {
            if (newValue && newValue.channel) {
                $scope.tabelParams.reload();
            }
        }, true);
    }]);
    app.controller('TransactionCtl', ['$scope', '$http', 'ngTableParams', 'toaster', function ($scope, $http, ngTableParams, toaster) {
        $scope.activeTransaction = {};
        $scope.tabelParams = new ngTableParams({
            page: 1,
            count: 25
        }, {
            total: 0,
            counts: [],
            getData: function ($defer, params) {
                if ($scope.activeBlockchain && $scope.activeBlockchain.channel) {
                    $http.get('/transaction/page', {
                        params: {
                            channel: $scope.activeBlockchain.channel,
                            page: params.url().page - 1,
                            size: params.url().count
                        }
                    }).success(function (data) {
                        if (data.success) {
                            params.total(data.content.totalElements);
                            $defer.resolve(data.content.content);
                        } else {
                            toaster.pop('error', '错误', data.message);
                        }
                    });
                }
            }
        });
        $scope.$watch('activeBlockchain', function (newValue, oldValue) {
            if (newValue && newValue.channel) {
                $scope.tabelParams.reload();
            }
        }, true);
        $scope.showTransactionActions = function(item) {
            $http.get('/transaction?transactionID=' + item.transactionID).success(function(data){
                if (data.success) {
                    $scope.activeTransaction = data.content;
                }
            });
        };
    }]);
    return app;
});
