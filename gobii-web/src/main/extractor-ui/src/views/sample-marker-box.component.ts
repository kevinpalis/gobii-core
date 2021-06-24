import {Component, EventEmitter, OnChanges, OnInit, SimpleChange, ViewEncapsulation} from "@angular/core";
import {HeaderStatusMessage} from "../model/dto-header-status-message";
import {GobiiExtractFilterType} from "../model/type-extractor-filter";
import {GobiiFileItem} from "../model/gobii-file-item";
import {ProcessType} from "../model/type-process";
import {ExtractorItemType} from "../model//type-extractor-item";
import {Labels} from "./entity-labels";
import {FilterParamNames} from "../model/file-item-param-names";
import * as fromRoot from '../store/reducers';
import {FileItemService} from "../services/core/file-item-service";
import {Store} from "@ngrx/store";
import {Observable} from "rxjs/Observable";
import {StatusLevel} from "../model/type-status-level";
import * as historyAction from "../store/actions/history-action";
import {EntityType} from "../model/type-entity";
import {ViewIdGeneratorService} from "../services/core/view-id-generator-service";
import { switchMap } from 'rxjs/operators';


@Component({
    selector: 'sample-marker-box',
    inputs: ['gobiiExtractFilterType'],
    outputs: ['onSampleMarkerError'],
    encapsulation: ViewEncapsulation.None,
    templateUrl: "sample-marker-box.component.html"
})

export class SampleMarkerBoxComponent implements OnInit, OnChanges {

    public constructor(private store: Store<fromRoot.State>,
                       private fileItemService: FileItemService,
                       public viewIdGeneratorService: ViewIdGeneratorService) {

    }

    public nameIdFilterParamTypesMarkerGroup: FilterParamNames = FilterParamNames.MARKER_GROUPS;


    public readonly extractorItemType: any = ExtractorItemType;
    public readonly ITEM_FILE_TYPE = "ITEM_FILE_TYPE";
    public readonly ITEM_LIST_TYPE = "ITEM_LIST_TYPE";
    public readonly MARKER_GROUP_TYPE = "MARKER_GROUP_TYPE";

    public maxListItems: number = 200;
    public displayMaxItemsExceeded: boolean = false;
    public maxExceededTypeLabel: string;

    public displayChoicePrompt: boolean = false;
    public selectedListType: string = this.ITEM_FILE_TYPE;

    public displayUploader: boolean = true;
    public displayListBox: boolean = false;
    public displayMarkerGroupBox: boolean = false;
    public displayMarkerGroupRadio: boolean = false;

    public gobiiExtractFilterType: GobiiExtractFilterType = GobiiExtractFilterType.UNKNOWN;
    public onSampleMarkerError: EventEmitter<HeaderStatusMessage> = new EventEmitter();

    public extractTypeLabelExisting: string;
    public extractTypeLabelProposed: string;


    // private handleUserSelected(arg) {
    //     this.onUserSelected.emit(this.nameIdList[arg.srcElement.selectedIndex].id);
    // }
    //
    // constructor(private _dtoRequestService:DtoRequestService<NameId[]>) {
    //
    // } // ctor

    public handleTextBoxDataSubmitted(items: string[]) {

        if (items.length <= this.maxListItems) {

            let nonDuplicateItems: string[] = [];
            items.forEach(item => {

                if (!nonDuplicateItems.find(ii => ii === item)) {
                    nonDuplicateItems.push(item);
                }


            });

            let listItemType: ExtractorItemType =
                this.gobiiExtractFilterType === GobiiExtractFilterType.BY_MARKER ?
                    ExtractorItemType.MARKER_LIST_ITEM : ExtractorItemType.SAMPLE_LIST_ITEM;

            nonDuplicateItems.forEach(listItem => {

                if (listItem && listItem !== "") {

                    this.fileItemService
                        .loadFileItem(GobiiFileItem.build(this.gobiiExtractFilterType, ProcessType.CREATE)
                            .setExtractorItemType(listItemType)
                            .setItemId(listItem)
                            .setItemName(listItem)
                            .setIsEphemeral(true), true);
                }
            });

        } else {

            if (this.gobiiExtractFilterType === GobiiExtractFilterType.BY_MARKER) {
                this.maxExceededTypeLabel = Labels.instance().treeExtractorTypeLabels[ExtractorItemType.MARKER_LIST_ITEM];
            } else if (this.gobiiExtractFilterType === GobiiExtractFilterType.BY_SAMPLE) {
                this.maxExceededTypeLabel = Labels.instance().treeExtractorTypeLabels[ExtractorItemType.SAMPLE_LIST_ITEM];
            } else {
                this.handleStatusHeaderMessage(new HeaderStatusMessage("This control does not handle the currently selected item type: "
                    + GobiiExtractFilterType[this.gobiiExtractFilterType]
                    , null, null))
            }

            this.displayMaxItemsExceeded = true;
        }
    }


