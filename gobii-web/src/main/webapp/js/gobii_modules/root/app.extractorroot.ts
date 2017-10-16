///<reference path="../../../../../../typings/index.d.ts"/>
import {Component, OnInit} from "@angular/core";
import {DtoRequestService} from "../services/core/dto-request.service";
import {GobiiDataSetExtract} from "../model/extractor-instructions/data-set-extract";
import {ProcessType} from "../model/type-process";
import {GobiiFileItem} from "../model/gobii-file-item";
import {ServerConfig} from "../model/server-config";
import {EntitySubType, EntityType} from "../model/type-entity";
import {DtoRequestItemServerConfigs} from "../services/app/dto-request-item-serverconfigs";
import {GobiiExtractFilterType} from "../model/type-extractor-filter";
import {CvFilterType} from "../model/cv-filter-type";
import {ExtractorItemType} from "../model/type-extractor-item";
import {GobiiExtractFormat} from "../model/type-extract-format";
import {HeaderStatusMessage} from "../model/dto-header-status-message";
import {FileItemParams} from "../model/name-id-request-params";
import {FileName} from "../model/file_name";
import {Contact} from "../model/contact";
import {ContactSearchType, DtoRequestItemContact} from "../services/app/dto-request-item-contact";
import {AuthenticationService} from "../services/core/authentication.service";
import {NameIdLabelType} from "../model/name-id-label-type";
import {StatusLevel} from "../model/type-status-level";
import {Store} from "@ngrx/store";
import * as fromRoot from '../store/reducers';
import * as fileItemAction from '../store/actions/fileitem-action';
import * as historyAction from '../store/actions/history-action';
import {NameIdFilterParamTypes} from "../model/type-nameid-filter-params";
import {FileItemService} from "../services/core/file-item-service";
import {Observable} from "rxjs/Observable";
import {InstructionSubmissionService} from "../services/core/instruction-submission-service";
import {GobiiSampleListType} from "../model/type-extractor-sample-list";

// import { RouteConfig, ROUTER_DIRECTIVES, ROUTER_PROVIDERS } from 'angular2/router';

// GOBii Imports


