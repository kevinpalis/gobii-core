import {Component, EventEmitter, Input, OnChanges, OnInit, SimpleChange, ViewEncapsulation} from '@angular/core';
import {GobiiExtractFormat} from "../model/type-extract-format";
import {GobiiFileItem} from "../model/gobii-file-item";
import {ProcessType} from "../model/type-process";
import {ExtractorItemType} from "../model//type-extractor-item";
import {GobiiExtractFilterType} from "../model/type-extractor-filter";
import {Header} from "../model/payload/header";
import * as fromRoot from '../store/reducers';
import {Store} from "@ngrx/store";
import {FileItemService} from "../services/core/file-item-service";
import {Observable} from "rxjs/Observable";
import {ViewIdGeneratorService} from "../services/core/view-id-generator-service";


@Component({
    selector: 'export-format',
    outputs: ['onFormatSelected', 'onError'],
    inputs: ['gobiiExtractFilterType'],
    encapsulation: ViewEncapsulation.None,
    template: `
        <form>
            <label class="the-legend">Select Format:&nbsp;</label>
            <BR>
            <div class="ui-g" style="width:250px;margin-bottom:5px">
                <div class="ui-g-12" style="height:30px">
                    <p-radioButton
                            (ngModelChange)="handleFormatSelected($event)"
                            [ngModel]="(fileFormat$  | async).getItemId()"
                            name="fileFormat"
                            value="HAPMAP"
                            label="Hapmap"
                            [id]="viewIdGeneratorService.makeExportFormatRadioButtonId(gobiiExtractFormat.HAPMAP)">
                    </p-radioButton>
                </div>
                <div class="ui-g-12" style="height:30px">
                    <p-radioButton
                            (ngModelChange)="handleFormatSelected($event)"
                            [ngModel]="(fileFormat$  | async).getItemId()"
                            name="fileFormat"
                            label="Flapjack"
                            value="FLAPJACK"
                            [id]="viewIdGeneratorService.makeExportFormatRadioButtonId(gobiiExtractFormat.FLAPJACK)">
                    </p-radioButton>
                </div>
                <div class="ui-g-12" style="height:30px">
                    <p-radioButton
                            (ngModelChange)="handleFormatSelected($event)"
                            [ngModel]="(fileFormat$  | async).getItemId()"
                            name="fileFormat"
                            label="Metadata"
                            value="META_DATA_ONLY"
                            [id]="viewIdGeneratorService.makeExportFormatRadioButtonId(gobiiExtractFormat.META_DATA_ONLY)">
                    </p-radioButton>
                </div>
            </div>
        </form>` // end template
})

/**
 * In the template you will notice some slight of hand to get the property value of the
 * GobiiFileItem for ngModel. In my original implementation, the selector from the reducer
 * returned a scalar string value. From that selector I would get the initial state value
 * but not subsequent values, even though the select itself was executing under the debugger.
 * I spent many hours trying to track this issue down and even created the reproduce-radio
 * project to try to isolate the issue. But it seemed to be working. Then I found ngrx/platform
 * issue # 208: https://github.com/ngrx/platform/issues/208
 * This is precisely the issue that was reported. My versions match those of the test project so I don't
 * know why this is happening. I will try to reproduce the issue in the test project as a good citizen.
 * In the meantime, we need to stick to returning actual state objects rather than scalar primtive values
 * from it.
 */
export class ExportFormatComponent implements OnInit, OnChanges {

    constructor(private store: Store<fromRoot.State>,
                private fileItemService: FileItemService,
                public viewIdGeneratorService: ViewIdGeneratorService) {


    } // ctor

    // private nameIdList: NameId[];
    // private selectedNameId: string = null;
    ngOnInit() {

    }


    public gobiiExtractFormat: any = GobiiExtractFormat;

    @Input()
    public fileFormat$: Observable<GobiiFileItem>;

    public gobiiExtractFilterType: GobiiExtractFilterType;
    public onFormatSelected: EventEmitter<GobiiExtractFormat> = new EventEmitter();
    public onError: EventEmitter<Header> = new EventEmitter();

    public handleFormatSelected(arg) {

        this.updateItemService(arg);
    }

    private updateItemService(arg: string) {


        let formatItem: GobiiFileItem = GobiiFileItem
            .build(this.gobiiExtractFilterType, ProcessType.UPDATE)
            .setExtractorItemType(ExtractorItemType.EXPORT_FORMAT)
            .setItemId(arg)
            .setItemName(GobiiExtractFormat[GobiiExtractFormat[arg]]);


        this.fileItemService.replaceFileItemByCompoundId(formatItem);

    }

    public metaDataExtractname: string;

    ngOnChanges(changes: { [propName: string]: SimpleChange }) {

        if (changes['gobiiExtractFilterType']
            && (changes['gobiiExtractFilterType'].currentValue != null)
            && (changes['gobiiExtractFilterType'].currentValue != undefined)) {

            if (changes['gobiiExtractFilterType'].currentValue != changes['gobiiExtractFilterType'].previousValue) {


                let labelSuffix: string = " Metadata";
                if (this.gobiiExtractFilterType === GobiiExtractFilterType.WHOLE_DATASET) {
                    this.metaDataExtractname = "Dataset" + labelSuffix;
                } else if (this.gobiiExtractFilterType === GobiiExtractFilterType.BY_MARKER) {
                    this.metaDataExtractname = "Marker" + labelSuffix;
                } else if (this.gobiiExtractFilterType === GobiiExtractFilterType.BY_SAMPLE) {
                    this.metaDataExtractname = "Sample" + labelSuffix;
                }

            } // if we have a new filter type

        } // if filter type changed


    } // ngonChanges


}
