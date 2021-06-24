import {Component, EventEmitter, KeyValueDiffers, OnInit, OnChanges, SimpleChanges} from "@angular/core";
import {GobiiFileItem} from "../model/gobii-file-item";
import {GobiiExtractFilterType} from "../model/type-extractor-filter";
import {FilterParams} from "../model/filter-params";
import {HeaderStatusMessage} from "../model/dto-header-status-message";
import {ExtractorItemType} from "../model//type-extractor-item";
import * as fromRoot from '../store/reducers';
import * as fileAction from '../store/actions/fileitem-action';
import {Store} from "@ngrx/store";
import {Observable} from "rxjs/Observable";
import {FileItemService} from "../services/core/file-item-service";
import {FilterParamNames} from "../model/file-item-param-names";
import {ViewIdGeneratorService} from "../services/core/view-id-generator-service";
import {TypeControl} from "../services/core/type-control";


@Component({
    selector: 'checklist-box',
    inputs: ['gobiiExtractFilterType',
        'filterParamName'],
    outputs: ['onError'],
    template: `
        <form
                [id]="viewIdGeneratorService.makeCheckboxListBoxId(filterParamName)">
            <div style="overflow:auto; height: 80px; border: 1px solid #336699; padding-left: 5px; padding-right: 2px">
                <div *ngFor="let gobiiFileItem of gobiiFileItems$ | async"
                     (click)=handleItemSelected($event)>
                    <p-checkbox
                        name="{{gobiiExtractFilterType}}"
                        value={{gobiiFileItem.getFileItemUniqueId()}}
                        label="{{gobiiFileItem.getItemName()}}"
                        (ngModelChange)=handleModelChange($event)
                        [(ngModel)]="selectedValues"
                    ></p-checkbox>
                </div>
            </div>
        </form>` // end template

})


export class CheckListBoxComponent implements OnInit, OnChanges {

    differ: any;

    constructor(private store: Store<fromRoot.State>,
                private fileItemService: FileItemService,
                private differs: KeyValueDiffers,
                public viewIdGeneratorService: ViewIdGeneratorService) {

        this.differ = differs.find({}).create();


    } // ctor
    ngOnChanges(changes: SimpleChanges): void {
    }

    public typeControl:any = TypeControl;

    ngOnInit(): any {
        this.gobiiFileItems$ = this.fileItemService.getForFilter(this.filterParamName);
    }

    private gobiiExtractFilterType: GobiiExtractFilterType = GobiiExtractFilterType.UNKNOWN;
    private onError: EventEmitter<HeaderStatusMessage> = new EventEmitter();


    public filterParamName: FilterParamNames;
    public gobiiFileItems$: Observable<GobiiFileItem[]>;

    selectedValues: string[] = [];

    public handleModelChange(arg) {

       if (this.selectedValues.length < arg.length) {
           //something was added
           let addId = arg.filter( value => !this.selectedValues.includes(value));
           this.store.dispatch(new fileAction.AddToExtractByItemIdAction(addId[0]));
       } else {
           //something was removed
           let removeId = this.selectedValues.filter( value => !arg.includes(value));
           this.store.dispatch(new fileAction.RemoveFromExractByItemIdAction(removeId[0]));
       }
    }


    public handleItemSelected(arg) {

//        arg.currentTarget.style = "background-color:#b3d9ff";

    }

    public clearSelection(): void {
        this.selectedValues = [];
    }

    public handleHeaderStatus(headerStatusMessage: HeaderStatusMessage) {

        this.onError.emit(headerStatusMessage);
    }


}
