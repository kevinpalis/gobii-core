import {Component, Input, OnChanges, OnInit, SimpleChange, ViewEncapsulation} from "@angular/core";
import {Store} from "@ngrx/store";
import * as fromRoot from '../store/reducers';
import * as historyAction from '../store/actions/history-action';
import {GobiiExtractFilterType} from "../model/type-extractor-filter";
import {FileItemService} from "../services/core/file-item-service";
import {FilterParamNames} from "../model/file-item-param-names";
import {Observable} from "rxjs/Observable";
import {DataSet} from "../model/dataset";
import {GobiiFileItem} from "../model/gobii-file-item";
import * as fileAction from '../store/actions/fileitem-action';
import {OverlayPanel} from "primeng/primeng";
import {DtoRequestService} from "../services/core/dto-request.service";
import {JsonToGfiDataset} from "../services/app/jsontogfi/json-to-gfi-dataset";
import {FilterParamsColl} from "../services/core/filter-params-coll";
import {DtoRequestItemGfi} from "../services/app/dto-request-item-gfi";
import {FilterParams} from "../model/filter-params";
import {JsonToGfiAnalysis} from "../services/app/jsontogfi/json-to-gfi-analysis";
import {CvFilters, CvFilterType} from "../model/cv-filter-type";
import {EntitySubType, EntityType} from "../model/type-entity";
import {GobiiFileItemCompoundId} from "../model/gobii-file-item-compound-id";
import {ExtractorItemType} from "../model/type-extractor-item";
// import {TooltipModule} from "/primeng/primeng";
import {PagedFileItemList} from "../model/payload/paged-item-list";
import {Pagination} from "../model/payload/pagination";
import {Subject} from "rxjs/Subject";
import 'rxjs/add/operator/withLatestFrom'
import {ExtractReadyPayloadFilter, JobTypeFilters, PayloadFilter} from "../store/actions/action-payload-filter";
import {ViewIdGeneratorService} from "../services/core/view-id-generator-service";
import {FileItem} from "ng2-file-upload";
import { AuthenticationService } from 'src/services/core/authentication.service';

@Component({
    selector: 'dataset-datatable',
    encapsulation: ViewEncapsulation.None,
    inputs: [],
    outputs: [],
    // styleUrls: [
    //     "/node_modules/primeicons/primeicons.css",
    //     "/node_modules/primeng/resources/themes/nova/theme.css",
    //     "/node_modules/primeng/resources/primeng.min.css",
    //     "/node_modules/bootswatch/dist/cerulean/bootstrap.min.css"
    // ],
    templateUrl: 'dataset-datatable.component.html'
})

export class DatasetDatatableComponent implements OnInit, OnChanges {


    public foo:string = "foo";
    public onClickForNextPage$ = new Subject<Pagination>();

    constructor(private store: Store<fromRoot.State>,
                private fileItemService: FileItemService,
                private filterParamsColl: FilterParamsColl,
                private fileItemRequestService: DtoRequestService<GobiiFileItem[]>,
                private authenticationService: AuthenticationService,
                public viewIdGeneratorService:ViewIdGeneratorService) {

        if (this.doPaging) {
            this.datasetsFileItems$ = this.store.select(fromRoot.getDatasetEntitiesPaged);
        } else {
            this.datasetsFileItems$ = this.store.select(fromRoot.getDatasetEntities);
        }

        this.onClickForNextPage$
            .withLatestFrom(this.store)
            .subscribe(([data, state]) => {
                if (state.fileItems.filters[FilterParamNames.DATASET_LIST_PAGED]) {
                    let pagination: Pagination = state.fileItems.filters[FilterParamNames.DATASET_LIST_PAGED].pagination;
                    if (pagination) {
                        this.fileItemService.loadPagedEntityList(this.gobiiExtractFilterType,
                            FilterParamNames.DATASET_LIST_PAGED,
                            pagination.pagedQueryId,
                            pagination.pageSize,
                            ++this.page,
                            state.fileItems.currentCrop)
                    }
                }
            });
    }

