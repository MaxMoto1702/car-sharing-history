(function () {
    'use strict';
    console.log('Start init Event Module');

    var module = angular.module('event', [
        'carSharingHistory',
        'ui.bootstrap'
    ]);
    modules.push(module.name);

    module.service('EventService', function EventService($q, $http, $log) {
        $log.info(this);
        this.list = list;
        this.get = get;

        function list() {
            console.log('Get events');
            return $http.get('/events').then(function (response) {
                console.log(response.data._embedded.events);
                return response.data._embedded.events;
            });
        }

        function get(id) {
            return $http.get('/events/' + id).then(function (response) {
                return response.data;
            })
        }
    });

    module.controller('EventListController', function EventListController(EventService, $state) {
        console.log(this);
        var vm = this;
        EventService.list().then(function (events) {
            vm.events = events;
        });

        vm.open = function (event) {
            $state.go('^.show', {id: event.id});
        }
    });

    module.controller('EventShowController', function EventListController(EventService, $state) {
        console.log(this);
        var vm = this;

    });

    module.component('eventList', {
        templateUrl: 'event-list.html',
        controller: 'EventListController',
        controllerAs: 'vm'
    });

    module.component('eventShow', {
        templateUrl: 'event-show.html',
        controller: 'EventShowController',
        controllerAs: 'vm',
        bindings: {
            'event': '<'
        }
    });

    module.config(function config($stateProvider) {
        console.log('Config Event Module');
        $stateProvider
            .state({
                name: 'events',
                url: '/events',
                abstract: true,
                template: '<ui-view></ui-view>'
            })
            .state({
                name: 'events.list',
                url: '',
                component: 'eventList'
            })
            .state({
                name: 'events.show',
                url: '/{id}',
                component: 'eventShow',
                resolve: {
                    'event': function (EventService, $stateParams) {
                        return EventService.get($stateParams.id);
                    }
                }
            });
    });

    module.run(function run() {
        console.log("Start Event Module");
    });

    console.log('Finish init Event Module');
})();