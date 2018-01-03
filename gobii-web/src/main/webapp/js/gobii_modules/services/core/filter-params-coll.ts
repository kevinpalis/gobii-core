import {Injectable} from "@angular/core";
import {EntitySubType, EntityType} from "../../model/type-entity";
import {GobiiExtractFilterType} from "../../model/type-extractor-filter";
import {CvFilters, CvFilterType} from "../../model/cv-filter-type";
import {FilterParams} from "../../model/file-item-params";
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

    constructor(private store: Store<fromRoot.State>) {

        // For non-hierarchically filtered request params, we just create them simply
        // as we add them to the flat map
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
                    GobiiExtractFilterType.WHOLE_DATASET,
                    EntityType.MAPSET)
                .setIsDynamicFilterValue(false)
                .setNameIdLabelType(NameIdLabelType.NO));

        this.addFilter(
            FilterParams
                .build(FilterParamNames.MAPSETS,
                    GobiiExtractFilterType.BY_SAMPLE,
                    EntityType.MAPSET)
                .setIsDynamicFilterValue(false)
                .setNameIdLabelType(NameIdLabelType.NO));

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
                    GobiiExtractFilterType.BY_SAMPLE,
                    EntityType.PLATFORM)
                .setIsDynamicFilterValue(false)
        );

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

        this.addFilter(
            FilterParams
                .build(FilterParamNames.PROJECTS,
                    GobiiExtractFilterType.BY_SAMPLE,
                    EntityType.PROJECT)
                .setIsDynamicFilterValue(false)
                .setNameIdLabelType(NameIdLabelType.ALL));

        this.addFilter(
            FilterParams
                .build(FilterParamNames.CONTACT_PI,
                    GobiiExtractFilterType.BY_SAMPLE,
                    EntityType.CONTACT)
                .setIsDynamicFilterValue(false)
                .setEntitySubType(EntitySubType.CONTACT_PRINCIPLE_INVESTIGATOR));


        let cvJobStatusCompoundUniqueId: GobiiFileItemCompoundId =
            new GobiiFileItemCompoundId(ExtractorItemType.ENTITY,
                EntityType.CV,
                EntitySubType.UNKNOWN,
                CvFilterType.JOB_STATUS,
                CvFilters.get(CvFilterType.JOB_STATUS));

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
                .setOnLoadFilteredItemsAction((fileItems, filterValue) => {

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

                    if (!filterValue) {

                        let completedItem: GobiiFileItem =
                            fileItems.find(fi => fi.getItemName() === "completed");

                        let labelItem: GobiiFileItem =
                            fileItems.find(fi => fi.getExtractorItemType() === ExtractorItemType.LABEL);

                        if (completedItem && labelItem) {

                            completedItem.setSelected(true);
                            returnVal =  new fileAction.LoadFilterAction(
                                {
                                    filterId: FilterParamNames.CV_JOB_STATUS,
                                    filter: {
                                        gobiiExtractFilterType: GobiiExtractFilterType.WHOLE_DATASET,
                                        gobiiCompoundUniqueId: cvJobStatusCompoundUniqueId,
                                        filterValue: completedItem.getItemId(),
                                        entityLasteUpdated: null
                                    }
                                }
                            );

                        }
                    }

                    return returnVal;
                })
        );

        this.addFilter(FilterParams
            .build(FilterParamNames.DATASET_LIST,
                GobiiExtractFilterType.WHOLE_DATASET,
                EntityType.DATASET)
            .setFilterType(FilterType.ENTITY_LIST));

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

        //for hierarchical items, we need to crate the nameid requests separately from the
        //flat map: they _will_ need to be in the flat map; however, they all need to be
        //useed to set up the filtering hierarchy
        let nameIdRequestParamsContactsPi: FilterParams = FilterParams
            .build(FilterParamNames.CONTACT_PI,
                GobiiExtractFilterType.WHOLE_DATASET,
                EntityType.CONTACT)
            .setIsDynamicFilterValue(true)
            .setEntitySubType(EntitySubType.CONTACT_PRINCIPLE_INVESTIGATOR);


        let nameIdRequestParamsProjectByPiContact: FilterParams = FilterParams
            .build(FilterParamNames.PROJECTS_BY_CONTACT,
                GobiiExtractFilterType.WHOLE_DATASET,
                EntityType.PROJECT)
            .setIsDynamicFilterValue(true)
            .setFilterType(FilterType.NAMES_BY_TYPEID);

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
        this.addFilter(nameIdRequestParamsContactsPi);
        this.addFilter(nameIdRequestParamsProjectByPiContact);
        this.addFilter(nameIdRequestParamsExperiments);
        this.addFilter(nameIdRequestParamsDatasets);

        //build the parent-child request params graph
        nameIdRequestParamsContactsPi
            .setChildNameIdRequestParams(
                [nameIdRequestParamsProjectByPiContact
                    .setParentFileItemParams(nameIdRequestParamsContactsPi)
                    .setChildNameIdRequestParams([nameIdRequestParamsExperiments
                        .setParentFileItemParams(nameIdRequestParamsProjectByPiContact)
                        .setChildNameIdRequestParams([nameIdRequestParamsDatasets
                            .setParentFileItemParams(nameIdRequestParamsExperiments)
                        ])
                    ])
                ]);

    } // constructor
}