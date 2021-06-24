import {Component, EventEmitter, OnChanges, OnInit, SimpleChange, ViewEncapsulation} from '@angular/core';
import {GobiiExtractFilterType} from "../model/type-extractor-filter";
import {GobiiSampleListType} from "../model/type-extractor-sample-list";
import {HeaderStatusMessage} from "../model/dto-header-status-message";
import {GobiiFileItem} from "../model/gobii-file-item";
import {ProcessType} from "../model/type-process";
import {ExtractorItemType} from "../model//type-extractor-item";
import {Observable} from "rxjs/Observable";
import * as fromRoot from '../store/reducers';
import {Store} from "@ngrx/store";
import {FileItemService} from "../services/core/file-item-service";
import {ViewIdGeneratorService} from "../services/core/view-id-generator-service";

/***
 * The RadioButton module is not compatabile with font-awesome. So
 * we have to turn out native ViewEncapsulation (i.e., put a protective
 * CSS bubble around our component's corner of the DOM) and manually include our
 * style sheets. Yes, I tried removing front-awesome from index.html and including it
 * explicitly in the status-display-tree-component, which is the only place it is used.
 * There should be a way to get that to work, but I was not able to.
 */
@Component({
    selector: 'sample-list-type',
    inputs: ['gobiiExtractFilterType'],
    outputs: ['onHeaderStatusMessage'],
    encapsulation: ViewEncapsulation.None,
    template: `
        <form>
            <label class="the-legend">List Item Type:&nbsp;</label>
            <BR>
            <div class="ui-g" style="width:250px;margin-bottom:5px">
                <div class="ui-g-12" style="height:30px">
                    <p-radioButton
                            (ngModelChange)="handleSampleTypeSelected($event)"
                            [ngModel]="(sampleListType$ | async).getItemId()"
                            name="listType"
                            value="GERMPLASM_NAME"
                            label="Germplasm Name"
                            [id]="viewIdGeneratorService.makeSampleListTypeId(gobiiSampleListType.GERMPLASM_NAME)">
                    </p-radioButton>
                </div>
                <div class="ui-g-12" style="height:30px">
                    <p-radioButton
                            (ngModelChange)="handleSampleTypeSelected($event)"
                            [ngModel]="(sampleListType$ | async).getItemId()"
                            name="listType"
                            value="EXTERNAL_CODE"
                            label="External Code"
                            [id]="viewIdGeneratorService.makeSampleListTypeId(gobiiSampleListType.EXTERNAL_CODE)">                        
                    </p-radioButton>
                </div>
                <div class="ui-g-12" style="height:30px">
                    <p-radioButton
                            (ngModelChange)="handleSampleTypeSelected($event)"
                            [ngModel]="(sampleListType$ | async).getItemId()"
                            name="listType"
                            value="DNA_SAMPLE"
                            label="DNA Sample"
                            [id]="viewIdGeneratorService.makeSampleListTypeId(gobiiSampleListType.DNA_SAMPLE)">                        
                    </p-radioButton>
                </div>
            </div>
        </form>` // end template
})

export class SampleListTypeComponent implements OnInit, OnChanges {

    constructor(private store: Store<fromRoot.State>,
                private fileItemService: FileItemService,
                public viewIdGeneratorService: ViewIdGeneratorService) {
    } // ctor

    public gobiiSampleListType: any = GobiiSampleListType;
    private onHeaderStatusMessage: EventEmitter<HeaderStatusMessage> = new EventEmitter();
    private gobiiExtractFilterType: GobiiExtractFilterType = GobiiExtractFilterType.UNKNOWN;
    public sampleListType$: any = this.store.select(fromRoot.getSelectedSampleType);
    //Observable<GobiiFileItem> = this.store.select(fromRoot.getSelectedSampleType);

    public handleSampleTypeSelected(radioValue: string) {

        let gobiiSampleListType: GobiiSampleListType = GobiiSampleListType[radioValue];

        this.fileItemService
            .replaceFileItemByCompoundId(GobiiFileItem.build(this.gobiiExtractFilterType, ProcessType.CREATE)
                .setExtractorItemType(ExtractorItemType.SAMPLE_LIST_TYPE)
                .setItemName(GobiiSampleListType[gobiiSampleListType])
                .setItemId(GobiiSampleListType[gobiiSampleListType]));

    }


    ngOnInit(): any {
    }


    ngOnChanges(changes: { [propName: string]: SimpleChange }) {
    }
}