@Component({
    selector: 'extractor-root',
    styleUrls: ['/extractor-ui.css'],
    template: `
        <div class="panel panel-default">

            <div class="panel-heading">
                <img src="images/gobii_logo.png" alt="GOBii Project"/>

                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title">Connected to {{currentStatus}}</h3>
                    </div>
                    <div class="panel-body">

                        <div class="col-md-1">

                            <crops-list-box
                                    [serverConfigList]="serverConfigList"
                                    [selectedServerConfig]="selectedServerConfig"
                                    (onServerSelected)="handleServerSelected($event)"></crops-list-box>
                        </div>

                        <div class="col-md-5">
                            <export-type
                                    (onExportTypeSelected)="handleExportTypeSelected($event)"></export-type>
                        </div>


                        <div class="col-md-4">
                            <div class="well">
                                <table>
                                    <tr>
                                        <td colspan="2">
                                            <export-format
                                                    [fileFormat$]="selectedExtractFormat$"
                                                    [gobiiExtractFilterType]="gobiiExtractFilterType">
                                            </export-format>
                                        </td>
                                        <td style="vertical-align: top;">
                                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                            <name-id-list-box
                                                    [gobiiExtractFilterType]="gobiiExtractFilterType"
                                                    [fileItems$]="fileItemsMapsets$">
                                            </name-id-list-box>
                                        </td>
                                    </tr>
                                </table>

                            </div>
                        </div>


                        <div class="col-md-2">
                            <p style="text-align: right; font-weight: bold;">{{loggedInUser}}</p>
                        </div>

                    </div> <!-- panel body -->
                </div> <!-- panel primary -->

            </div>

            <div class="container-fluid">

                <div class="row">

                    <div class="col-md-4">

                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title">Filters</h3>
                            </div>
                            <div class="panel-body">

                                <div *ngIf="displaySelectorPi">
                                    <label class="the-label">Principle Investigator:</label><BR>
                                    <name-id-list-box
                                            [gobiiExtractFilterType]="gobiiExtractFilterType"
                                            [fileItems$]="fileItemsContactsPI$">
                                    </name-id-list-box>

                                </div>

                                <div *ngIf="displaySelectorProjectForPi">
                                    <BR>
                                    <BR>
                                    <label class="the-label">PI's Project:</label><BR>
                                    <name-id-list-box
                                            [gobiiExtractFilterType]="gobiiExtractFilterType"
                                            [fileItems$]="fileItemsProjectsForPi$">
                                    </name-id-list-box>
                                </div>

                                <div *ngIf="displaySelectorForAllProjects">
                                    <BR>
                                    <BR>
                                    <label class="the-label">Projects:</label><BR>
                                    <name-id-list-box
                                            [gobiiExtractFilterType]="gobiiExtractFilterType"
                                            [fileItems$]="fileItemsProjects$">
                                    </name-id-list-box>
                                </div>

                                <div *ngIf="displaySelectorDataType">
                                    <BR>
                                    <BR>
                                    <label class="the-label">Dataset Types:</label><BR>
                                    <name-id-list-box
                                            [gobiiExtractFilterType]="gobiiExtractFilterType"
                                            [fileItems$]="fileItemsDatasetTypes$">
                                    </name-id-list-box>
                                </div>


                                <div *ngIf="displaySelectorExperiment">
                                    <BR>
                                    <BR>
                                    <label class="the-label">Project's Experiment:</label><BR>
                                    <name-id-list-box
                                            [gobiiExtractFilterType]="gobiiExtractFilterType"
                                            [fileItems$]="fileItemsExperiments$">
                                    </name-id-list-box>

                                </div>

                                <div *ngIf="displaySelectorPlatform">
                                    <BR>
                                    <BR>
                                    <label class="the-label">Platforms:</label><BR>
                                    <checklist-box
                                            [gobiiFileItems$]="fileItemsPlatforms$"
                                            [gobiiExtractFilterType]="gobiiExtractFilterType"
                                            [retainHistory]="true">
                                    </checklist-box>
                                </div>


                                <div *ngIf="displayAvailableDatasets">
                                    <BR>
                                    <BR>
                                    <label class="the-label">Experiment's Data Sets</label><BR>
                                    <checklist-box
                                            [gobiiFileItems$]="fileItemsDatasets$"
                                            [gobiiExtractFilterType]="gobiiExtractFilterType"
                                            [retainHistory]="true"
                                            (onError)="handleHeaderStatusMessage($event)">
                                    </checklist-box>
                                    <!--<dataset-checklist-box-->
                                    <!--[gobiiExtractFilterType]="gobiiExtractFilterType"-->
                                    <!--[experimentId]="selectedExperimentId"-->
                                    <!--(onAddStatusMessage)="handleHeaderStatusMessage($event)">-->
                                    <!--</dataset-checklist-box>-->
                                </div>
                            </div> <!-- panel body -->
                        </div> <!-- panel primary -->


                        <div *ngIf="displaySampleListTypeSelector">
                            <div class="panel panel-primary">
                                <div class="panel-heading">
                                    <h3 class="panel-title">Included Samples</h3>
                                </div>
                                <div class="panel-body">
                                    <sample-list-type
                                            [gobiiExtractFilterType]="gobiiExtractFilterType"
                                            (onHeaderStatusMessage)="handleHeaderStatusMessage($event)">
                                    </sample-list-type>
                                    <hr style="width: 100%; color: black; height: 1px; background-color:black;"/>
                                    <sample-marker-box
                                            [gobiiExtractFilterType]="gobiiExtractFilterType"
                                            (onSampleMarkerError)="handleHeaderStatusMessage($event)">
                                    </sample-marker-box>
                                </div> <!-- panel body -->
                            </div> <!-- panel primary -->
                        </div>

                        <div *ngIf="displaySampleMarkerBox">
                            <div class="panel panel-primary">
                                <div class="panel-heading">
                                    <h3 class="panel-title">Included Markers</h3>
                                </div>
                                <div class="panel-body">
                                    <sample-marker-box
                                            [gobiiExtractFilterType]="gobiiExtractFilterType"
                                            (onSampleMarkerError)="handleHeaderStatusMessage($event)">
                                    </sample-marker-box>
                                </div> <!-- panel body -->
                            </div> <!-- panel primary -->
                        </div>

                    </div>  <!-- outer grid column 1-->


                    <div class="col-md-4">

                        <div class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title">Extraction Criteria</h3>
                            </div>
                            <div class="panel-body">
                                <status-display-tree
                                        [fileItemEventChange]="treeFileItemEvent"
                                        [gobiiExtractFilterTypeEvent]="gobiiExtractFilterType"
                                        (onAddMessage)="handleHeaderStatusMessage($event)">
                                </status-display-tree>

                                <BR>

                                <button type="submit"
                                        [class]="submitButtonStyle"
                                        (mouseenter)="handleOnMouseOverSubmit($event,true)"
                                        (mouseleave)="handleOnMouseOverSubmit($event,false)"
                                        (click)="handleExtractSubmission()">Submit
                                </button>

                                <button type="clear"
                                        [class]="clearButtonStyle"
                                        (click)="handleClearTree()">Clear
                                </button>

                            </div> <!-- panel body -->
                        </div> <!-- panel primary -->

                    </div>  <!-- outer grid column 2-->


                    <div class="col-md-4">


                        <div>
                            <div class="panel panel-primary">
                                <div class="panel-heading">
                                    <h3 class="panel-title">Status Messages</h3>
                                </div>
                                <div class="panel-body">
                                    <status-display [messages$]="messages$"></status-display>
                                    <BR>
                                    <button type="clear"
                                            class="btn btn-primary"
                                            (click)="handleClearMessages()">Clear
                                    </button>
                                </div> <!-- panel body -->

                            </div> <!-- panel primary -->
                        </div>


                    </div>  <!-- outer grid column 3 (inner grid)-->

                </div> <!-- .row of outer grid -->

                <div class="row"><!-- begin .row 2 of outer grid-->
                    <div class="col-md-3"><!-- begin column 1 of outer grid -->

                    </div><!-- end column 1 of outer grid -->

                </div><!-- end .row 2 of outer grid-->

            </div>` // end template
}) // @Component

