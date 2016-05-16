app.controller('StudentTasksController', function ($scope, $http, $window, $compile, $uibModal, $cookies) {
    const SCORES_LIST_ITEM_PREFIX = 'task-list-item-';
    const SCORES_LIST_ELEMENT = document.getElementById('table-scores-list-body');

    $scope.scores = new Map();
    $scope.removeId;


    $scope.toElementId = function (id) {
        return SCORES_LIST_ITEM_PREFIX + id;
    };

    $scope.addScore = function (score) {
        var scoreObj = {'scoreObj': score, 'opened': false, 'fd': null, 'filenameStr': ''};
        $scope.scores.set(score.id, scoreObj);

        var html = '<tr id="' + $scope.toElementId(score.id) + '"></td>';

        // check if description set
        if (score.task.description == null || score.task.description == "")
            html += '<td></td>';
        else
            html += '<td><span role="button" ng-class="getOpenedClass(' + score.id + ')" ng-click="scores.get(' + score.id + ').opened = !scores.get(' + score.id + ').opened""></span></td>';

        html += '<td>' + score.task.name + '</td>' +
            '<td>' + score.task.clazz.name + '</td>' +
            '<td>' + score.task.deadline + '</td>';

        // upload or download icon
        if (score.fileName == null || score.fileName == '') {
            html += '<td><div class="row"> ' +
                '<em class="col-xs-7 text-muted">{{getFilenameStr(' + score.id + ')}}</em>' +
                '<span class="col-xs-2" align="center" input-group-btn"><span class=" btn btn-primary btn-file btn-sm">•••' +
                '<input type="file" onchange="angular.element(this).scope().setFile(' + score.id + ',this.files[0])"/></span></span>' +
                '<span role="button" ng-show="isVisible(' + score.id + ')" class="glyphicon glyphicon-upload col-xs-2" ng-click="uploadFile(' + score.id + ')"></span></div></td>';
        } else {
            html += '<td><div class="row"> ' +
                '<em class="col-xs-7 text-muted">' + score.fileName + '</em>' +
                '<span align="center" role="button" class="glyphicon glyphicon-download col-xs-2" ng-click="downloadFile(' + score.id + ')"></span>' +
                '<span role="button" class="glyphicon glyphicon-remove col-xs-2" ng-click="openRemoveModal(' + score.id + ')"></span></div></td>';
        }

        // check if score already set
        if (score.score == null)
            html += '<td>' + "" + '</td>';
        else if (score.remarks == null || score.remarks == "")
            html += '<td align="center">' + score.score + '</td>';
        else
            html += '<td align="center"><div tooltip-class="customClass" tooltip-placement="top" uib-tooltip="Commentaar:\n' + score.remarks + '">' + score.score + '</div></td>';

        // task description
        if (score.task.description != null && score.task.description != "")
            html += '<tr></tr><tr ng-show="scores.get(' + score.id + ').opened"><td></td><td colspan="5"><pre>' + score.task.description + '</pre></td></tr>';

        html += '</tr>';
        var element = document.createElement('tr');
        SCORES_LIST_ELEMENT.appendChild(element);
        element.outerHTML = html;
    };

    // Update the Angular controls that have been added in the HTML
    $scope.refresh = function () {
        $compile(SCORES_LIST_ELEMENT)($scope);
    };

    $scope.getOpenedClass = function (id) {
        if ($scope.scores.get(id).opened)
            return 'glyphicon glyphicon-chevron-down';
        else
            return 'glyphicon glyphicon-chevron-right'
    };

    $scope.downloadFile = function (id) {
        $scope.execDownload('http://localhost:8080/task/download/' + id,
            $scope.scores.get(id).scoreObj.fileName);
    };

    $scope.openRemoveModal = function (id) {
        var modalInstance = $uibModal.open({
            animation: true,
            templateUrl: 'views/panels/remove_modal.html',
            controller: 'RemoveUploadModalInstanceCtrl',
            resolve: {}
        });
        $scope.removeId = id;
        modalInstance.result.then(function () {
            $scope.removeUploadBackend($scope.removeId)
        });
    };

    $scope.removeUploadBackend = function (id) {
        $http({
            method: 'PUT', url: 'http://localhost:8080/task/score/' + id + '/reset',
            headers: {'X-auth': $cookies.get("auth")}
        }).success(function (response) {
            $window.location.reload(true);
            $scope.createAlertCookie('Upload verwijderd.');
        });
    };


    $scope.setFile = function (id, file) {
        // if 'cancel' clicked
        if (typeof file === 'undefined') {
            $scope.scores.get(id).fd = null;
            $scope.$apply(function () {
                $scope.scores.get(id).filenameStr = '';
            });
            return;
        }

        // if file selected
        var fd = new FormData();
        fd = new FormData();
        fd.append("file", file);
        $scope.scores.get(id).fd = fd;
        $scope.$apply(function () {
            $scope.scores.get(id).filenameStr = file.name;
        });
    };

    $scope.getFilenameStr = function (id) {
        return $scope.scores.get(id).filenameStr;
    };

    $scope.isVisible = function (id) {
        return $scope.scores.get(id).fd != null;
    };


    $scope.uploadFile = function (id) {
        $http({
            method: 'POST', url: 'http://localhost:8080/task/upload/' + id,
            data: $scope.scores.get(id).fd,
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined, 'X-auth': $cookies.get("auth")}
        }).success(function (response) {
            $window.location.reload(true);
            $scope.createAlertCookie('Uploaden gelukt.');
        }).error(function (response) {
            console.log("error: " + JSON.stringify(response));
        });
    };


    // ACTUAL ACTIONS ON LOADED PAGE
    // get all tasks associated with current student
    $http({
        method: 'GET', url: 'http://localhost:8080/score/id/' + $cookies.getObject("user").id + '/tasks',
        headers: {'X-auth': $cookies.get("auth")}
    }).success(function (response) {
        response.forEach(function (item) {
            $scope.addScore(item);
        });
        $scope.refresh();
    });
});


app.controller('RemoveUploadModalInstanceCtrl', function ($scope, $uibModalInstance) {
    $scope.modalObject = "upload";

    $scope.ok = function () {
        $uibModalInstance.close();
    };
    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
});