(function () {
    'use strict';
    console.log('Start init Finance (Main) Module');

    var app = angular.module('carSharingHistory', [
        'ui.router',
        'ui.bootstrap'
    ]);
    modules.push(app.name);

    app.config(config);

    app.run(run);

    function config($urlRouterProvider) {
        console.log('Config Car Sharing History (Main) Module');
        $urlRouterProvider.otherwise('/');
    }

    function run($uiRouter) {
        console.log('Run Finance (Main) Module');
        if (window['ui-router-visualizer']) {
            var Visualizer = window['ui-router-visualizer'].Visualizer;
            var pluginInstance = $uiRouter.plugin(Visualizer);
        }
    }

    console.log('Finish init Car Sharing History (Main) Module');
})();