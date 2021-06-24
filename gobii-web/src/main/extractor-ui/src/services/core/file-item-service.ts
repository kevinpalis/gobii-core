import { Injectable, ɵConsole } from "@angular/core";
import { Store } from "@ngrx/store";
import "rxjs/add/operator/concat";
import "rxjs/add/operator/expand";
import { Observable } from "rxjs/Observable";
import { CvFilterType } from "../../model/cv-filter-type";
import { HeaderStatusMessage } from "../../model/dto-header-status-message";
import { EntityStats } from "../../model/entity-stats";
import { FilterParamNames } from "../../model/file-item-param-names";
import { FilterParams } from "../../model/filter-params";
import { FilterType } from "../../model/filter-type";
import { GobiiFileItem } from "../../model/gobii-file-item";
import { GobiiFileItemCompoundId } from "../../model/gobii-file-item-compound-id";
import { GobiiFileItemEntityRelation } from "../../model/gobii-file-item-entity-relation";
import { NameIdLabelType } from "../../model/name-id-label-type";
import { Pagination } from "../../model/payload/pagination";
import { EntitySubType, EntityType } from "../../model/type-entity";
import { GobiiExtractFilterType } from "../../model/type-extractor-filter";
import { ExtractorItemType } from "../../model/type-extractor-item";
import { ProcessType } from "../../model/type-process";
import { StatusLevel } from "../../model/type-status-level";
import { PayloadFilter } from "../../store/actions/action-payload-filter";
import * as fileItemActions from '../../store/actions/fileitem-action';
import * as historyAction from '../../store/actions/history-action';
import * as fromRoot from '../../store/reducers';
import { FilterHistory } from "../../store/reducers/history-reducer";
import { Labels } from "../../views/entity-labels";
import { DtoRequestItemEntityStats, EntityRequestType } from "../app/dto-request-item-entity-stats";
import { DtoRequestItem } from "./dto-request-item";
import { DtoRequestService } from "./dto-request.service";
import { FilterParamsColl } from "./filter-params-coll";
import { NameIdService } from "./name-id-service";
import { Crop } from 'src/model/crop';


@Injectable()
export class FileItemService {
    

    private readonly NONE_ITEM_ITEM_ID: string = "-1";

    constructor(private nameIdService: NameIdService,
                private entityStatsService: DtoRequestService<EntityStats>,
                private fileItemRequestService: DtoRequestService<GobiiFileItem[]>,
                private store: Store<fromRoot.State>,
                private filterParamsColl: FilterParamsColl) {

        // For non-hierarchically filtered request params, we just create them simply
        // as we add them to the flat map

    } // constructor

    public loadFilter(gobiiExtractFilterType: GobiiExtractFilterType, filterParamsName: FilterParamNames, filterValue: string) {

        let filterParams: FilterParams = this.filterParamsColl.getFilter(filterParamsName, gobiiExtractFilterType);

        if (filterParams) {

            let loadAction: fileItemActions.LoadFilterAction = new fileItemActions.LoadFilterAction(
                {
                    filterId: filterParams.getQueryName(),
                    filter: new PayloadFilter(
                        gobiiExtractFilterType,
                        filterParams.getTargetEtityUniqueId(),
                        filterParams.getRelatedEntityUniqueId(),
                        filterValue,
                        null,
                        null,
                        null
                    )
                }
            );

            this.store.dispatch(loadAction)

        } else {
            this.store.dispatch(new historyAction.AddStatusMessageAction("Error loading filter: there is no query params object for query "
                + filterParamsName
                + " with extract filter type "
                + GobiiExtractFilterType[gobiiExtractFilterType]));
        }
    }

