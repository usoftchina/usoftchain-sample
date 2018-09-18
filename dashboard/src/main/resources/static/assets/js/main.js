require.config({
    baseUrl: 'assets',
    paths: {
        'src': 'js',
        'lib': 'lib',
        'angular': 'lib/angular/angular.min',
        'ngResource': 'lib/angular/angular-resource.min',
        'ngTable': 'lib/angular/ng-table',
        'ngAnimate': 'lib/angular/angular-animate.min',
        'toaster': 'lib/angular/angular-toaster.min',
        'ui.router': 'lib/angular/angular-ui-router.min',
        'bootstrap': 'lib/bootstrap/js/bootstrap.min',
        'jquery': 'lib/jquery/jquery.min',
        'mousewheel': 'lib/jquery/jquery.mousewheel.min',
        'clearmin': 'js/clearmin.min'
    },
    shim: {
        'angular': {
            'exports': 'angular'
        },
        'ngResource': {
            'exports': 'ngResource',
            'deps': ['angular']
        },
        'ngAnimate': ['angular'],
        'toaster': ['angular', 'ngAnimate'],
        'ngTable': {
            'exports': 'ngTable',
            'deps': ['angular']
        },
        'ui.router': ['angular'],
        'ui-bootstrap': ['angular'],
        'bootstrap': ['jquery'],
        'mousewheel': ['jquery'],
        'clearmin': ['mousewheel']
    }
});
require(['bootstrap', 'clearmin', 'src/app'], function (bootstrap, clearmin, app) {
    app.init();
});