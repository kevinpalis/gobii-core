///<reference path="../../../../../../typings/index.d.ts"/>

import {Component} from "@angular/core";
import {HTTP_PROVIDERS} from "@angular/http";
import {ExportFormatComponent} from "../views/export-format.component";
import {DtoRequestService} from "../services/core/dto-request.service";
import {AuthenticationService} from "../services/core/authentication.service";
import {ContactsListBoxComponent} from "../views/contacts-list-box.component";
import {ProjectListBoxComponent} from "../views/project-list-box.component";
import {ExperimentListBoxComponent} from "../views/experiment-list-box.component";
import {DataSetCheckListBoxComponent} from "../views/dataset-checklist-box.component";
import {GobiiDataSetExtract} from "../model/extractor-instructions/data-set-extract";
import {CriteriaDisplayComponent} from "../views/criteria-display.component";
import {ProcessType} from "../model/type-process";
import {CheckBoxEvent} from "../model/event-checkbox";
import {ServerConfig} from "../model/server-config";
import {CropsListBoxComponent} from "../views/crops-list-box.component";
import {UsersListBoxComponent} from "../views/users-list-box.component";
import {NameId} from "../model/name-id";
import {DatasetDetailBoxComponent} from "../views/dataset-detail.component";
import {ExperimentDetailBoxComponent} from "../views/experiment-detail-component";
import {GobiiFileType} from "../model/type-gobii-file";
import  {ExtractorInstructionFilesDTO} from "../model/extractor-instructions/dto-extractor-instruction-files";
import {GobiiExtractorInstruction} from  "../model/extractor-instructions/gobii-extractor-instruction"
import {DtoRequestItemDataSet} from "../services/app/dto-request-item-dataset";
import {DtoRequestItemExtractorSubmission} from "../services/app/dto-request-item-extractor-submission";


// import { RouteConfig, ROUTER_DIRECTIVES, ROUTER_PROVIDERS } from 'angular2/router';

// GOBii Imports


@Component({
    selector: 'extractor-root',
    directives: [ExportFormatComponent,
        ContactsListBoxComponent,
        ProjectListBoxComponent,
        ExperimentListBoxComponent,
        DataSetCheckListBoxComponent,
        CriteriaDisplayComponent,
        CropsListBoxComponent,
        UsersListBoxComponent,
        DatasetDetailBoxComponent,
        ExperimentDetailBoxComponent],
    styleUrls: ['/extractor-ui.css'],
    providers: [
        HTTP_PROVIDERS,
        AuthenticationService,
        DtoRequestService
    ],
    template: `<div class = "panel panel-default">
        
           <div class = "panel-heading">
              <h1 class = "panel-title">GOBii Extractor</h1>
           </div>
           
            <div class="container-fluid">
            
                <div class="row">
                
                    <div class="col-md-4">
                        <fieldset class="well the-fieldset">
                        <legend class="the-legend">Crop</legend>
                        <crops-list-box (onServerSelected)="handleServerSelected($event)"></crops-list-box>
                        </fieldset>
                        
                        <fieldset class="well the-fieldset">
                        <legend class="the-legend">Submit As</legend>
                        <users-list-box (onUserSelected)="handleUserSelected($event)"></users-list-box>
                        </fieldset>
                        
                        <div class="col-md-12">
                            <export-format></export-format>
                        </div>

                        
                        
                    </div>  <!-- outer grid column 1-->
                
                
                
                    <div class="col-md-4"> 
                        <fieldset class="well the-fieldset">
                        <legend class="the-legend">Principle Investigator</legend>
                        <contacts-list-box (onContactSelected)="handleContactSelected($event)"></contacts-list-box>
                        </fieldset>
                        
                        <fieldset class="well the-fieldset">
                        <legend class="the-legend">Projects</legend>
                        <project-list-box [primaryInvestigatorId] = "selectedContactId" (onProjectSelected)="handleProjectSelected($event)" ></project-list-box>
                        </fieldset>
                        
                        <fieldset class="well the-fieldset">
                        <legend class="the-legend">Experiments</legend>
                        <experiment-list-box [projectId] = "selectedProjectId" (onExperimentSelected)="handleExperimentSelected($event)"></experiment-list-box>
                        </fieldset>
                        
                        <fieldset class="well the-fieldset">
                        <legend class="the-legend">Data Sets</legend>
                        <dataset-checklist-box [experimentId] = "selectedExperimentId" 
                            (onItemChecked)="handleCheckedDataSetItem($event)"
                            (onItemSelected)="handleDataSetDetailSelected($event)">
                        </dataset-checklist-box>
                        </fieldset>
                        
                    </div>  <!-- outer grid column 2-->
                    <div class="col-md-4">
                     
                            <fieldset [hidden]="!displayDataSetDetail" class="well the-fieldset" style="vertical-align: top;">
                                <legend class="the-legend">Data Set</legend>
                                <dataset-detail-box [dataSetId] = "selectedDataSetDetailId"></dataset-detail-box>
                            </fieldset>
                     
                            <fieldset [hidden]="!displayExperimentDetail" class="well the-fieldset" style="vertical-align: top;">
                                <legend class="the-legend">Experiment</legend>
                                <experiment-detail-box [experimentId] = "selectedExperimentDetailId"></experiment-detail-box>
                            </fieldset>
                     
                           
                            <fieldset class="well the-fieldset" style="vertical-align: bottom;">
                            <legend class="the-legend">Extract Critiera</legend>
                            <criteria-display [gobiiDatasetExtracts] = "gobiiDatasetExtracts"></criteria-display>
                            </fieldset>
                            
                            <form>
                                <input type="button" value="Submit" (click)="handleExtractSubmission()" >
                            </form>
                            
       
                    </div>  <!-- outer grid column 3 (inner grid)-->
                                        
                </div> <!-- .row of outer grid -->
                
                    <div class="row"><!-- begin .row 2 of outer grid-->
                        <div class="col-md-3"><!-- begin column 1 of outer grid -->
                         
                         </div><!-- end column 1 of outer grid -->
                    
                    </div><!-- end .row 2 of outer grid-->
                
            </div>` // end template
}) // @Component