    public getForFilter(filterParamName: FilterParamNames): Observable<GobiiFileItem[]> {

        //Wrapping an Observable around the select functions just doesn't work. So at leaset this
        //function can encapsualte getting the correct selector for the filter
        let returnVal: any;

        switch (filterParamName) {

            case FilterParamNames.MARKER_GROUPS:
                returnVal = this.store.select(fromRoot.getMarkerGroups);
                break;

            case FilterParamNames.PROJECTS:
                returnVal = this.store.select(fromRoot.getProjects);
                break;

            case FilterParamNames.PROJECTS_BY_CONTACT:
                returnVal = this.store.select(fromRoot.getProjectsByPI);
                break;

            case FilterParamNames.EXPERIMENTS_BY_PROJECT:
                returnVal = this.store.select(fromRoot.getExperimentsByProject);
                break;

            case FilterParamNames.EXPERIMENTS:
                returnVal = this.store.select(fromRoot.getExperiments);
                break;

            case FilterParamNames.DATASETS_BY_EXPERIMENT:
                returnVal = this.store.select(fromRoot.getDatasetsByExperiment);
                break;

            case FilterParamNames.PLATFORMS:
                returnVal = this.store.select(fromRoot.getPlatforms);
                break;

            case FilterParamNames.CV_DATATYPE:
                returnVal = this.store.select(fromRoot.getCvTermsDataType);
                break;

            case FilterParamNames.CV_JOB_STATUS:
                returnVal = this.store.select(fromRoot.getCvTermsJobStatus);
                break;

            case FilterParamNames.MAPSETS:
                returnVal = this.store.select(fromRoot.getMapsets);
                break;

            case FilterParamNames.CONTACT_PI_HIERARCHY_ROOT:
                returnVal = this.store.select(fromRoot.getPiContacts);
                break;

            case FilterParamNames.CONTACT_PI_FILTER_OPTIONAL:
                returnVal = this.store.select(fromRoot.getPiContactsFilterOptional);
                break;

            case FilterParamNames.PROJECT_FILTER_OPTIONAL:
                returnVal = this.store.select(fromRoot.getProjectsFilterOptional);
                break;

            case FilterParamNames.EXPERIMENT_FILTER_OPTIONAL:
                returnVal = this.store.select(fromRoot.getExperimentsFilterOptional);
                break;

            case FilterParamNames.CROP_TYPE:
                returnVal = this.store.select(fromRoot.getCrops);
                break;

            default:
                returnVal = this.store.select(fromRoot.getAllFileItems);
                break;

        }

        return returnVal;

        // THIS IS THE THING THAT DIDN'T WORK; IT'S WORTH KEEPING IT AROUND FOR REFERENCE.
        // return Observable.create(observer => {
        //     let filteredItems: GobiiFileItem[] = [];
        //     this.store
        //         .select(fromRoot.getAllFileItems)
        //         .subscribe(fileItems => {
        //                 let nameIdRequestParams: FileItemParams = this.nameIdRequestParams.get(filterParamNames);
        //                 if (nameIdRequestParams) {
        //                     if (!nameIdRequestParams.getIsDynamicFilterValue()) {
        //                         filteredItems = fileItems.filter(fi => fi.compoundIdeEquals(nameIdRequestParams))
        //                     } else {
        //                         this.store.select(fromRoot.getFileItemsFilters)
        //                             .subscribe(filters => {
        //                                 if (filters[nameIdRequestParams.getQueryName()]) {
        //                                     let filterValue: string = filters[nameIdRequestParams.getQueryName()].filterValue;
        //                                     filteredItems = fileItems.filter(
        //                                         fi =>
        //                                             fi.compoundIdeEquals(nameIdRequestParams)
        //                                             && fi.getParentItemId() === filterValue);
        //
        //                                     if (filteredItems.length <= 0) {
        //                                         filteredItems = fileItems.filter(e =>
        //                                             ( e.getExtractorItemType() === ExtractorItemType.ENTITY
        //                                                 && e.getEntityType() === EntityType.DATASET
        //                                                 //                    && e.getParentItemId() === experimentId
        //                                                 && e.getProcessType() === ProcessType.DUMMY))
        //                                             .map(fi => fi);
        //
        //                                     }
        //                                     observer.next(filteredItems)
        //                                 } // if filters have been populated
        //                             });
        //
        //                     }
        //                 }
        //             } //Store.subscribe
        //         );
        //
        //
        //
        // }) //Observable.create()

    }


    public setItemLabelType(gobiiExtractFilterType: GobiiExtractFilterType,
                            filterParamNames: FilterParamNames,
                            nameIdLabelType: NameIdLabelType) {

        let nameIdRequestParamsFromType: FilterParams = this.filterParamsColl.getFilter(filterParamNames, gobiiExtractFilterType);
        if (nameIdRequestParamsFromType) {
            nameIdRequestParamsFromType.setNameIdLabelType(nameIdLabelType);
        } else {
            this.store.dispatch(new historyAction.AddStatusMessageAction("Error setting label type: there is no query params object for query "
                + filterParamNames
                + " with extract filter type "
                + GobiiExtractFilterType[gobiiExtractFilterType]));
        }

    }

    public loadNameIdsFromFilterParams(gobiiExtractFilterType: GobiiExtractFilterType,
                                       filterParamName: FilterParamNames,
                                       filterValue: string,
                                       cropType: string) {

        let nameIdRequestParamsFromType: FilterParams = this.filterParamsColl.getFilter(filterParamName, gobiiExtractFilterType);

        if (nameIdRequestParamsFromType) {

            this.makeNameIdLoadActions(gobiiExtractFilterType, filterParamName, filterValue, cropType)
                .subscribe(action => {
                    if (action) {
                        this.store.dispatch(action);
                    }
                });

        } else {
            this.store.dispatch(new historyAction.AddStatusMessageAction("Error loading with filter params: there is no query params object for query "
                + filterParamName
                + " with extract filter type "
                + GobiiExtractFilterType[gobiiExtractFilterType]));
        }
    }

    public loadCrops(gobiiExtractFilterType: GobiiExtractFilterType, crops: Crop[], selectedIndex: number ) {
        let cropFileSelection: GobiiFileItem[] = [];
        let targetSelected: GobiiFileItem = null;
        crops.filter(crop => crop.userAuthorized).forEach(
            (crop, index) => {

                let fileItem: GobiiFileItem = GobiiFileItem
                    .build(gobiiExtractFilterType, ProcessType.CREATE)
                    .setEntityType(EntityType.CROP)
                    .setCvFilterType(CvFilterType.UNKNOWN)
                    .setExtractorItemType(ExtractorItemType.CROP_TYPE)
                    .setItemName(crop.cropType)
                    .setItemId(crop.cropType)
                    .setFileItemUniqueId(crop.cropType)
                    .setIsExtractCriterion(true)
                    .setCropType(null);
                if (selectedIndex && index == selectedIndex) {
                    fileItem.setSelected(true);
                    targetSelected = fileItem;
                }
                cropFileSelection.push(
                    fileItem
                );
            }
        );
        let filterParamsToLoad: FilterParams = this.filterParamsColl.getFilter(FilterParamNames.CROP_TYPE, GobiiExtractFilterType.UNKNOWN);

        let loadAction: fileItemActions.LoadFileItemListWithFilterAction = new fileItemActions.LoadFileItemListWithFilterAction(
            {
                gobiiFileItems: cropFileSelection,
                filterId: FilterParams,
                filter: new PayloadFilter(
                    gobiiExtractFilterType,
                    filterParamsToLoad.getTargetEtityUniqueId(),
                    null,
                    null,
                    null,
                    null,
                    null
                )
            }
        );
        this.store.dispatch(loadAction);
        if (targetSelected) {
            this.replaceFileItemByCompoundId(targetSelected);
        }
        
    }

