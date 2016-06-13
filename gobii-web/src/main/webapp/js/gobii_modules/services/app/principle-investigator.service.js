System.register(["@angular/core", "./../core/dto-request.service", "rxjs/Observable", "./dto-request-item-nameids", "../../model/type-entity", "../../model/type-process"], function(exports_1, context_1) {
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
    var core_1, dto_request_service_1, Observable_1, dto_request_item_nameids_1, type_entity_1, type_process_1;
    var PrincipleInvestigatorService;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (dto_request_service_1_1) {
                dto_request_service_1 = dto_request_service_1_1;
            },
            function (Observable_1_1) {
                Observable_1 = Observable_1_1;
            },
            function (dto_request_item_nameids_1_1) {
                dto_request_item_nameids_1 = dto_request_item_nameids_1_1;
            },
            function (type_entity_1_1) {
                type_entity_1 = type_entity_1_1;
            },
            function (type_process_1_1) {
                type_process_1 = type_process_1_1;
            }],
        execute: function() {
            PrincipleInvestigatorService = (function () {
                function PrincipleInvestigatorService(_nameIdListService) {
                    this._nameIdListService = _nameIdListService;
                    this._nameIdListService = _nameIdListService;
                } // ctor
                PrincipleInvestigatorService.prototype.getPiNameIds = function () {
                    var _this = this;
                    // return Observable.create(observer => {
                    //         observer.next([new NameId(1, 'from pisvc 1'),
                    //             new NameId(2, 'from pisvc 2')]);
                    //         observer.complete();
                    //     }
                    // );
                    return Observable_1.Observable.create(function (observer) {
                        _this._nameIdListService.getItemList(new dto_request_item_nameids_1.DtoRequestItemNameIds(type_process_1.ProcessType.READ, type_entity_1.EntityType.DataSetNames))
                            .subscribe(function (nameIds) {
                            observer.next(nameIds);
                            observer.complete();
                        });
                    });
                }; // getPiNameIds()
                PrincipleInvestigatorService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [dto_request_service_1.DtoRequestService])
                ], PrincipleInvestigatorService);
                return PrincipleInvestigatorService;
            }());
            exports_1("PrincipleInvestigatorService", PrincipleInvestigatorService);
        }
    }
});
//# sourceMappingURL=principle-investigator.service.js.map