System.register(['angular2/core', '../views/name-id-list-box.component'], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var core_1, name_id_list_box_component_1;
    var PageByProjectComponent;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (name_id_list_box_component_1_1) {
                name_id_list_box_component_1 = name_id_list_box_component_1_1;
            }],
        execute: function() {
            PageByProjectComponent = (function () {
                function PageByProjectComponent() {
                } // ctor
                PageByProjectComponent.prototype.ngOnInit = function () {
                    /*
                     let id = +this._routeParams.get('id');
                     this._heroService.getHero(id)
                     .then(hero => this.hero = hero);
                     */
                };
                PageByProjectComponent = __decorate([
                    core_1.Component({
                        selector: 'page-by-project',
                        directives: [name_id_list_box_component_1.NameIdListBoxComponent],
                        template: "\n            <form>\n                <fieldset class=\"well the-fieldset\">\n                <legend class=\"the-legend\">Search Criteria</legend>\n                \n                <name-id-list-box></name-id-list-box>\n                \n                </fieldset>\n            </form>\n    " // end template
                    })
                ], PageByProjectComponent);
                return PageByProjectComponent;
            }());
            exports_1("PageByProjectComponent", PageByProjectComponent);
        }
    }
});
//# sourceMappingURL=page-by-project.component.js.map