export class ExtractorRoot implements OnInit {
    title = 'Gobii Web';


    // *** You cannot use an Enum directly as a template type parameter, so we need
    //     to assign them to properties
    public nameIdRequestParamsDatasetType: FileItemParams;
    public nameIdRequestParamsPlatforms: FileItemParams;
    // ************************************************************************

    // unfiltered
    fileItemsContactsPI$: Observable<GobiiFileItem[]> = this.store.select(fromRoot.getPiContacts);
    fileItemsMapsets$: Observable<GobiiFileItem[]> = this.store.select(fromRoot.getMapsets);
    fileItemsDatasetTypes$: Observable<GobiiFileItem[]> = this.store.select(fromRoot.getCvTermsDataType);
    fileItemsPlatforms$: Observable<GobiiFileItem[]> = this.store.select(fromRoot.getPlatforms);

    // filtered
    fileItemsProjectsForPi$: Observable<GobiiFileItem[]> = this.store.select(fromRoot.getProjectsByPI);
    fileItemsProjects$: Observable<GobiiFileItem[]> = this.store.select(fromRoot.getProjects);
    fileItemsExperiments$: Observable<GobiiFileItem[]> = this.store.select(fromRoot.getExperimentsByProject);
    fileItemsDatasets$: Observable<GobiiFileItem[]> = this.store.select(fromRoot.getDatasetsByExperiment);

    selectedExtractFormat$: Observable<GobiiFileItem> = this.store.select(fromRoot.getSelectedFileFormat);
//    selectedExtractFormat$: Observable<string> = createSelector(getFileItemsState, fromFileItems.getSelectedFileFormat);


    public messages$: Observable<string[]> = this.store.select(fromRoot.getStatusMessages);

    // ************************************************************************