    public doPaging = false;
//    public datasetsFileItems$: Observable<GobiiFileItem[]> = this.store.select(fromRoot.getDatsetEntities);
    //public datasetsFileItems$: Observable<GobiiFileItem[]> = this.store.select(fromRoot.getDatsetEntitiesPaged);
    public datasetsFileItems$: Observable<GobiiFileItem[]>;
    public selectedDatasets: GobiiFileItem[];
    public datasetAnalysesNames: string[] = [];
    public nameIdFilterParamTypes: any = Object.assign({}, FilterParamNames);
    public selectedDatasetDetailEntity: DataSet;
    public analysisPanelCollapsed: boolean = true;
    public analysisPanelToggle: boolean = true;

    public filterToExtractReady: boolean = true;
    public disableFilterToExtractReadyCheckbox: boolean = false;

    /**
     * Event to allow only extract ready jobs to be selected.
     * @param fi - GobiiFileItem
     */
    public hideNonExtractReadyJobs(fi:GobiiFileItem, jobStatusFilterValues:JobTypeFilters) {
        return !ExtractReadyPayloadFilter.isExtractReady(fi, jobStatusFilterValues);
    }

    public handleFilterToExtractReadyChecked(event) {

        let jobStatusFilterValues:JobTypeFilters;

        if (event.checked === true) {
            jobStatusFilterValues = {
                "load" : ["completed"]
            };
        } else {
            jobStatusFilterValues = null;
        }


        this.store.dispatch(new fileAction.LoadFilterAction(
            {
                filterId: FilterParamNames.DATASET_LIST_STATUS,
                filter: new ExtractReadyPayloadFilter(
                    GobiiExtractFilterType.WHOLE_DATASET,
                    new GobiiFileItemCompoundId(ExtractorItemType.ENTITY,
                        EntityType.DATASET,
                        EntitySubType.UNKNOWN,
                        CvFilterType.UNKNOWN,
                        CvFilters.get(CvFilterType.UNKNOWN)),
                    null,
                    null,
                    null,
                    null,
                    null,
                    jobStatusFilterValues
                )
            }
        ))

    }


    public handleHideOverlayPanel($event) {

        this.datasetAnalysesNames = [];
        this.analysisPanelCollapsed = true;
    }

    /***
     * Lazy load dataset when the dataset pane is opened. Notice that we don't dispatch the dataset to the store.
     * There are a couple of things to note here:
     * 1) Keeping data local to the component breaks the store model, because we are effectively keeping some state locally.
     *    I would argue that this issues is mitigated by the fact the data are only used in that pop up and then they go away;
     * 2) Consequently, if the user returns over and over again to the same dataset, we are taking on the otherwise unnecessary
     *    expense of repeating the same query. However, it is my judgement that that scenario will happen infrequently enough
     *    that we don't need to worry about this for now.
     * @param event
     * @param {GobiiFileItem} dataSetItem
     * @param {OverlayPanel} datasetOverlayPanel
     */
    selectDataset(event, dataSetItem: GobiiFileItem, datasetOverlayPanel: OverlayPanel) {
        let datasetId: number = dataSetItem.getEntity().id;

        let filterParams: FilterParams = this.filterParamsColl.getFilter(FilterParamNames.DATASET_BY_DATASET_ID, GobiiExtractFilterType.WHOLE_DATASET);

        let dtoRequestItemGfi: DtoRequestItemGfi = new DtoRequestItemGfi(filterParams,
            datasetId.toString(),
            new JsonToGfiDataset(filterParams, this.filterParamsColl));

        this.fileItemRequestService
            .get(dtoRequestItemGfi)
            .subscribe(entityItems => {
                if (entityItems.length === 1 && entityItems[0].getEntity()) {
                    this.selectedDatasetDetailEntity = entityItems[0].getEntity();
                    this.analysisPanelToggle = this.selectedDatasetDetailEntity.analysesIds.length > 0;
                    datasetOverlayPanel.toggle(event);
                } else {
                    this.store
                        .dispatch(new historyAction.AddStatusMessageAction("There is no dataset data for dataset id "
                            + datasetId.toString()));

                }
            });
    }