    public makeLabel(inputType: string) {

        let returnVal: string;


        if (this.gobiiExtractFilterType === GobiiExtractFilterType.BY_SAMPLE) {

            if (inputType === this.ITEM_FILE_TYPE) {

                returnVal = Labels.instance().treeExtractorTypeLabels[ExtractorItemType.SAMPLE_FILE];

            } else if (inputType === this.ITEM_LIST_TYPE) {
                returnVal = Labels.instance().treeExtractorTypeLabels[ExtractorItemType.SAMPLE_LIST_ITEM];

            } else {

                this.store.dispatch(new historyAction.AddStatusAction(new HeaderStatusMessage(
                    "Unhandled input type making input type label "
                    + inputType
                    + " for extract type "
                    + GobiiExtractFilterType[this.gobiiExtractFilterType],
                    StatusLevel.ERROR,
                    null)));
            }

        } else if (this.gobiiExtractFilterType === GobiiExtractFilterType.BY_MARKER) {

            if (inputType === this.ITEM_FILE_TYPE) {

                returnVal = Labels.instance().treeExtractorTypeLabels[ExtractorItemType.MARKER_FILE];

            } else if (inputType === this.ITEM_LIST_TYPE) {

                returnVal = Labels.instance().treeExtractorTypeLabels[ExtractorItemType.MARKER_LIST_ITEM];

            } else if (inputType === this.MARKER_GROUP_TYPE) {

                returnVal = Labels.instance().entityNodeLabels[EntityType.MARKER_GROUP];

            } else {

                this.store.dispatch(new historyAction.AddStatusAction(new HeaderStatusMessage(
                    "Unhandled input type making input type label "
                    + inputType
                    + " for extract type "
                    + GobiiExtractFilterType[this.gobiiExtractFilterType],
                    StatusLevel.ERROR,
                    null)));
            }

        } else {
            this.store.dispatch(new historyAction.AddStatusAction(new HeaderStatusMessage(
                "This component is not intended to be used in extract type: " + GobiiExtractFilterType[this.gobiiExtractFilterType],
                StatusLevel.ERROR,
                null)));
        }


        return returnVal;
    }

    private currentFileItems: GobiiFileItem[] = [];

    handleSampleMarkerChoicesExist(previousInputType: string, proposedInputType: string): boolean {

        let returnVal: boolean = false;

        this.store.select(fromRoot.getSelectedFileItems)
            .subscribe(fileItems => {

                    let extractorItemTypeListToFind: ExtractorItemType = ExtractorItemType.UNKNOWN;
                    let extractorItemTypeFileToFind: ExtractorItemType = ExtractorItemType.UNKNOWN;

                    if (this.gobiiExtractFilterType === GobiiExtractFilterType.BY_SAMPLE) {
                        extractorItemTypeListToFind = ExtractorItemType.SAMPLE_LIST_ITEM;
                        extractorItemTypeFileToFind = ExtractorItemType.SAMPLE_FILE;
                    } else if (this.gobiiExtractFilterType === GobiiExtractFilterType.BY_MARKER) {

                        extractorItemTypeListToFind = ExtractorItemType.MARKER_LIST_ITEM;
                        extractorItemTypeFileToFind = ExtractorItemType.MARKER_FILE;
                    }

                    this.currentFileItems = fileItems.filter(item => {
                        return ((item.getExtractorItemType() === extractorItemTypeListToFind) ||
                            (item.getExtractorItemType() === extractorItemTypeFileToFind) ||
                            (item.getExtractorItemType() === ExtractorItemType.ENTITY &&
                                item.getEntityType() === EntityType.MARKER_GROUP))
                    });

                    if (this.currentFileItems.length > 0) {

                        this.extractTypeLabelProposed = this.makeLabel(proposedInputType);
                        this.extractTypeLabelExisting = this.makeLabel(previousInputType);
                        this.displayChoicePrompt = true;
                        returnVal = true;
                        // it does not seem that the PrimeNG dialog really blocks in the usual sense;
                        // so we have to chain what we do next off of the click events on the dialog.
                        // see handleUserChoice()

                    }
                },
                hsm => {
                    this.handleStatusHeaderMessage(hsm)
                }
            ).unsubscribe();

        return returnVal;

    }