    public treeFileItemEvent: GobiiFileItem;
//    private selectedExportTypeEvent:GobiiExtractFilterType;
    public datasetFileItemEvents: GobiiFileItem[] = [];
    public gobiiDatasetExtracts: GobiiDataSetExtract[] = [];

    public criteriaInvalid: boolean = true;

    public loggedInUser: string = null;


    constructor(private _dtoRequestServiceContact: DtoRequestService<Contact>,
                private _authenticationService: AuthenticationService,
                private _dtoRequestServiceServerConfigs: DtoRequestService<ServerConfig[]>,
                private store: Store<fromRoot.State>,
                private fileItemService: FileItemService,
                private instructionSubmissionService: InstructionSubmissionService) {

    }


    // ****************************************************************
    // ********************************************** SERVER SELECTION
    public selectedServerConfig: ServerConfig;
    public serverConfigList: ServerConfig[];
    public currentStatus: string;

    private initializeServerConfigs() {
        let scope$ = this;
        this._dtoRequestServiceServerConfigs.get(new DtoRequestItemServerConfigs()).subscribe(serverConfigs => {

                if (serverConfigs && ( serverConfigs.length > 0 )) {
                    scope$.serverConfigList = serverConfigs;

                    let serverCrop: String =
                        this._dtoRequestServiceServerConfigs.getGobiiCropType();

                    let gobiiVersion: string = this._dtoRequestServiceServerConfigs.getGobbiiVersion();

                    scope$.selectedServerConfig =
                        scope$.serverConfigList
                            .filter(c => {
                                    return c.crop === serverCrop;
                                }
                            )[0];
                    this.handleExportTypeSelected(GobiiExtractFilterType.WHOLE_DATASET);
//                    scope$.initializeSubmissionContact();
                    scope$.currentStatus = "GOBII Server " + gobiiVersion;
                    scope$.handleAddMessage("Connected to crop config: " + scope$.selectedServerConfig.crop);

                } else {
                    scope$.serverConfigList = [new ServerConfig("<ERROR NO SERVERS>", "<ERROR>", "<ERROR>", 0, "")];
                }
            },
            dtoHeaderResponse => {
                dtoHeaderResponse.statusMessages.forEach(m => scope$.handleAddMessage("Retrieving server configs: "
                    + m.message))
            }
        )
        ;
    } // initializeServerConfigs()

    private initializeSubmissionContact() {


        this.loggedInUser = this._authenticationService.getUserName();
        let scope$ = this;
        scope$._dtoRequestServiceContact.get(new DtoRequestItemContact(
            ContactSearchType.BY_USERNAME,
            this.loggedInUser)).subscribe(contact => {


                let foo: string = "foo";

                if (contact && contact.contactId && contact.contactId > 0) {

                    //loggedInUser
                    this.fileItemService.loadFileItem(GobiiFileItem.build(scope$.gobiiExtractFilterType, ProcessType.CREATE)
                            .setEntityType(EntityType.Contacts)
                            .setEntitySubType(EntitySubType.CONTACT_SUBMITED_BY)
                            .setCvFilterType(CvFilterType.UNKNOWN)
                            .setExtractorItemType(ExtractorItemType.ENTITY)
                            .setItemName(contact.email)
                            .setItemId(contact.contactId.toLocaleString()),
                        true);

                } else {
                    scope$.handleAddMessage("There is no contact associated with user " + this.loggedInUser);
                }

            },
            dtoHeaderResponse => {
                dtoHeaderResponse.statusMessages.forEach(m => scope$.handleAddMessage("Retrieving contacts for submission: "
                    + m.message))
            });

        //   _dtoRequestServiceContact
    }

    public handleServerSelected(arg) {
        this.selectedServerConfig = arg;
        let currentPath = window.location.pathname;
        let currentPage: string = currentPath.substr(currentPath.lastIndexOf('/') + 1, currentPath.length);
        let newDestination = "http://"
            + this.selectedServerConfig.domain
            + ":"
            + this.selectedServerConfig.port
            + this.selectedServerConfig.contextRoot
            + currentPage;

        window.location.href = newDestination;
    } // handleServerSelected()


// ********************************************************************
// ********************************************** EXPORT TYPE SELECTION AND FLAGS


