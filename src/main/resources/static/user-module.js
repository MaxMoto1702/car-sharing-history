(function () {
    'use strict';
    console.log('Start init User Module');

    var module = angular.module('user', [
        'carSharingHistory',
        'ui.bootstrap'
    ]);
    modules.push(module.name);

    module.service('UserService', function UserService($q, $http, $log) {
        $log.info(this);
        this.list = list;
        this.get = get;

        function list() {
            console.log('Get users');
            return $http.get('/users').then(function (response) {
                console.log(response.data._embedded.users);
                return response.data._embedded.users;
            });
        }

        function get(id) {
            return $http.get('/users/' + id).then(function (response) {
                return response.data;
            })
        }
    });

    module.controller('UserListController', function UserListController(UserService, $state) {
        console.log(this);
        var vm = this;
        UserService.list().then(function (users) {
            vm.users = users;
        });
    });

    module.component('userList', {
        templateUrl: 'user-list.html',
        controller: 'UserListController',
        controllerAs: 'vm'
    });

    module.config(function config($stateProvider) {
        console.log('Config User Module');
        $stateProvider
            .state({
                name: 'users',
                url: '/users',
                abstract: true,
                template: '<ui-view></ui-view>'
            })
            .state({
                name: 'users.list',
                url: '',
                component: 'userList'
            });
    });

    module.run(function run() {
        console.log("Start User Module");
    });

    console.log('Finish init User Module');
})();