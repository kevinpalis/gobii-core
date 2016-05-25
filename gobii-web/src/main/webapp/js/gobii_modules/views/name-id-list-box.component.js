System.register(['angular2/core', '../services/app/principle-investigator.service'], function(exports_1, context_1) {
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
    var core_1, principle_investigator_service_1;
    var NameIdListBoxComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (principle_investigator_service_1_1) {
                principle_investigator_service_1 = principle_investigator_service_1_1;
            }],
        execute: function() {
            NameIdListBoxComponent = (function () {
                // TODO: Use interface so this component can be reused?
                function NameIdListBoxComponent(_principleInvestigatorService) {
                    this._principleInvestigatorService = _principleInvestigatorService;
                } // ctor
                NameIdListBoxComponent.prototype.ngOnInit = function () {
                    this.nameIds = this._principleInvestigatorService.getNameIds();
                    //this.nameIds = [new NameId(1,"foo"), new NameId(2,"bar")];
                    /*
                            let id = +this._routeParams.get('id');
                            this._heroService.getHero(id)
                              .then(hero => this.hero = hero);
                    */
                }; // ngOnInit
                NameIdListBoxComponent = __decorate([
                    core_1.Component({
                        selector: 'name-id-list-box',
                        //directives: [RADIO_GROUP_DIRECTIVES]
                        template: "<select name=\"principleInvestigators\">\n\t\t\t<option *ngFor=\"#nameId of nameIds\" \n\t\t\t\tvalue={{nameId.id}}>{{nameId.name}}</option>\n\t\t</select>\n" // end template
                    }), 
                    __metadata('design:paramtypes', [principle_investigator_service_1.PrincipleInvestigatorService])
                ], NameIdListBoxComponent);
                return NameIdListBoxComponent;
            }());
            exports_1("NameIdListBoxComponent", NameIdListBoxComponent);
        }
    }
});
//# sourceMappingURL=name-id-list-box.component.js.map