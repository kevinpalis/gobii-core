import {Injectable} from "@angular/core";
import {EntitySubType, EntityType} from "../../model/type-entity";
import {GobiiExtractFilterType} from "../../model/type-extractor-filter";
import {CvFilters, CvFilterType} from "../../model/cv-filter-type";
import {FilterParams} from "../../model/filter-params";
import * as historyAction from '../../store/actions/history-action';
import * as fromRoot from '../../store/reducers';
import {Store} from "@ngrx/store";
import {NameIdLabelType} from "../../model/name-id-label-type";
import {FilterType} from "../../model/filter-type";
import {FilterParamNames} from "../../model/file-item-param-names";
import "rxjs/add/operator/expand"
import {GobiiFileItem} from "../../model/gobii-file-item";
import {ExtractorItemType} from "../../model/type-extractor-item";
import * as fileAction from '../../store/actions/fileitem-action';
import {GobiiFileItemCompoundId} from "../../model/gobii-file-item-compound-id";
import {DtoRequestService} from "./dto-request.service";
import {PagedFileItemList} from "../../model/payload/paged-item-list";
import {JsonToGfiDataset} from "../app/jsontogfi/json-to-gfi-dataset";
import {DtoRequestItemGfi} from "../app/dto-request-item-gfi";
import {DtoRequestItemGfiPaged} from "../app/dto-request-item-gfi-paged";
import {PayloadFilter} from "../../store/actions/action-payload-filter";
import {ExtractReadyPayloadFilter} from "../../store/actions/action-payload-filter";


@Injectable()
export class FilterParamsColl {

    private filterParams: FilterParams[] = [];

    private addFilter(filterParamsToAdd: FilterParams) {

        let existingFilterParams = this.filterParams
            .find(ffp =>
                ffp.getQueryName() === filterParamsToAdd.getQueryName()
                && ffp.getGobiiExtractFilterType() === filterParamsToAdd.getGobiiExtractFilterType()
            );

        if (!existingFilterParams) {
            this.filterParams.push(filterParamsToAdd);
        } else {
            this.store.dispatch(new historyAction.AddStatusMessageAction("The query "
                + filterParamsToAdd.getQueryName()
                + " because there is already a filter by that name for this extract type "
                + GobiiExtractFilterType[filterParamsToAdd.getQueryName()]));

        }
    }

    public getFilter(nameIdFilterParamTypes: FilterParamNames, gobiiExtractFilterType: GobiiExtractFilterType): FilterParams {

        return this.filterParams.find(ffp =>
            ffp.getQueryName() === nameIdFilterParamTypes &&
            ffp.getGobiiExtractFilterType() === gobiiExtractFilterType
        )
    }