    handleUserChoice(userChoice: boolean) {

        this.displayChoicePrompt = false;

        if (this.currentFileItems.length > 0 && userChoice === true) {

            this.setDisplayFlags(this.extractTypeLabelProposed);
            this.currentFileItems.forEach(currentFileItem => {

                currentFileItem.setProcessType(ProcessType.DELETE);
                this.fileItemService
                    .unloadFileItemFromExtract(currentFileItem);
            });

        } else {


            this.setDisplayFlags(this.extractTypeLabelExisting);

        } // if-else user answered "yes"
    }


    private setDisplayFlags(labelValue: string) {


        if (labelValue === this.makeLabel(this.ITEM_LIST_TYPE)) {

            this.displayListBox = true;
            this.displayUploader = false;
            this.displayMarkerGroupBox = false;

            this.selectedListType = this.ITEM_LIST_TYPE;

        } else if (labelValue === this.makeLabel(this.ITEM_FILE_TYPE)) {

            this.displayListBox = false;
            this.displayUploader = true;
            this.displayMarkerGroupBox = false;

            this.selectedListType = this.ITEM_FILE_TYPE;

        } else if (labelValue === this.makeLabel(this.MARKER_GROUP_TYPE)) {
            this.displayListBox = false;
            this.displayUploader = false;
            this.displayMarkerGroupBox = true;

            this.selectedListType = this.MARKER_GROUP_TYPE;

        } else {

            this.store.dispatch(new historyAction.AddStatusAction(new HeaderStatusMessage(
                "Unhandled input type setting display flags for label type "
                + labelValue
                + " for extract type "
                + GobiiExtractFilterType[this.gobiiExtractFilterType],
                StatusLevel.ERROR,
                null)));
        }

    } // setDisplayFlags()

    private getPreviousInputType(): string {

        let returnVal: string;

        if (this.displayListBox) {

            returnVal = this.ITEM_LIST_TYPE;

        } else if (this.displayUploader) {

            returnVal = this.ITEM_FILE_TYPE;

        } else if (this.displayMarkerGroupBox) {

            returnVal = this.MARKER_GROUP_TYPE;

        }

        return returnVal;
    }

    public handleTextBoxChanged(event) {

        // if there is no existing selected list or file, then this is just a simple setting
        if (this.handleSampleMarkerChoicesExist(this.getPreviousInputType(), this.ITEM_LIST_TYPE) === false) {

            this.displayListBox = true;
            this.displayUploader = false;
            this.displayMarkerGroupBox = false;

            // this.displayListBox = true;
            // this.displayUploader = false;

        }
    }

    public handleOnClickBrowse($event) {

        if (this.handleSampleMarkerChoicesExist(this.getPreviousInputType(), this.ITEM_FILE_TYPE) === false) {

            this.displayListBox = false;
            this.displayUploader = true;
            this.displayMarkerGroupBox = false;

            // this.displayListBox = false;
            // this.displayUploader = true;

        }
    }

    handleMarkerGroupChanged($event) {

        if (this.handleSampleMarkerChoicesExist(this.getPreviousInputType(), this.MARKER_GROUP_TYPE) === false) {

            this.displayListBox = false;
            this.displayUploader = false;
            this.displayMarkerGroupBox = true;
        }
    }

    private handleStatusHeaderMessage(statusMessage: HeaderStatusMessage) {

        this.onSampleMarkerError.emit(statusMessage);
    }


    ngOnInit(): any {

        if (this.gobiiExtractFilterType === GobiiExtractFilterType.BY_MARKER) {
            this.store.select(fromRoot.getSelectedCrop).subscribe(
                crop => {
                    this.fileItemService.loadNameIdsFromFilterParams(this.gobiiExtractFilterType,
                        FilterParamNames.MARKER_GROUPS,
                        null,
                        crop);
                }
            );
            
        }

        return null;
    }


    ngOnChanges(changes: { [propName: string]: SimpleChange }) {

        if (changes['gobiiExtractFilterType']
            && (changes['gobiiExtractFilterType'].currentValue != null)
            && (changes['gobiiExtractFilterType'].currentValue != undefined)) {

            if (changes['gobiiExtractFilterType'].currentValue != changes['gobiiExtractFilterType'].previousValue) {

                //this.notificationSent = false;

                if (this.gobiiExtractFilterType == GobiiExtractFilterType.BY_MARKER) {
                    this.displayMarkerGroupRadio = true;
                } else {
                    this.displayMarkerGroupRadio = false
                }

            } // if we have a new filter type

        } // if filter type changed


    } // ngonChanges

}