    public loadFileItem(gobiiFileItem: GobiiFileItem, selectForExtract: boolean) {


        let loadAction: fileItemActions.LoadFileItemtAction = new fileItemActions.LoadFileItemtAction(
            {
                gobiiFileItem: gobiiFileItem,
                selectForExtract: selectForExtract
            }
        );


        this.store.dispatch(loadAction);

    }

    /***
     * This is a hard-repalce: the item is not just removed from the extract, but nuked entirely from
     * the store
     * @param {GobiiFileItem} gobiiFileItem
     * @param {boolean} selectForExtract
     */
    public replaceFileItemByCompoundId(gobiiFileItem: GobiiFileItem) {


        let loadAction: fileItemActions.ReplaceItemOfSameCompoundIdAction = new fileItemActions.ReplaceItemOfSameCompoundIdAction(
            {
                gobiiFileitemToReplaceWith: gobiiFileItem
            }
        );


        this.store.dispatch(loadAction);

    }

    public unloadFileItemFromExtract(gobiiFileItem: GobiiFileItem) {


        let loadAction: fileItemActions.RemoveFromExtractAction = new fileItemActions.RemoveFromExtractAction(gobiiFileItem);

        this.store.dispatch(loadAction);

    }


    /***
     * The purpose of this method is to create an observable of filter load actions for a given filter
     * with a given filter value. Conceptually, the filtername and value passed to this function are the
     * initial target filter. For example, if the user selects a PI (contact) for the dataset grid, the filter name
     * and filter value (contactId) will make their way (though the effect for the action) to this method. The contactId filter
     * value passed in will become the "target" value for the filter. The idea is that the target reflects
     * the value that the user actually selected for a given filter. The primary observable creates the
     * action for this filter. The makeChildActions() method with which it is concatenated performs further
     * processing on the filter's children. The makeChildActions() method will take the first child filter of the
     * target filter and pass it along with what is now considered the "parent" filter value (still contactId) into the recurseFilters()
     * method. There the name of the game is to set up the related filter values. For example, the contract filter
     * will have a child filter for projects. recurseFilters() will set the related filter value of the project
     * filter to the contact id of the target (parent) filter. The recurseFilters() method will now recursively
     * process the children of the project filter and its children. For the subsequent children, it will figure
     * out how to set the related IDs (the new parent ids) based on the results of the filter passed to it. For
     * example, when the project filter is passed to recurseFilters(), recurseFilters() will also retrieve all
     * file items of type project with that contact ID as their related entity. It will then use the 0th such
     * file item's item id as the new parentid of the project filter's child filters. The filter's target filter
     * value is set in only two places: in this method, and in recruseFilters(). recruseFilters() will set it to null
     * in the condition where the related filter value has changed. Thus, for example, if the user selects a new
     * PI value, the contactId in the project filter's related filter value will no longer match. In this circumstance,
     * the target filter value of the project filter has been invalidated by the new related (parent) id value,
     * and so the target filter value of the project filter will be set to null. What this means in practice is that
     * only related IDs are cascaded; target IDs are selected by the user or invalidated by the user's selection of
     * a new parent ID. Moreover, for the most part the target filter values are used for filtering the content of the
     * grid whereas the related IDs are used for filtering the content of the grid's drop downs.
     *
     * The organization of these nethods is a bit haphazard and represents the atheoretical way in which they evolved.
     * It should be that there is just one method and one obsevable here that handles everything more elegantly. But
     * for now this all seems to work.
     *
     * @param {GobiiExtractFilterType} gobiiExtractFilterType
     * @param {FilterParamNames} filterParamName
     * @param {string} newTargetFilterValue
     * @returns {Observable<LoadFileItemListWithFilterAction>}
     */
    public makeFileActionsFromFilterParamName(gobiiExtractFilterType: GobiiExtractFilterType,
                                              filterParamName: FilterParamNames,
                                              newTargetFilterValue: string,
                                              cropType: string) : Observable<fileItemActions.LoadFileItemListWithFilterAction> {

        return Observable.create(observer => {

            let parentFilterParams: FilterParams = this.filterParamsColl.getFilter(filterParamName, gobiiExtractFilterType);

            this.store
                .select(fromRoot.getFileItemsFilters)
                .subscribe(filters => {

                    let relatedEntityFiltervalueFromState:string = null;
                    let stateFilter:PayloadFilter = filters[filterParamName.toString()];
                    if( stateFilter ) {
                        relatedEntityFiltervalueFromState = stateFilter.relatedEntityFilterValue;
                    }


                    if (parentFilterParams) {

                        //parentFilterParams.setTargetEntityFilterValue(parentFilterValue);
                        let parentLoadAction: fileItemActions.LoadFilterAction = new fileItemActions.LoadFilterAction(
                            {
                                filterId: parentFilterParams.getQueryName(),
                                filter: new PayloadFilter(
                                    gobiiExtractFilterType,
                                    parentFilterParams.getTargetEtityUniqueId(),
                                    parentFilterParams.getRelatedEntityUniqueId(),
                                    relatedEntityFiltervalueFromState,
                                    newTargetFilterValue,
                                    null, //not sure about this
                                    null
                                )
                            }
                        );

                        observer.next(parentLoadAction);
                        observer.complete();


                    } else {
                        this.store.dispatch(new historyAction.AddStatusMessageAction("Undefined FileItemParams filter: "
                            + filterParamName.toString()
                            + " for extract type " + GobiiExtractFilterType[gobiiExtractFilterType]));
                    }
                });

        }).concat(
            this.makeChildActions(gobiiExtractFilterType, filterParamName, newTargetFilterValue, cropType)
        ); // observable
    }