    constructor(private store: Store<fromRoot.State>,
                private pagedDatasetRequestService: DtoRequestService<PagedFileItemList>,
                private fileItemRequestService: DtoRequestService<GobiiFileItem[]>) {

        // ************************************************************************
        // **************************** GENERAL  *********************************
        let cvJobStatusCompoundUniqueId: GobiiFileItemCompoundId =
            new GobiiFileItemCompoundId(ExtractorItemType.ENTITY,
                EntityType.CV,
                EntitySubType.UNKNOWN,
                CvFilterType.JOB_STATUS,
                CvFilters.get(CvFilterType.JOB_STATUS));

        // ************************************************************************


        this.addFilter(
            FilterParams
                .build(FilterParamNames.CROP_TYPE,
                    GobiiExtractFilterType.UNKNOWN,
                    EntityType.CROP)
                .setIsDynamicFilterValue(false)
                .setExtractorItemType(ExtractorItemType.CROP_TYPE)
                .setNameIdLabelType(NameIdLabelType.NO)
        );

        this.addFilter(
            FilterParams
                .build(FilterParamNames.CROP_TYPE,
                    GobiiExtractFilterType.WHOLE_DATASET,
                    EntityType.CROP)
                .setIsDynamicFilterValue(false)
                .setExtractorItemType(ExtractorItemType.CROP_TYPE)
                .setNameIdLabelType(NameIdLabelType.NO)
        );

        this.addFilter(
            FilterParams
                .build(FilterParamNames.CROP_TYPE,
                    GobiiExtractFilterType.BY_MARKER,
                    EntityType.CROP)
                .setIsDynamicFilterValue(false)
                .setExtractorItemType(ExtractorItemType.CROP_TYPE)
                .setNameIdLabelType(NameIdLabelType.NO)
        );

        this.addFilter(
            FilterParams
                .build(FilterParamNames.CROP_TYPE,
                    GobiiExtractFilterType.BY_SAMPLE,
                    EntityType.CROP)
                .setIsDynamicFilterValue(false)
                .setExtractorItemType(ExtractorItemType.CROP_TYPE)
                .setNameIdLabelType(NameIdLabelType.NO)
        );

        // ************************************************************************
        // **************************** BY SAMPLE *********************************
        this.addFilter(
            FilterParams
                .build(FilterParamNames.CV_DATATYPE,
                    GobiiExtractFilterType.BY_SAMPLE,
                    EntityType.CV)
                .setIsDynamicFilterValue(false)
                .setCvFilterType(CvFilterType.DATASET_TYPE)
                .setCvFilterValue(CvFilters.get(CvFilterType.DATASET_TYPE))
                .setFilterType(FilterType.NAMES_BY_TYPE_NAME)
                .setNameIdLabelType(NameIdLabelType.SELECT_A)
        );

        this.addFilter(
            FilterParams
                .build(FilterParamNames.MAPSETS,
                    GobiiExtractFilterType.BY_SAMPLE,
                    EntityType.MAPSET)
                .setIsDynamicFilterValue(false)
                .setNameIdLabelType(NameIdLabelType.NO));

        this.addFilter(
            FilterParams
                .build(FilterParamNames.PLATFORMS,
                    GobiiExtractFilterType.BY_SAMPLE,
                    EntityType.PLATFORM)
                .setIsDynamicFilterValue(false)
        );


        this.addFilter(
            FilterParams
                .build(FilterParamNames.CONTACT_PI_HIERARCHY_ROOT,
                    GobiiExtractFilterType.BY_SAMPLE,
                    EntityType.CONTACT)
                .setExtractorItemType(ExtractorItemType.ENTITY)
                .setCvFilterType(CvFilterType.UNKNOWN)
                .setIsDynamicFilterValue(true)
                .setIsDynamicDataLoad(false)
                .setEntitySubType(EntitySubType.CONTACT_PRINCIPLE_INVESTIGATOR)
                .setNameIdLabelType(NameIdLabelType.ALL)
                .setIsExtractCriterion(true)
                .setFilterType(FilterType.NAMES_BY_TYPE_NAME)
                .setCvFilterValue("PI"));

        this.addFilter(
            FilterParams
                .build(FilterParamNames.PROJECTS_BY_CONTACT,
                    GobiiExtractFilterType.BY_SAMPLE,
                    EntityType.PROJECT)
                .setExtractorItemType(ExtractorItemType.ENTITY)
                .setRelatedEntityUniqueId(new GobiiFileItemCompoundId(ExtractorItemType.ENTITY,
                    EntityType.CONTACT,
                    EntitySubType.CONTACT_PRINCIPLE_INVESTIGATOR,
                    CvFilterType.UNKNOWN,
                    null).setIsExtractCriterion(true))
                .setIsDynamicFilterValue(false)
                .setIsDynamicDataLoad(false)
                .setNameIdLabelType(NameIdLabelType.ALL)
                .setIsExtractCriterion(true));


        this.getFilter(FilterParamNames.CONTACT_PI_HIERARCHY_ROOT, GobiiExtractFilterType.BY_SAMPLE)
            .setChildNameIdRequestParams(
                [this.getFilter(FilterParamNames.PROJECTS_BY_CONTACT, GobiiExtractFilterType.BY_SAMPLE)]
            );

        this.getFilter(FilterParamNames.PROJECTS_BY_CONTACT, GobiiExtractFilterType.BY_SAMPLE)
            .setParentFileItemParams(this.getFilter(FilterParamNames.CONTACT_PI_HIERARCHY_ROOT, GobiiExtractFilterType.BY_SAMPLE))

        this.addFilter(
            FilterParams
                .build(FilterParamNames.PROJECTS,
                    GobiiExtractFilterType.BY_SAMPLE,
                    EntityType.PROJECT)
                .setIsDynamicFilterValue(false)
                .setNameIdLabelType(NameIdLabelType.ALL));


        // ************************************************************************
        // **************************** BY MARKER  *********************************
        this.addFilter(
            FilterParams
                .build(FilterParamNames.CV_DATATYPE,
                    GobiiExtractFilterType.BY_MARKER,
                    EntityType.CV)
                .setIsDynamicFilterValue(false)
                .setCvFilterType(CvFilterType.DATASET_TYPE)
                .setCvFilterValue(CvFilters.get(CvFilterType.DATASET_TYPE))
                .setFilterType(FilterType.NAMES_BY_TYPE_NAME)
                .setNameIdLabelType(NameIdLabelType.SELECT_A)
        );

        this.addFilter(
            FilterParams
                .build(FilterParamNames.MAPSETS,
                    GobiiExtractFilterType.BY_MARKER,
                    EntityType.MAPSET)
                .setIsDynamicFilterValue(false)
                .setNameIdLabelType(NameIdLabelType.NO));

        this.addFilter(
            FilterParams
                .build(FilterParamNames.PLATFORMS,
                    GobiiExtractFilterType.BY_MARKER,
                    EntityType.PLATFORM)
                .setIsDynamicFilterValue(false)
        );

        this.addFilter(
            FilterParams
                .build(FilterParamNames.MARKER_GROUPS,
                    GobiiExtractFilterType.BY_MARKER,
                    EntityType.MARKER_GROUP)
                .setIsDynamicFilterValue(false)
        );


        // ************************************************************************
        // **************************** BY DATASET *********************************
        this.addFilter(
            FilterParams
                .build(FilterParamNames.MAPSETS,
                    GobiiExtractFilterType.WHOLE_DATASET,
                    EntityType.MAPSET)
                .setIsDynamicFilterValue(false)
                .setNameIdLabelType(NameIdLabelType.NO));
    
        this.addFilter(
            FilterParams
                .build(FilterParamNames.CV_JOB_STATUS,
                    GobiiExtractFilterType.WHOLE_DATASET,
                    cvJobStatusCompoundUniqueId.getEntityType())
                .setIsDynamicFilterValue(true)
                .setCvFilterType(cvJobStatusCompoundUniqueId.getCvFilterType())
                .setCvFilterValue(cvJobStatusCompoundUniqueId.getCvFilterValue())
                .setFilterType(FilterType.NAMES_BY_TYPE_NAME)
                .setNameIdLabelType(NameIdLabelType.ALL)
                .setOnLoadFilteredItemsAction((fileItems, payloadFilter) => {

                    /***
                     * This event will cause the initially selected job status to be completed and the
                     *  dataset grid items to be filtered accordingly.
                     *
                     * I am a little uneasy with the implementation. For one thing, it sets the
                     * completedItem's selected property. Ideally, the semantics of the action
                     * should be such that the reducer knows to set the selected property. We're sort
                     * of monkeying with state here. In essence,
                     * we really need a new action and corresponding reducer code to handle this;
                     * I'm also not in comfortable with the fact that we are testing for a filter value
                     * to determine whether or not to apply the initial select state and filter value. Here again,
                     * there should be semantics in the load filtered items action or something that would indicate
                     * that it's an initial load. But that will make things more complicated.
                     *
                     * For now this is fine. If we expand this type of thing to include other types of initial select
                     * actions, we will probably want to revisit the design.
                     *
                     */

                    let returnVal: fileAction.LoadFilterAction = null;

                    if (!payloadFilter) {

                        let completedItem: GobiiFileItem =
                            fileItems.find(fi => fi.getItemName() === "completed");

                        let labelItem: GobiiFileItem =
                            fileItems.find(fi => fi.getExtractorItemType() === ExtractorItemType.LABEL);

                        if (completedItem && labelItem) {

                            returnVal = new fileAction.LoadFilterAction(
                                {
                                    filterId: FilterParamNames.CV_JOB_STATUS,
                                    filter: new PayloadFilter(GobiiExtractFilterType.WHOLE_DATASET,
                                        cvJobStatusCompoundUniqueId,
                                        null,
                                        completedItem.getItemId(),
                                        null,
                                        payloadFilter.entityLasteUpdated,
                                        payloadFilter.pagination)
                                }
                            );

                        }
                    }

                    return returnVal;
                })
        );

        let cvDatasetCompoundUniqueId: GobiiFileItemCompoundId =
            new GobiiFileItemCompoundId(ExtractorItemType.ENTITY,
                EntityType.DATASET,
                EntitySubType.UNKNOWN,
                CvFilterType.UNKNOWN,
                CvFilters.get(CvFilterType.UNKNOWN));
        this.addFilter(FilterParams
            .build(FilterParamNames.DATASET_LIST,
                GobiiExtractFilterType.WHOLE_DATASET,
                EntityType.DATASET)
            .setFilterType(FilterType.ENTITY_LIST)
            .setOnLoadFilteredItemsAction((fileItems, payloadFilter) => {

                let returnVal: fileAction.LoadFilterAction = null;

                if (!payloadFilter || !payloadFilter.relatedEntityFilterValue) {
                    returnVal = new fileAction.LoadFilterAction(
                        {
                            filterId: FilterParamNames.DATASET_LIST_STATUS,
                            filter: new ExtractReadyPayloadFilter(
                                GobiiExtractFilterType.WHOLE_DATASET,
                                cvDatasetCompoundUniqueId,
                                null,
                                null,
                                null,
                                payloadFilter.entityLasteUpdated,
                                payloadFilter.pagination,
                                {
                                    "load": ["completed"],
                                }
                            )
                        }
                    );

                }

                return returnVal;
            }));

        // add dto request to DATASET_LIST filter
        this.getFilter(FilterParamNames.DATASET_LIST, GobiiExtractFilterType.WHOLE_DATASET)
            .setDtoRequestItem(new DtoRequestItemGfi(
                this.getFilter(FilterParamNames.DATASET_LIST,
                    GobiiExtractFilterType.WHOLE_DATASET),
                null,
                new JsonToGfiDataset(this.getFilter(FilterParamNames.DATASET_LIST,
                    GobiiExtractFilterType.WHOLE_DATASET),
                    this)))
            .setDtoRequestService(this.fileItemRequestService);


        // same as previous except configured for paging
        this.addFilter(FilterParams
            .build(FilterParamNames.DATASET_LIST_PAGED,
                GobiiExtractFilterType.WHOLE_DATASET,
                EntityType.DATASET)
            .setFilterType(FilterType.ENTITY_LIST)
            .setOnLoadFilteredItemsAction((fileItems, payloadFilter) => {

                let returnVal: fileAction.LoadFilterAction = null;

                if (!payloadFilter || !payloadFilter.relatedEntityFilterValue) {
                    returnVal = new fileAction.LoadFilterAction(
                        {
                            filterId: FilterParamNames.DATASET_LIST_STATUS,
                            filter: new ExtractReadyPayloadFilter(
                                GobiiExtractFilterType.WHOLE_DATASET,
                                cvDatasetCompoundUniqueId,
                                null,
                                null,
                                null,
                                payloadFilter.entityLasteUpdated,
                                payloadFilter.pagination,
                                {
                                    "load": ["completed"],
                                }
                            )
                        }
                    );

                }

                return returnVal;
            })
            .setIsPaged(true));

        // add dto request to DATASET_LIST_PAGED filter
        this.getFilter(FilterParamNames.DATASET_LIST_PAGED, GobiiExtractFilterType.WHOLE_DATASET)
            .setDtoRequestItem(new DtoRequestItemGfiPaged(
                this.getFilter(FilterParamNames.DATASET_LIST_PAGED,
                    GobiiExtractFilterType.WHOLE_DATASET),
                null,
                new JsonToGfiDataset(this.getFilter(FilterParamNames.DATASET_LIST_PAGED,
                    GobiiExtractFilterType.WHOLE_DATASET),
                    this)))
            .setDtoRequestService(this.pagedDatasetRequestService);


        this.addFilter(FilterParams
            .build(FilterParamNames.DATASET_BY_DATASET_ID,
                GobiiExtractFilterType.WHOLE_DATASET,
                EntityType.DATASET)
            .setFilterType(FilterType.ENTITY_BY_ID));

        this.addFilter(FilterParams
            .build(FilterParamNames.ANALYSES_BY_DATASET_ID,
                GobiiExtractFilterType.WHOLE_DATASET,
                EntityType.ANALYSIS)
            .setFilterType(FilterType.ENTITY_BY_ID));


        this.addFilter(
            FilterParams
                .build(FilterParamNames.CONTACT_PI_FILTER_OPTIONAL,
                    GobiiExtractFilterType.WHOLE_DATASET,
                    EntityType.CONTACT)
                .setExtractorItemType(ExtractorItemType.ENTITY)
                .setIsDynamicFilterValue(true)
                .setIsDynamicDataLoad(false)
                .setEntitySubType(EntitySubType.CONTACT_PRINCIPLE_INVESTIGATOR)
                .setNameIdLabelType(NameIdLabelType.ALL)
                .setIsExtractCriterion(false)
                .setFilterType(FilterType.NAMES_BY_TYPE_NAME)
                .setCvFilterValue("PI"));

        // relate this filter to CONTACT_PI_FILTER_OPTIONAL as parent
        this.addFilter(
            FilterParams
                .build(FilterParamNames.PROJECT_FILTER_OPTIONAL,
                    GobiiExtractFilterType.WHOLE_DATASET,
                    EntityType.PROJECT)
                .setExtractorItemType(ExtractorItemType.ENTITY)
                .setRelatedEntityUniqueId(new GobiiFileItemCompoundId(ExtractorItemType.ENTITY,
                    EntityType.CONTACT,
                    EntitySubType.UNKNOWN,
                    CvFilterType.UNKNOWN,
                    null))
                .setIsDynamicFilterValue(true)
                .setIsDynamicDataLoad(false)
                .setNameIdLabelType(NameIdLabelType.ALL)
                .setIsExtractCriterion(false));

        // relate this filter to PROJECT_FILTER_OPTIONAL as pa\rent
        this.addFilter(
            FilterParams
                .build(FilterParamNames.EXPERIMENT_FILTER_OPTIONAL,
                    GobiiExtractFilterType.WHOLE_DATASET,
                    EntityType.EXPERIMENT)
                .setRelatedEntityUniqueId(new GobiiFileItemCompoundId(ExtractorItemType.ENTITY,
                    EntityType.PROJECT,
                    EntitySubType.UNKNOWN,
                    CvFilterType.UNKNOWN,
                    null))
                .setIsDynamicFilterValue(true)
                .setIsDynamicDataLoad(false)
                .setExtractorItemType(ExtractorItemType.ENTITY)
                .setNameIdLabelType(NameIdLabelType.ALL)
                .setIsExtractCriterion(false));


        this.addFilter(
            FilterParams
                .build(FilterParamNames.DATASET_FILTER_OPTIONAL,
                    GobiiExtractFilterType.WHOLE_DATASET,
                    EntityType.DATASET)
                .setRelatedEntityUniqueId(new GobiiFileItemCompoundId(ExtractorItemType.ENTITY,
                    EntityType.EXPERIMENT,
                    EntitySubType.UNKNOWN,
                    CvFilterType.UNKNOWN,
                    null))
                .setIsDynamicFilterValue(true)
                .setIsDynamicDataLoad(false)
                .setExtractorItemType(ExtractorItemType.ENTITY));


        //Set up hierarchy
        this.getFilter(FilterParamNames.CONTACT_PI_FILTER_OPTIONAL, GobiiExtractFilterType.WHOLE_DATASET)
            .setChildNameIdRequestParams(
                [this.getFilter(FilterParamNames.PROJECT_FILTER_OPTIONAL, GobiiExtractFilterType.WHOLE_DATASET)]
            );

        this.getFilter(FilterParamNames.PROJECT_FILTER_OPTIONAL, GobiiExtractFilterType.WHOLE_DATASET)
            .setParentFileItemParams(this.getFilter(FilterParamNames.CONTACT_PI_FILTER_OPTIONAL, GobiiExtractFilterType.WHOLE_DATASET))
            .setChildNameIdRequestParams(
                [this.getFilter(FilterParamNames.EXPERIMENT_FILTER_OPTIONAL, GobiiExtractFilterType.WHOLE_DATASET)]
            );

        this.getFilter(FilterParamNames.EXPERIMENT_FILTER_OPTIONAL, GobiiExtractFilterType.WHOLE_DATASET)
            .setParentFileItemParams(this.getFilter(FilterParamNames.PROJECT_FILTER_OPTIONAL, GobiiExtractFilterType.WHOLE_DATASET))
            .setChildNameIdRequestParams(
                [this.getFilter(FilterParamNames.DATASET_FILTER_OPTIONAL, GobiiExtractFilterType.WHOLE_DATASET)]
            );


        //for hierarchical items, we need to crate the nameid requests separately from the
        //flat map: they _will_ need to be in the flat map; however, they all need to be
        //useed to set up the filtering hierarchy
        let nameIdRequestParamsContactsPi: FilterParams = FilterParams
            .build(FilterParamNames.CONTACT_PI_HIERARCHY_ROOT,
                GobiiExtractFilterType.WHOLE_DATASET,
                EntityType.CONTACT)
            .setIsDynamicFilterValue(true)
            .setEntitySubType(EntitySubType.CONTACT_PRINCIPLE_INVESTIGATOR);


        let nameIdRequestParamsExperiments: FilterParams = FilterParams
            .build(FilterParamNames.EXPERIMENTS_BY_PROJECT,
                GobiiExtractFilterType.WHOLE_DATASET,
                EntityType.EXPERIMENT)
            .setIsDynamicFilterValue(true)
            .setFilterType(FilterType.NAMES_BY_TYPEID);

        let nameIdRequestParamsDatasets: FilterParams = FilterParams
            .build(FilterParamNames.DATASETS_BY_EXPERIMENT,
                GobiiExtractFilterType.WHOLE_DATASET,
                EntityType.DATASET)
            .setIsDynamicFilterValue(true)
            .setFilterType(FilterType.NAMES_BY_TYPEID);

        //add the individual requests to the map
        // this.addFilter(nameIdRequestParamsContactsPi);
        // this.addFilter(nameIdRequestParamsProjectByPiContact);
        // this.addFilter(nameIdRequestParamsExperiments);
        // this.addFilter(nameIdRequestParamsDatasets);

        //build the parent-child request params graph
        // nameIdRequestParamsContactsPi
        //     .setChildNameIdRequestParams(
        //         [nameIdRequestParamsProjectByPiContact
        //             .setParentFileItemParams(nameIdRequestParamsContactsPi)
        //             .setChildNameIdRequestParams([nameIdRequestParamsExperiments
        //                 .setParentFileItemParams(nameIdRequestParamsProjectByPiContact)
        //                 .setChildNameIdRequestParams([nameIdRequestParamsDatasets
        //                     .setParentFileItemParams(nameIdRequestParamsExperiments)
        //                 ])
        //             ])
        //         ]);


    } // constructor
}
