import {Component, Input, Output, EventEmitter} from "@angular/core";
import {GobiiFileItem} from "../model/gobii-file-item";
import {GobiiExtractFilterType} from "../model/type-extractor-filter";
import {Store} from "@ngrx/store";
import * as fromRoot from '../store/reducers';
import * as fileAction from '../store/actions/fileitem-action';
import * as historyAction from '../store/actions/history-action';
import {Observable} from "rxjs/Observable";
import {FilterParamNames} from "../model/file-item-param-names";
import {FileItemService} from "../services/core/file-item-service";
import {ViewIdGeneratorService} from "../services/core/view-id-generator-service";
import {SelectItem} from 'primeng/api';



@Component({
    selector: 'name-id-list-box',
    outputs: [],
    templateUrl: 'name-id-list-box.component.html'

})
export class NameIdListBoxComponent  {


    public fileItems$: Observable<GobiiFileItem[]>;

    public options: SelectItem[] = [];
    public selectedItem: string;

    @Input() private gobiiExtractFilterType: GobiiExtractFilterType;

    @Input() private filterParamName:FilterParamNames;
    public controlId:string = "<NO-ID>";

    @Output() valueChange = new EventEmitter()

    constructor(private store: Store<fromRoot.State>,
                private fileItemService:FileItemService,
                private viewIdGeneratorService:ViewIdGeneratorService) {

    } // ctor


    ngOnInit(): any {
        //TODO: this method is being called multiple times. must find a way
        //to disable that behavior.
        let scope$ = this;
        this.controlId = this.viewIdGeneratorService.makeIdNameIdListBoxId(this.filterParamName);
        this.fileItems$ = this.fileItemService.getForFilter(this.filterParamName)

        this
            .fileItems$
            .subscribe(items => {
                    scope$.options = [];
                    if (this.previousSelectedItemId === null && items && items.length > 0) {
                        this.previousSelectedItemId = items[0].getFileItemUniqueId();
                        if (items.length > 0) {
                            this.selectedItem = items[0].getFileItemUniqueId();
                        }
                    }
                    items.forEach((item, index) => {
                        let itemLabel = item.getItemName().length < 34 ? item.getItemName() : item.getItemName().substr(0,30).concat(" . . .");

                        scope$.options.push(
                            {
                                label: itemLabel,
                                value: item.getFileItemUniqueId()
                            }
                        );
                    });
                    
                 },
                error => {
                    this.store.dispatch(new historyAction.AddStatusMessageAction(error))
                });

        
    }

    previousSelectedItemId: string = null;

    public handleFileItemSelected(arg) {

        if( ! this.gobiiExtractFilterType ) {
            this.store.dispatch(new historyAction.AddStatusMessageAction("The gobiiExtractFilterType property is not set"))
        }

        let newFileItemUniqueId: string = arg.value;
        let previousFileItemUniqueId: string = this.previousSelectedItemId;

        this.store.dispatch(new fileAction.ReplaceByItemIdAction({
            filterParamName: this.filterParamName,
            gobiiExtractFilterType: this.gobiiExtractFilterType,
            itemIdCurrentlyInExtract: previousFileItemUniqueId,
            itemIdToReplaceItWith: newFileItemUniqueId
        }));

        this.previousSelectedItemId = newFileItemUniqueId;
        //emit change outside
        this.valueChange.emit(arg.value);
    }

    public clearSelection(): void {
        if (!this.options || this.options.length == 0) return;
        this.selectedItem = this.options[0].value;
    }

    public refreshData(): void {

        let scope$ = this;
        this.fileItemService.getForFilter(this.filterParamName).subscribe( items => {
            scope$.options = [];
            
            items.forEach((item, index) => {
                let itemLabel = item.getItemName().length < 34 ? item.getItemName() : item.getItemName().substr(0,30).concat(" . . .");
                if (index == 0) {
                    scope$.selectedItem = item.getFileItemUniqueId();
                }
                scope$.options.push(
                    {
                        label: itemLabel,
                        value: item.getFileItemUniqueId()
                    }
                );
            });

           
        });
    }


} // class