    public displayAvailableDatasets: boolean = true;
    public displaySelectorPi: boolean = true;
    public doPrincipleInvestigatorTreeNotifications: boolean = false;
    public displaySelectorProjectForPi: boolean = true;
    public displaySelectorForAllProjects: boolean = false;
    public displaySelectorExperiment: boolean = true;
    public displaySelectorDataType: boolean = false;
    public displaySelectorPlatform: boolean = false;
    public displayIncludedDatasetsGrid: boolean = true;
    public displaySampleListTypeSelector: boolean = false;
    public displaySampleMarkerBox: boolean = false;
    public reinitProjectList: boolean = false;
    public gobiiExtractFilterType: GobiiExtractFilterType;

    private handleExportTypeSelected(arg: GobiiExtractFilterType) {


        //
        this.store.dispatch(new fileItemAction.RemoveAllFromExtractAction(arg));
        this.store.dispatch(new fileItemAction.SetExtractType({gobiiExtractFilterType: arg}));

        // this will trigger onchange events in child components
        this.gobiiExtractFilterType = arg;

        this.instructionSubmissionService.submitReady(this.gobiiExtractFilterType)
            .subscribe(submistReady => {
                submistReady ? this.submitButtonStyle = this.buttonStyleSubmitReady : this.submitButtonStyle = this.buttonStyleSubmitNotReady;
            })


        let jobId: string = FileName.makeUniqueFileId();
        this.fileItemService.loadFileItem(GobiiFileItem.build(arg, ProcessType.CREATE)
            .setExtractorItemType(ExtractorItemType.JOB_ID)
            .setItemId(jobId)
            .setItemName(jobId), true)

        if (this.gobiiExtractFilterType === GobiiExtractFilterType.WHOLE_DATASET) {

            this.doPrincipleInvestigatorTreeNotifications = false;
            this.fileItemService.setItemLabelType(this.gobiiExtractFilterType,
                NameIdFilterParamTypes.CONTACT_PI,
                NameIdLabelType.UNKNOWN);
            this.displaySelectorPi = true;
            this.displaySelectorProjectForPi = true;
            this.displaySelectorForAllProjects = false;
            this.displaySelectorExperiment = true;
            this.displayAvailableDatasets = true;
            this.displayIncludedDatasetsGrid = true;

            this.displaySelectorDataType = false;
            this.displaySelectorPlatform = false;
            this.displaySampleListTypeSelector = false;
            this.displaySampleMarkerBox = false;
            this.reinitProjectList = false;


        } else if (this.gobiiExtractFilterType === GobiiExtractFilterType.BY_SAMPLE) {

            this.fileItemService.loadWithFilterParams(this.gobiiExtractFilterType,
                NameIdFilterParamTypes.PROJECTS,
                null);


            this.displaySelectorPi = true;
            this.doPrincipleInvestigatorTreeNotifications = true;
            this.fileItemService.setItemLabelType(this.gobiiExtractFilterType,
                NameIdFilterParamTypes.CONTACT_PI,
                NameIdLabelType.ALL);

            this.displaySelectorProjectForPi = false;
            this.displaySelectorForAllProjects = true;
            this.displaySelectorDataType = true;
            this.displaySelectorPlatform = true;
            this.displaySampleListTypeSelector = true;

            this.displaySelectorExperiment = false;
            this.displayAvailableDatasets = false;
            this.displayIncludedDatasetsGrid = false;
            this.displaySampleMarkerBox = false;

            this.reinitProjectList = true;

        } else if (this.gobiiExtractFilterType === GobiiExtractFilterType.BY_MARKER) {

            this.displaySelectorPi = false;
            this.displaySelectorDataType = true;
            this.displaySelectorPlatform = true;
            this.displaySampleMarkerBox = true;

            this.displaySelectorProjectForPi = false;
            this.displaySelectorForAllProjects = false;
            this.doPrincipleInvestigatorTreeNotifications = false;
            this.fileItemService.setItemLabelType(this.gobiiExtractFilterType,
                NameIdFilterParamTypes.CONTACT_PI,
                NameIdLabelType.UNKNOWN);
            this.displaySelectorProjectForPi = false;
            this.displaySelectorExperiment = false;
            this.displayAvailableDatasets = false;
            this.displayIncludedDatasetsGrid = false;
            this.displaySampleListTypeSelector = false;

            this.reinitProjectList = false;


        }


        this.initializeSubmissionContact();


        this.fileItemService.loadWithFilterParams(this.gobiiExtractFilterType,
            NameIdFilterParamTypes.CONTACT_PI,
            null);
        // this.fileItemService.loadWithFilterParams(this.gobiiExtractFilterType,
        //     this.nameIdRequestParamsExperiments);

        this.fileItemService.loadWithFilterParams(this.gobiiExtractFilterType,
            NameIdFilterParamTypes.MAPSETS,
            null);

        this.fileItemService.loadWithFilterParams(this.gobiiExtractFilterType,
            NameIdFilterParamTypes.CV_DATATYPE,
            null);

        this.fileItemService.loadWithFilterParams(this.gobiiExtractFilterType,
            NameIdFilterParamTypes.PLATFORMS,
            null);

        //changing modes will have nuked the submit as item in the tree, so we need to re-event (sic.) it:
        let formatItem: GobiiFileItem = GobiiFileItem
            .build(this.gobiiExtractFilterType, ProcessType.UPDATE)
            .setExtractorItemType(ExtractorItemType.EXPORT_FORMAT)
            .setItemId(GobiiExtractFormat[GobiiExtractFormat.HAPMAP])
            .setItemName(GobiiExtractFormat[GobiiExtractFormat[GobiiExtractFormat.HAPMAP]]);
        this.fileItemService.loadFileItem(formatItem, true);


        this.fileItemService
            .loadFileItem(GobiiFileItem.build(this.gobiiExtractFilterType, ProcessType.CREATE)
                    .setExtractorItemType(ExtractorItemType.SAMPLE_LIST_TYPE)
                    .setItemName(GobiiSampleListType[GobiiSampleListType.GERMPLASM_NAME])
                    .setItemId(GobiiSampleListType[GobiiSampleListType.GERMPLASM_NAME]),
                true);


    }