    public makeChildActions(gobiiExtractFilterType: GobiiExtractFilterType,
                            filterParamName: FilterParamNames,
                            parentFilterValue: string,
                            cropType: string): Observable<fileItemActions.LoadFileItemListWithFilterAction> {

        let returnVal: Observable<fileItemActions.LoadFileItemListWithFilterAction>;

        let parentFilterParams: FilterParams = this.filterParamsColl.getFilter(filterParamName, gobiiExtractFilterType);


        if (parentFilterParams) {

            // Now we process child filters
            let filterParamsToProcess: FilterParams = null;

            if (parentFilterParams.getIsDynamicFilterValue() || parentFilterParams.getIsDynamicFilterValue()) {


                if (parentFilterParams.getChildFileItemParams() && parentFilterParams.getChildFileItemParams().length === 1) {
                    filterParamsToProcess = parentFilterParams.getChildFileItemParams()[0];
                    if (filterParamsToProcess.getIsDynamicDataLoad()) {
                        returnVal = this.makeFileItemActionsFromNameIds(gobiiExtractFilterType,
                            filterParamsToProcess,
                            parentFilterValue,
                            true,
                            cropType);
                    } else {

                        returnVal = this.recurseFilters(gobiiExtractFilterType,
                            filterParamsToProcess,
                            parentFilterValue,
                            filterParamsToProcess.getIsDynamicFilterValue());
                    }
                } else {
                    this.store.dispatch(new historyAction.AddStatusMessageAction("The dynamic filter does not have children: "
                        + filterParamName.toString()
                        + " for extract type " + GobiiExtractFilterType[gobiiExtractFilterType]));
                }
            } else {

                return Observable.of(null);
            }


        } else {
            this.store.dispatch(new historyAction.AddStatusMessageAction("Undefined FileItemParams filter: "
                + filterParamName.toString()
                + " for extract type " + GobiiExtractFilterType[gobiiExtractFilterType]));
        }

        return returnVal;
    }


    public makeNameIdLoadActions(gobiiExtractFilterType: GobiiExtractFilterType,
                                 filterParamName: FilterParamNames,
                                 filterValue: string,
                                 cropType: string): Observable<fileItemActions.LoadFileItemListWithFilterAction> {

        let nameIdRequestParamsFromType: FilterParams = this.filterParamsColl.getFilter(filterParamName, gobiiExtractFilterType);

        if (nameIdRequestParamsFromType) {
            return this.makeFileItemActionsFromNameIds(gobiiExtractFilterType,
                nameIdRequestParamsFromType,
                filterValue,
                true,
                cropType);
        } else {
            this.store.dispatch(new historyAction.AddStatusMessageAction("Undefined FileItemParams filter: "
                + filterParamName.toString()
                + " for extract type " + GobiiExtractFilterType[gobiiExtractFilterType]))
        }
    }