export class ExtractorRoot {
    title = 'Gobii Web';


    private gobiiDatasetExtracts:GobiiDataSetExtract[] = [];


    constructor(private _dtoRequestServiceExtractorFile:DtoRequestService<ExtractorInstructionFilesDTO>) {
        let foo = "foo";
    }

    private selectedContactId:string = "1";

    private handleContactSelected(arg) {
        this.selectedContactId = arg;
        //console.log("selected contact id:" + arg);
    }

    private selectedProjectId:string = "0";

    private handleProjectSelected(arg) {
        this.selectedProjectId = arg;
        this.displayExperimentDetail = false;
        this.displayDataSetDetail = false;
    }

    private displayExperimentDetail:boolean = false;
    private selectedExperimentId:string = "0";
    private selectedExperimentDetailId:string = "0";

    private handleExperimentSelected(arg) {
        this.selectedExperimentId = arg;
        this.selectedExperimentDetailId = arg;
        this.displayExperimentDetail = true;

        //console.log("selected contact id:" + arg);
    }

    private displayDataSetDetail:boolean = false;
    private selectedDataSetDetailId:number;

    private handleDataSetDetailSelected(arg) {
        this.selectedDataSetDetailId = arg;
        this.selectedExperimentDetailId = undefined;
        this.displayDataSetDetail = true;
    }


    private selectedServerConfig:ServerConfig;

    private handleServerSelected(arg) {
        this.selectedServerConfig = arg;
        let currentPath = window.location.pathname;
        let currentPage:string = currentPath.substr(currentPath.lastIndexOf('/') + 1, currentPath.length);
        let newDestination = "http://"
            + this.selectedServerConfig.domain
            + ":"
            + this.selectedServerConfig.port
            + this.selectedServerConfig.contextRoot
            + currentPage;
//        console.log(newDestination);
        window.location.href = newDestination;
    } // handleServerSelected()

    private selectedUser:NameId

    private handleUserSelected(arg) {

    }

    private handleCheckedDataSetItem(arg:CheckBoxEvent) {
        if (ProcessType.CREATE == arg.processType) {
            this.gobiiDatasetExtracts.push(new GobiiDataSetExtract(GobiiFileType.GENERIC,
                false,
                Number(arg.id),
                arg.name));
        } else {

            this.gobiiDatasetExtracts =
                this.gobiiDatasetExtracts
                    .filter((item:GobiiDataSetExtract) => {
                        return item.getDataSetId() != Number(arg.id)
                    });
        } // if-else we're adding
    }

    private handleExtractSubmission() {

        let gobiiExtractorInstructions:GobiiExtractorInstruction[] = [];

        gobiiExtractorInstructions.push(
            new GobiiExtractorInstruction(
                "foordir",
                this.gobiiDatasetExtracts,
                Number(this.selectedContactId),
                null)
        );


        let date:Date = new Date();
        let fileName:string = "extractor_"
            + date.getFullYear()
            + "_"
            + date.getMonth()
            + "_"
            + date.getDay()
            + "_"
            + date.getHours()
            + "_"
            + date.getMinutes()
            + "_"
            + date.getSeconds();
        let extractorInstructionFilesDTORequest:ExtractorInstructionFilesDTO =
            new ExtractorInstructionFilesDTO(gobiiExtractorInstructions,
                fileName,
                ProcessType.CREATE);

        let extractorInstructionFilesDTOResponse:ExtractorInstructionFilesDTO = null;
        this._dtoRequestServiceExtractorFile.getResult(new DtoRequestItemExtractorSubmission(extractorInstructionFilesDTORequest))
            .subscribe(extractorInstructionFilesDTO => {
                    extractorInstructionFilesDTOResponse = extractorInstructionFilesDTO;
                },
                dtoHeaderResponse => {
                    dtoHeaderResponse.statusMessages.forEach(m => console.log(m.message))
                });

    }

}

