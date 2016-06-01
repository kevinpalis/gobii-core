///<reference path="../../../../../../node_modules/angular2/typings/browser.d.ts"/>

// Angular Imports
import {Component} from "angular2/core";
import {HTTP_PROVIDERS} from "angular2/http";
import {ExportFormatComponent} from "../views/export-format.component";
import {SearchCriteriaBySamplesComponent} from "./page-by-samples.component";
import {PageByProjectComponent} from "./page-by-project.component";
import {PrincipleInvestigatorService} from "../services/app/principle-investigator.service";
import {NameIdListService} from "../services/app/name-id-list.service";
import {AuthenticationService} from "../services/core/authentication.service";
// import { RouteConfig, ROUTER_DIRECTIVES, ROUTER_PROVIDERS } from 'angular2/router';

// GOBii Imports


@Component({
    selector: 'extractor-root',
    styleUrls: ['/extractor-ui.css'],
    directives: [ExportFormatComponent,
        SearchCriteriaBySamplesComponent,
        PageByProjectComponent],
    providers: [
        HTTP_PROVIDERS,
        AuthenticationService,
        NameIdListService,
        PrincipleInvestigatorService
    ],
    template: `
        <div class = "panel panel-default">
        
           <div class = "panel-heading">
              <h1 class = "panel-title">GOBii Extractor</h1>
           </div>
           
            <div class="container-fluid">
            
                <div class="row">
        
                    <div class="col-md-3"> 
        
                        <div class="sidebar-nav">
                          <div class="navbar navbar-default" role="navigation">
                            <div class="navbar-header">Search Options</div>
                            <div class="navbar-collapse collapse sidebar-navbar-collapse">
                              <ul class="nav navbar-nav">
                                <li><a href="#" class="active">By Project</a></li>
                                <li><a href="#">By Sample</a></li>
                                <li><a href="#">By Marker/Haplotype</a></li>
                                <li><a href="#">By Platform</a></li>
                                <li><a href="#">By Map Location</a></li>
                              </ul>
                            </div><!--/.nav-collapse -->
                          </div>
                        </div>			
                    
                    </div>  <!-- outer grid column 1-->
                    
                    <div class="col-md-5"> 
                          
                            <div class="row">
                                <div class="col-md-12">							
                                    <page-by-project></page-by-project>
                                </div>
                            </div> <!-- inner grid row 1 -->
                            
                            <div class="row">
                                <div class="col-md-12">
                                    <export-format></export-format>
                                </div>
                            </div> <!-- inner grid row 2 -->
        
                            <div class="row">
                                <div class="col-md-12">
                                    SEARCH BUTTON GOES HERE
                                </div>
                            </div> <!-- inner grid row 3 -->
        
        
                         
                    </div>  <!-- outer grid column 2 (inner grid)-->
                    
                    <div class="col-md-4">
                        FILTERS GO HERE
                    </div>  <!-- outer grid column 3-->
                    
                </div> <!-- .row of outer grid -->
                
            </div> 
	` // end template
}) // @Component

/*
 @RouteConfig([

 {
 path: '/dashboard',
 name: 'Dashboard',
 component: DashboardComponent,
 useAsDefault: true
 },
 {
 path: '/heroes',
 name: 'Heroes',
 component: HeroesComponent
 },
 {
 path: '/detail/:id',
 name: 'HeroDetail',
 component: HeroDetailComponent
 },

 ]) // @RouteConfig
 */

export class ExtractorRoot {
    title = 'Tour of Heroes';
}