    /***
     * This is the core retrieval method for nameIds, which is the bread-and butter of the extractor UI.
     * This method uses the /entity/lastmodified/{entityName} and the client retrieval history to determine
     * whether or not data need to be retrieved from the server or can be retrieved from the local store.
     * This method does not actually add anything to the store. Rather, it is consumed by other places in the
     * code that need to add GobiiFileItems for particular entities to the store. So this method returns an
     * observable of actions. Thus, when the data are retrieved from the server, there are actions that both
     * add the items to the store and set the filter value in the store. When they are retrieved from the
     * store, the action that is next()'d to the caller only updates the filter value in the store. Because
     * the selectors use the filter values in the store, it all works out. It was discovered in the process
     * of development that changing the content of the history store from could within a select() from the
     * history store causes extremely strange things to happen. I'm sure this is a general rule with ngrx/store:
     * Any code within a select() must absolutely not generate side effects that change the result of
     * the select(). Think of this as a variation on the strange things that happen when you try to modify an
     * array within a loop that is iterating that same array.
     * @param {GobiiExtractFilterType} gobiiExtractFilterType
     * @param {FilterParams} filterParamsToLoad
     * @param {string} filterValue
     * @param {boolean} recurse
     * @returns {Observable<LoadFileItemListWithFilterAction>}
     */
    private makeFileItemActionsFromNameIds(gobiiExtractFilterType: GobiiExtractFilterType,
                                           filterParamsToLoad: FilterParams,
                                           filterValue: string,
                                           recurse: boolean,
                                           cropType: string): Observable<fileItemActions.LoadFileItemListWithFilterAction> {


        return Observable.create(observer => {

            // if (filterParamsToLoad.getIsDynamicFilterValue()) {
            //     filterParamsToLoad.setRelatedEntityFilterValue(filterValue);
            // }


            /***
             * In the next refactoring we probably want to use the last modified call
             * that gives the datetime stamps for _all_entities, because I'm sure the setup/teardown of the
             * http request is a lot more expensive, cumulatively, than the extra payload. The trick there is that we'd need to cache the
             * entire set of lastmodified dates in such a way that they would get refreshed at the start of a "transaction,"
             * where a transaction for this purpose is ill-defined. Does it mean when we switch from one extract type to another?
             * There is actually a way to do this when the export type tabs are controlled by the angular router, because for a
             * given navigational path transition we could reset the server last modified dates in the store. For that matter,
             * with that model, the update of the items in the store could be driven from when the collection of lastmodified
             * dates are modified in the store: that is, when you switch from one extract type to another (or some other
             * consequential UI event), you dispatch a fresh collection of lastmodified dates, one per each entity-filter type to
             * the store. There is then an effect from that dispatch action that iterates all the query filters: if the
             * lastmodified for a given filter's entity type has changed, the effect requests a refresh of the filter's items.
             */
            //this.entityStatsService.get(new DtoRequestItemEntityStats(
            //    EntityRequestType.LasetUpdated,
            //    filterParamsToLoad.getEntityType(),
            //    null,
            //    null))
            //    .subscribe(entityStats => {

                    // this.store.select(fromRoot.getFiltersRetrieved)
                    //     .subscribe(filterHistoryItems => {

                    //         let fileHistoryItem: FilterHistory =
                    //             filterHistoryItems.find(fhi =>
                    //                 fhi.gobiiExtractFilterType === filterParamsToLoad.getGobiiExtractFilterType()
                    //                 && fhi.filterId === filterParamsToLoad.getQueryName()
                    //                 && fhi.filterValue === filterValue
                    //             );

                            //let disregardDateSensitiveQueryingForNow = true; //TODO: changed this to tru --rnduldulao
                            // if (disregardDateSensitiveQueryingForNow ||
                            //     (
                            //         (!fileHistoryItem) ||
                            //         (entityStats.lastModified > fileHistoryItem.entityLasteUpdated)
                            //     )
                            // ) {
                                // Either the data have never been retrieved at all for a given filter value,
                                // or the server-side entity has been updated. So we shall refresh the
                                // data and dispatch both the new filter value and the
                                //BEGIN: nameIdService.get()
                                this.nameIdService.get(filterParamsToLoad, filterValue)
                                    .subscribe(nameIds => {

                                            let minEntityLastUpdated: Date;
                                            let fileItems: GobiiFileItem[] = [];
                                            if (nameIds && (nameIds.length > 0)) {

                                                nameIds.forEach(nameIdItem => {

                                                    let entityRelation: GobiiFileItemEntityRelation = null;

                                                    // the server method for the particular nameid retrieval in question will have had
                                                    // to have added the fk entity type and id value
                                                    if (nameIdItem.fkEntityType && nameIdItem.fkId) {

                                                        let gobiiFileItemCompoundUniqueId: GobiiFileItemCompoundId = null;

                                                        if (filterParamsToLoad.getParentFileItemParams()) {
                                                            gobiiFileItemCompoundUniqueId = filterParamsToLoad.getParentFileItemParams().getTargetEtityUniqueId();
                                                            entityRelation = GobiiFileItemEntityRelation
                                                                .fromGobiiFileItemCompoundId(gobiiFileItemCompoundUniqueId)
                                                                .setRelatedEntityId(nameIdItem.fkId);
                                                        }
                                                        /* else {
                                                                                                                   gobiiFileItemCompoundUniqueId = new GobiiFileItemCompoundId(ExtractorItemType.ENTITY,
                                                                                                                       nameIdItem.fkEntityType,
                                                                                                                       EntitySubType.UNKNOWN,
                                                                                                                       CvFilterType.UNKNOWN,
                                                                                                                       null);
                                                                                                               }*/


                                                    }
                                                    //console.log("Creating file item: " + nameIdItem.id + ", " + nameIdItem.name + " for crop " + cropType);
                                                    let currentFileItem: GobiiFileItem =
                                                        GobiiFileItem.build(
                                                            gobiiExtractFilterType,
                                                            ProcessType.CREATE)
                                                            .setExtractorItemType(ExtractorItemType.ENTITY)
                                                            .setEntityType(filterParamsToLoad.getEntityType())
                                                            .setEntitySubType(filterParamsToLoad.getEntitySubType())
                                                            .setCvFilterType(filterParamsToLoad.getCvFilterType())
                                                            .setItemId(nameIdItem.id)
                                                            .setItemName(nameIdItem.name)
                                                            //.setSelected(false)
                                                            .setRequired(false)
                                                            .setParentItemId(filterValue)
                                                            .setIsExtractCriterion(filterParamsToLoad.getIsExtractCriterion())
                                                            .withRelatedEntity(entityRelation)
                                                            .setCropType(cropType);


                                                    fileItems.push(currentFileItem);
                                                });

                                                minEntityLastUpdated = new Date(Math.min.apply(null, nameIds
                                                    .map(nameId => nameId.entityLasetModified)));



                                                if (filterParamsToLoad.getMameIdLabelType() != NameIdLabelType.UNKNOWN) {

                                                    let entityName: string = "";
                                                    if (filterParamsToLoad.getCvFilterType() !== CvFilterType.UNKNOWN) {
                                                        entityName += Labels.instance().cvFilterNodeLabels[filterParamsToLoad.getCvFilterType()];
                                                    } else if (filterParamsToLoad.getEntitySubType() !== EntitySubType.UNKNOWN) {
                                                        entityName += Labels.instance().entitySubtypeNodeLabels[filterParamsToLoad.getEntitySubType()];
                                                    } else {
                                                        entityName += Labels.instance().entityNodeLabels[filterParamsToLoad.getEntityType()];
                                                    }

                                                    let label: string = "";
                                                    switch (filterParamsToLoad.getMameIdLabelType()) {

                                                        case NameIdLabelType.SELECT_A:
                                                            label = "Select a " + entityName;
                                                            break;

                                                        // we require that these entity labels all be in the singular
                                                        case NameIdLabelType.ALL:
                                                            label = "All " + entityName + "s";
                                                            break;

                                                        case NameIdLabelType.NO:
                                                            label = "No " + entityName;
                                                            break;

                                                        default:
                                                            this.store.dispatch(new historyAction.AddStatusAction(new HeaderStatusMessage("Unknown label type "
                                                                + NameIdLabelType[filterParamsToLoad.getMameIdLabelType()], null, null)));

                                                    }


                                                    let labelFileItem: GobiiFileItem = GobiiFileItem
                                                        .build(gobiiExtractFilterType, ProcessType.CREATE)
                                                        .setEntityType(filterParamsToLoad.getEntityType())
                                                        .setEntitySubType(filterParamsToLoad.getEntitySubType())
                                                        .setCvFilterType(filterParamsToLoad.getCvFilterType())
                                                        .setExtractorItemType(ExtractorItemType.LABEL)
                                                        .setItemName(label)
                                                        .setIsExtractCriterion(filterParamsToLoad.getIsExtractCriterion())
                                                        .setParentItemId(filterValue)
                                                        .setItemId("0")
                                                        .setCropType(cropType);


                                                    fileItems.unshift(labelFileItem);
                                                    //.selectedFileItemId = "0";

                                                }

                                            }

                                            //With reference to GSD-70, Not loading None type just for marker groups.
                                            if(filterParamsToLoad.getQueryName() !== FilterParamNames.MARKER_GROUPS) {

                                                let noneFileItem: GobiiFileItem = GobiiFileItem
                                                    .build(gobiiExtractFilterType, ProcessType.DUMMY)
                                                    .setExtractorItemType(ExtractorItemType.ENTITY)
                                                    .setEntityType(filterParamsToLoad.getEntityType())
                                                    .setItemId(this.NONE_ITEM_ITEM_ID)
                                                    .setItemName("<none>")
                                                    .setIsExtractCriterion(filterParamsToLoad.getIsExtractCriterion())
                                                    .setParentItemId(filterValue)
                                                    .setCropType(cropType);

                                                fileItems.push(noneFileItem);
                                            }

                                            let parentId: string = fileItems[0].getItemId();
                                            //filterParamsToLoad.setTargetEntityFilterValue(parentId && +parentId > 0 ? parentId : null);
                                            let targetEntityFilterValue: string = parentId && +parentId > 0 ? parentId : null;
                                            let loadAction: fileItemActions.LoadFileItemListWithFilterAction = new fileItemActions.LoadFileItemListWithFilterAction(
                                                {
                                                    gobiiFileItems: fileItems,
                                                    filterId: filterParamsToLoad.getQueryName(),
                                                    filter: new PayloadFilter(
                                                        gobiiExtractFilterType,
                                                        filterParamsToLoad.getTargetEtityUniqueId(),
                                                        filterParamsToLoad.getRelatedEntityUniqueId(),
                                                        filterValue,
                                                        targetEntityFilterValue,
                                                        minEntityLastUpdated,
                                                        null
                                                    )
                                                }
                                            );

                                            observer.next(loadAction);

                                            // if there are children, we will load their data as well
                                            if (recurse) {

                                                if (filterParamsToLoad
                                                        .getChildFileItemParams()
                                                        .filter(rqp => rqp.getFilterType() === FilterType.NAMES_BY_TYPEID)
                                                        .length > 0) {


                                                    for (let idx: number = 0;
                                                         idx < filterParamsToLoad.getChildFileItemParams().length;
                                                         idx++) {
                                                        let rqp: FilterParams = filterParamsToLoad.getChildFileItemParams()[idx];
                                                        if (rqp.getFilterType() === FilterType.NAMES_BY_TYPEID) {
                                                            //rqp.setRelatedEntityFilterValue(parentId);

                                                            this.makeFileItemActionsFromNameIds(gobiiExtractFilterType,
                                                                rqp,
                                                                parentId,
                                                                true,
                                                                cropType)
                                                                .subscribe(fileItems => observer.next(fileItems));
                                                        }
                                                    }
                                                    ;
                                                }

                                            } // if we are recursing

                                        }, // Observer=>next
                                        responseHeader => {
                                            this.store.dispatch(new historyAction.AddStatusAction(responseHeader));
                                        }); // subscribe
                        //     } else {
                        //         // The data for given filter value exist and do not not need to be
                        //         // updated. So here we shall dispatch only the new filter value.
                        //         // This action will not update the filter history -- only the filter value
                        //         //BEGIN: nameIdService.get()


                        //         let loadAction: fileItemActions.LoadFilterAction = new fileItemActions.LoadFilterAction(
                        //             {
                        //                 filterId: filterParamsToLoad.getQueryName(),
                        //                 filter: new PayloadFilter(
                        //                     gobiiExtractFilterType,
                        //                     filterParamsToLoad.getTargetEtityUniqueId(),
                        //                     filterParamsToLoad.getRelatedEntityUniqueId(),
                        //                     filterValue,
                        //                     null,
                        //                     fileHistoryItem.entityLasteUpdated,
                        //                     null
                        //                 )
                        //             }
                        //         );
                        //         observer.next(loadAction);

                        //         if (recurse) {
                        //             if (filterParamsToLoad
                        //                     .getChildFileItemParams()
                        //                     .filter(rqp => rqp.getFilterType() === FilterType.NAMES_BY_TYPEID)
                        //                     .length > 0) {


                        //                 // we need to set the current filter in state, but with respect to
                        //                 // gobiiFileItems, it should be a null op

                        //                 //Because we don't have the data freshly from the sever, we shall need
                        //                 //to get the "parentId" from the file items we have in the store
                        //                 this.store.select(fromRoot.getAllFileItems)
                        //                     .subscribe(allFileItems => {

                        //                         // Get the parent item id from the store;
                        //                         // however, this will only work if the parent items have been loaded
                        //                         // already.
                        //                         let candidateParentFileItems: GobiiFileItem[] =
                        //                             allFileItems.filter(fi =>
                        //                                 filterParamsToLoad.getTargetEtityUniqueId().compoundIdeEquals(fi)
                        //                                 && fi.getParentItemId() === filterValue
                        //                             );

                        //                         let childItemsFilterValue: string = null;
                        //                         if (candidateParentFileItems.length > 0) {
                        //                             childItemsFilterValue = candidateParentFileItems[0].getItemId();
                        //                         }

                        //                         for (let idx: number = 0;
                        //                              idx < filterParamsToLoad.getChildFileItemParams().length;
                        //                              idx++) {
                        //                             let rqp: FilterParams = filterParamsToLoad.getChildFileItemParams()[idx];
                        //                             if (rqp.getFilterType() === FilterType.NAMES_BY_TYPEID) {
                        //                                 //rqp.setRelatedEntityFilterValue(childItemsFilterValue);

                        //                                 this.makeFileItemActionsFromNameIds(gobiiExtractFilterType,
                        //                                     rqp,
                        //                                     childItemsFilterValue,
                        //                                     true,
                        //                                     cropType)
                        //                                     .subscribe(fileItems => observer.next(fileItems));
                        //                             }
                        //                         }

                        //                     }).unsubscribe();//select all file items

                        //             } // if we have child filters


                        //         } // if we are recursing

                        //     }// if-else we need to refresh from server or rely on what's in the store already
                        //     //END: nameIdService.get()

                        // })
                        // .unsubscribe(); // get filter history items


             //   }); //subscribe get entity stats

        });//return Observer.create

    } // make file items from query