    public handleAddMessage(arg) {
        this.store.dispatch(new historyAction.AddStatusAction(new HeaderStatusMessage(arg, StatusLevel.OK, undefined)))
    }

    public handleClearMessages() {
        this.store.dispatch(new historyAction.ClearStatusesAction())
    }

    public handleHeaderStatusMessage(statusMessage: HeaderStatusMessage) {

        if (!statusMessage.statusLevel || statusMessage.statusLevel != StatusLevel.WARNING) {
            this.handleAddMessage(statusMessage.message);
        } else {
            console.log(statusMessage.message);
        }
    }


// ********************************************************************
// ********************************************** MARKER/SAMPLE selection
    // ********************************************************************
    // ********************************************** Extract file submission
    public submitButtonStyleDefault = "btn btn-primary";
    public buttonStyleSubmitReady = "btn btn-success";
    public buttonStyleSubmitNotReady = "btn btn-warning";
    public submitButtonStyle = this.buttonStyleSubmitNotReady;
    public clearButtonStyle = this.submitButtonStyleDefault;


    private setSubmitButtonState(): boolean {

        let returnVal: boolean = false;

        return returnVal;
    }

    public handleOnMouseOverSubmit(arg, isEnter) {

        // this.criteriaInvalid = true;

        if (isEnter) {

            this.setSubmitButtonState()


        }

    }

    public handleClearTree() {

        this.handleExportTypeSelected(this.gobiiExtractFilterType);


    }


    public handleExtractSubmission() {

        this.instructionSubmissionService.submit(this.gobiiExtractFilterType);

    }

    ngOnInit(): any {

        this.initializeServerConfigs();


    } // ngOnInit()


}

