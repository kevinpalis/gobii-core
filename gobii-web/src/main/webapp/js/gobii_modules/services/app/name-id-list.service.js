System.register(['angular2/core', 'angular2/http', 'rxjs/Observable', 'rxjs/add/operator/map', 'rxjs/add/operator/catch'], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, http_1, Observable_1;
    var NameIdListService;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (http_1_1) {
                http_1 = http_1_1;
            },
            function (Observable_1_1) {
                Observable_1 = Observable_1_1;
            },
            function (_1) {},
            function (_2) {}],
        execute: function() {
            NameIdListService = (function () {
                function NameIdListService(_http) {
                    this._http = _http;
                }
                NameIdListService.prototype.getNameIds = function () {
                    var requestBody = JSON.stringify({
                        "processType": "READ",
                        "dtoHeaderAuth": { "userName": null, "password": null, "token": null },
                        "dtoHeaderResponse": { "succeeded": true, "statusMessages": [] },
                        "entityType": "DBTABLE",
                        "entityName": "datasetnames",
                        "namesById": {},
                        "filter": null
                    });
                    var headers = new http_1.Headers();
                    headers.append('Content-Type', 'application/json');
                    headers.append('Accept', 'application/json');
                    return this
                        ._http
                        .post("load/nameidlist", requestBody, { headers: headers })
                        .map(this.handleResponse)
                        .catch(this.handleError);
                };
                NameIdListService.prototype.handleResponse = function (response) {
                    if (response.status < 200 || response.status > 300) {
                        throw new Error('Bad response status: ' + response.status);
                    }
                    var payload = response.json;
                    console.log(payload);
                    return [];
                };
                NameIdListService.prototype.handleError = function (error) {
                    var errorMessage = error.message;
                    console.error(errorMessage);
                    return Observable_1.Observable.throw(errorMessage);
                };
                NameIdListService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [http_1.Http])
                ], NameIdListService);
                return NameIdListService;
            }());
            exports_1("NameIdListService", NameIdListService);
        }
    }
});
//# sourceMappingURL=name-id-list.service.js.map