    private recurseFilters(gobiiExtractFilterType: GobiiExtractFilterType,
                           filterParamsToLoad: FilterParams,
                           parentFilterValue: string,
                           recurse: boolean): Observable<fileItemActions.LoadFileItemListWithFilterAction> {


        return Observable.create(observer => {

            // if (filterParamsToLoad.getRelatedEntityFilterValue() !== parentFilterValue) {
            //     filterParamsToLoad.setRelatedEntityFilterValue(parentFilterValue);
            //     filterParamsToLoad.setTargetEntityFilterValue(null);
            // }

            let loadAction: fileItemActions.LoadFilterAction = new fileItemActions.LoadFilterAction(
                {
                    filterId: filterParamsToLoad.getQueryName(),
                    filter: new PayloadFilter(
                        gobiiExtractFilterType,
                        filterParamsToLoad.getTargetEtityUniqueId(),
                        filterParamsToLoad.getRelatedEntityUniqueId(),
                        parentFilterValue,
                        null,
                        null, //not sure about this
                        null
                    )
                }
            );
            observer.next(loadAction);

            if (recurse) {
                if (filterParamsToLoad
                        .getChildFileItemParams()
                        //.filter(rqp => rqp.getFilterType() === FilterType.NAMES_BY_TYPEID)
                        .length > 0) {


                    // we need to set the current filter in state, but with respect to
                    // gobiiFileItems, it should be a null op

                    //Because we don't have the data freshly from the sever, we shall need
                    //to get the "parentId" from the file items we have in the store
                    this.store.select(fromRoot.getAllFileItems)
                        .subscribe(allFileItems => {

                            // Get the parent item id from the store;
                            // however, this will only work if the parent items have been loaded
                            // already.
                            //let's make everything 100% explicit. Our comments here will use the example of
                            //filtering projects by selected contact
                            // these variables are redundant but I want to make this 100% unambigous what it means
                            // and how we are using it


                            // For example, the coupound unique ID for Contacts
                            let parentEntityCompoundUniqueId: GobiiFileItemCompoundId = this.filterParamsColl.getFilter(
                                filterParamsToLoad.getParentFileItemParams().getQueryName(),
                                gobiiExtractFilterType
                            ).getTargetEtityUniqueId();


                            // for example, filter to only those file items that:
                            // 1) have the compoundUniqueId of the items we are loading (project), and
                            // 2) have an entity relation that has:
                            //      a) the compoundUniqueId of the parentEntity (contact)
                            //      b) the relatedEntityId of the parentFilterValue (the contactId) (
                            let candidateParentFileItems: GobiiFileItem[] =
                                allFileItems.filter(fi =>
                                    filterParamsToLoad.getTargetEtityUniqueId().compoundIdeEquals(fi)
                                    && fi.getRelatedEntityFilterValue(parentEntityCompoundUniqueId) === parentFilterValue
                                    && fi.getItemId() !== this.NONE_ITEM_ITEM_ID
                                );

                            // Now we will set the child filter's fkEntityValue to whatever happens to be the first item
                            // in the list to which we filtered
                            let childItemsFilterValue: string = null;
                            if (candidateParentFileItems.length > 0) {
                                childItemsFilterValue = candidateParentFileItems[0].getItemId();
                            }

                            for (let idx: number = 0;
                                 idx < filterParamsToLoad.getChildFileItemParams().length;
                                 idx++) {
                                let rqp: FilterParams = filterParamsToLoad.getChildFileItemParams()[idx];
//                                if (rqp.getFilterType() === FilterType.NAMES_BY_TYPEID) {

                                //rqp.setRelatedEntityFilterValue(childItemsFilterValue);
                                //rqp.setTargetEntityFilterValue(null); //the parent selection invalidates the previous target selection

                                this.recurseFilters(gobiiExtractFilterType,
                                    rqp,
                                    childItemsFilterValue,
                                    true)
                                    .subscribe(fileItems => observer.next(fileItems));
//                                }
                            }

                        }).unsubscribe();//select all file items

                } // if we have child filters


            } // if we are recursing


        });//return Observer.create


    } // recurseFilters()