    /***
     * Lazy-load analyses if there are any.
     * The note about not putting these data in the store with regard to the dataset entity applies to
     * the analyses.
     * @param event
     */
    public handleOpenAnalysesTab(event) {

        if (this.selectedDatasetDetailEntity) {

            let datasetId: number = this.selectedDatasetDetailEntity.id;
            let filterParams: FilterParams = this.filterParamsColl.getFilter(FilterParamNames.ANALYSES_BY_DATASET_ID, GobiiExtractFilterType.WHOLE_DATASET);

            let dtoRequestItemGfi: DtoRequestItemGfi = new DtoRequestItemGfi(filterParams,
                datasetId.toString(),
                new JsonToGfiAnalysis(filterParams, this.filterParamsColl));

            this.fileItemRequestService
                .get(dtoRequestItemGfi)
                .subscribe(entityItems => {

                    this.datasetAnalysesNames = entityItems
                        .map(gfi => gfi.getItemName())


                });

        } // if we have a selected datset entity

    }


    private page: number = 0;

    public handleRowChecked(checked: boolean, selectedDatasetFileItem: GobiiFileItem) {
        this.handleItemChecked(selectedDatasetFileItem.getFileItemUniqueId(), checked);

    }


    public handleRowSelect(event) {
        let selectedDatasetFileItem: GobiiFileItem = event.data;
        this.handleItemChecked(selectedDatasetFileItem.getFileItemUniqueId(), true);
    }

    public handleRowUnSelect(event) {
        let selectedDatasetFileItem: GobiiFileItem = event.data;
        this.handleItemChecked(selectedDatasetFileItem.getFileItemUniqueId(), false);
    }

    public handleOnRowClick(event) {
        let selectedDataset: GobiiFileItem = event.data;

    }

    public handleItemChecked(currentFileItemUniqueId: string, isChecked: boolean) {

        if (isChecked) {
            this.store.dispatch(new fileAction.AddToExtractByItemIdAction(currentFileItemUniqueId));
        } else {
            this.store.dispatch(new fileAction.RemoveFromExractByItemIdAction(currentFileItemUniqueId));
        }

    } // handleItemChecked()


    @Input()
    public gobiiExtractFilterType: GobiiExtractFilterType;

    ngOnInit() {

        //this.handleLoadPage(1);
    } // ngOnInit()

    // gobiiExtractType is not set until you get OnChanges
    ngOnChanges(changes: { [propName: string]: SimpleChange }) {
        if (changes['gobiiExtractFilterType']
            && (changes['gobiiExtractFilterType'].currentValue != null)
            && (changes['gobiiExtractFilterType'].currentValue != undefined)) {

            if (changes['gobiiExtractFilterType'].currentValue != changes['gobiiExtractFilterType'].previousValue) {

                if (this.gobiiExtractFilterType === GobiiExtractFilterType.WHOLE_DATASET) {
                    this.refreshData();
                }

            } // if we have a new filter type

        } // if filter type changed

    } // ngonChanges

    public clearSelection(): void {
        this.filterToExtractReady = false;
        this.selectedDatasets = [];
    }

    public refreshData(): void {

        this.filterToExtractReady = true;
        if (this.doPaging) {
            this.fileItemService.loadPagedEntityList(this.gobiiExtractFilterType,
                FilterParamNames.DATASET_LIST_PAGED,
                null,
                5,
                0,
                this.authenticationService.getGobiiCropType());

        } else {
            this.fileItemService.loadEntityList(this.gobiiExtractFilterType, FilterParamNames.DATASET_LIST, this.authenticationService.getGobiiCropType());
        }
        this.fileItemService.loadNameIdsFromFilterParams(this.gobiiExtractFilterType,
            FilterParamNames.CV_JOB_STATUS,
            null,
            this.authenticationService.getGobiiCropType());
        if (this.doPaging) {
            this.datasetsFileItems$ = this.store.select(fromRoot.getDatasetEntitiesPaged);
        } else {
            this.datasetsFileItems$ = this.store.select(fromRoot.getDatasetEntities);
        }


        
    }
}