    public loadEntityList(gobiiExtractFilterType: GobiiExtractFilterType,
                          fileItemParamName: FilterParamNames,
                          cropType: string) {
        let fileItemParams: FilterParams = this.filterParamsColl.getFilter(fileItemParamName, gobiiExtractFilterType);
        if (fileItemParams && fileItemParams.getFilterType() === FilterType.ENTITY_LIST) {
            this.makeFileItemActionsFromEntities(gobiiExtractFilterType, fileItemParams, null, false, cropType)
                .subscribe(action => {
                    if (action) {
                        this.store.dispatch(action);
                    }
                });
        }
    } // loadEntityList()


    public loadPagedEntityList(gobiiExtractFilterType: GobiiExtractFilterType,
                               fileItemParamName: FilterParamNames,
                               paedQueryId: string,
                               pageSize: number,
                               pageNum: number,
                               cropType: string) {

        let fileItemParams: FilterParams = this.filterParamsColl.getFilter(fileItemParamName, gobiiExtractFilterType);

        if (fileItemParams.getIsPaged()) {
            fileItemParams.setPageSize(pageSize);
            fileItemParams.setPageNum(pageNum);
            fileItemParams.setPagedQueryId(paedQueryId);
            if (fileItemParams && fileItemParams.getFilterType() === FilterType.ENTITY_LIST) {
                this.makeFileItemActionsFromEntities(gobiiExtractFilterType, fileItemParams, null, false, cropType)
                    .subscribe(action => {
                        if (action) {
                            this.store.dispatch(action);
                        }
                    });
            }
        } else {
            this.store.dispatch(new historyAction.AddStatusAction(new HeaderStatusMessage("This filter does not support paging: " + fileItemParamName,
                StatusLevel.ERROR,
                null)));
        }

    } // loadEntityList()


    private makeFileItemActionsFromEntities(gobiiExtractFilterType: GobiiExtractFilterType,
                                            filterParams: FilterParams,
                                            filterValue: string,
                                            recurse: boolean,
                                            cropType: string): Observable<fileItemActions.LoadFileItemListWithFilterAction> {

        return Observable.create(observer => {

            try {

                // if (filterParams.getIsDynamicFilterValue()) {
                //     filterParams.setRelatedEntityFilterValue(filterValue);
                // }

                // note that this method does not do any of the entity dating and checking
                // thing. It needs to be reworked for paging so that the filter ID also takes
                // into account the current page -- i.e., so that the datetime stamp pertains to the
                // specific page. This is going to require some refactoring.

                if (filterParams.getFilterType() === FilterType.ENTITY_LIST) {


                    let dtoRequestItem: DtoRequestItem<any> = filterParams.getDtoRequestItem();


                    let dtoRequestService: DtoRequestService<any> = filterParams.getDtoRequestService();

                    dtoRequestService
                        .get(dtoRequestItem)
                        .subscribe(entityResult => {

                                let pagination: Pagination = null;
                                let entityItems: GobiiFileItem[] = [];
                                if (filterParams.getIsPaged()) {

                                    entityItems = entityResult.gobiiFileItems;
                                    pagination = entityResult.pagination;

                                } else {

                                    entityItems = entityResult;
                                }


                                entityItems.forEach(fi => {
                                    fi.setGobiiExtractFilterType(gobiiExtractFilterType);
                                    fi.setCropType(cropType);
                                });

                                let date: Date = new Date();
                                let loadAction: fileItemActions.LoadFileItemListWithFilterAction =
                                    new fileItemActions.LoadFileItemListWithFilterAction(
                                        {
                                            gobiiFileItems: entityItems,
                                            filterId: filterParams.getQueryName(),
                                            filter: new PayloadFilter(
                                                gobiiExtractFilterType,
                                                filterParams.getTargetEtityUniqueId(),
                                                filterParams.getRelatedEntityUniqueId(),
                                                filterValue,
                                                filterValue,
                                                date,
                                                pagination
                                            )
                                        }
                                    );

                                observer.next(loadAction);

                            },
                            responseHeader => {
                                this.store.dispatch(new historyAction.AddStatusAction(responseHeader));

                            });

                } else {

                    let extractFilterTypeString: string = "undefined";
                    if (gobiiExtractFilterType) {
                        extractFilterTypeString = GobiiExtractFilterType[gobiiExtractFilterType];
                    }
                    this.store.dispatch(new historyAction.AddStatusMessageAction("FileItemParams "
                        + filterParams.getQueryName()
                        + " for extract type "
                        + extractFilterTypeString
                        + " is not of type "
                        + FilterType[FilterType.ENTITY_LIST]
                    ));


                } // if else filterParams are correct

            } catch (error) {
                this.store.dispatch(new historyAction.AddStatusAction(error));
            }

        }); // Observer.create()

    }//makeFileItemActionsFromEntities()

}
