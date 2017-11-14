System.register(["@angular/core", "../../model/type-entity", "../../views/entity-labels", "../../model/type-extractor-item", "../../model/type-extractor-filter", "../../model/cv-filter-type", "../../model/gobii-file-item", "../../model/dto-header-status-message", "../../model/type-process", "./name-id-service", "../../model/file-item-params", "../../store/actions/history-action", "../../store/actions/fileitem-action", "../../store/reducers", "@ngrx/store", "../../model/name-id-label-type", "../../model/filter-type", "../../model/file-item-param-names", "rxjs/Observable", "rxjs/add/operator/expand", "./dto-request.service", "../app/dto-request-item-entity-stats", "../app/dto-request-item-gfi", "../app/jsontogfi/json-to-gfi-dataset"], function (exports_1, context_1) {
    "use strict";
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var __moduleName = context_1 && context_1.id;
    var core_1, type_entity_1, entity_labels_1, type_extractor_item_1, type_extractor_filter_1, cv_filter_type_1, gobii_file_item_1, dto_header_status_message_1, type_process_1, name_id_service_1, file_item_params_1, historyAction, fileItemActions, fromRoot, store_1, name_id_label_type_1, filter_type_1, file_item_param_names_1, Observable_1, dto_request_service_1, dto_request_item_entity_stats_1, dto_request_item_gfi_1, json_to_gfi_dataset_1, FileItemService;
    return {
        setters: [
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (type_entity_1_1) {
                type_entity_1 = type_entity_1_1;
            },
            function (entity_labels_1_1) {
                entity_labels_1 = entity_labels_1_1;
            },
            function (type_extractor_item_1_1) {
                type_extractor_item_1 = type_extractor_item_1_1;
            },
            function (type_extractor_filter_1_1) {
                type_extractor_filter_1 = type_extractor_filter_1_1;
            },
            function (cv_filter_type_1_1) {
                cv_filter_type_1 = cv_filter_type_1_1;
            },
            function (gobii_file_item_1_1) {
                gobii_file_item_1 = gobii_file_item_1_1;
            },
            function (dto_header_status_message_1_1) {
                dto_header_status_message_1 = dto_header_status_message_1_1;
            },
            function (type_process_1_1) {
                type_process_1 = type_process_1_1;
            },
            function (name_id_service_1_1) {
                name_id_service_1 = name_id_service_1_1;
            },
            function (file_item_params_1_1) {
                file_item_params_1 = file_item_params_1_1;
            },
            function (historyAction_1) {
                historyAction = historyAction_1;
            },
            function (fileItemActions_1) {
                fileItemActions = fileItemActions_1;
            },
            function (fromRoot_1) {
                fromRoot = fromRoot_1;
            },
            function (store_1_1) {
                store_1 = store_1_1;
            },
            function (name_id_label_type_1_1) {
                name_id_label_type_1 = name_id_label_type_1_1;
            },
            function (filter_type_1_1) {
                filter_type_1 = filter_type_1_1;
            },
            function (file_item_param_names_1_1) {
                file_item_param_names_1 = file_item_param_names_1_1;
            },
            function (Observable_1_1) {
                Observable_1 = Observable_1_1;
            },
            function (_1) {
            },
            function (dto_request_service_1_1) {
                dto_request_service_1 = dto_request_service_1_1;
            },
            function (dto_request_item_entity_stats_1_1) {
                dto_request_item_entity_stats_1 = dto_request_item_entity_stats_1_1;
            },
            function (dto_request_item_gfi_1_1) {
                dto_request_item_gfi_1 = dto_request_item_gfi_1_1;
            },
            function (json_to_gfi_dataset_1_1) {
                json_to_gfi_dataset_1 = json_to_gfi_dataset_1_1;
            }
        ],
        execute: function () {
            FileItemService = (function () {
                function FileItemService(nameIdService, entityStatsService, fileItemRequestService, store) {
                    this.nameIdService = nameIdService;
                    this.entityStatsService = entityStatsService;
                    this.fileItemRequestService = fileItemRequestService;
                    this.store = store;
                    this.fileFilterParams = [];
                    // For non-hierarchically filtered request params, we just create them simply
                    // as we add them to the flat map
                    this.addFilter(file_item_params_1.FileItemParams
                        .build(file_item_param_names_1.FileItemParamNames.CV_DATATYPE, type_extractor_filter_1.GobiiExtractFilterType.BY_SAMPLE, type_entity_1.EntityType.CV)
                        .setIsDynamicFilterValue(false)
                        .setCvFilterType(cv_filter_type_1.CvFilterType.DATASET_TYPE)
                        .setFilterType(filter_type_1.FilterType.NAMES_BY_TYPE_NAME)
                        .setFkEntityFilterValue(cv_filter_type_1.CvFilters.get(cv_filter_type_1.CvFilterType.DATASET_TYPE))
                        .setNameIdLabelType(name_id_label_type_1.NameIdLabelType.SELECT_A));
                    this.addFilter(file_item_params_1.FileItemParams
                        .build(file_item_param_names_1.FileItemParamNames.CV_DATATYPE, type_extractor_filter_1.GobiiExtractFilterType.BY_MARKER, type_entity_1.EntityType.CV)
                        .setIsDynamicFilterValue(false)
                        .setCvFilterType(cv_filter_type_1.CvFilterType.DATASET_TYPE)
                        .setFilterType(filter_type_1.FilterType.NAMES_BY_TYPE_NAME)
                        .setFkEntityFilterValue(cv_filter_type_1.CvFilters.get(cv_filter_type_1.CvFilterType.DATASET_TYPE))
                        .setNameIdLabelType(name_id_label_type_1.NameIdLabelType.SELECT_A));
                    this.addFilter(file_item_params_1.FileItemParams
                        .build(file_item_param_names_1.FileItemParamNames.MAPSETS, type_extractor_filter_1.GobiiExtractFilterType.WHOLE_DATASET, type_entity_1.EntityType.MAPSET)
                        .setIsDynamicFilterValue(false)
                        .setNameIdLabelType(name_id_label_type_1.NameIdLabelType.NO));
                    this.addFilter(file_item_params_1.FileItemParams
                        .build(file_item_param_names_1.FileItemParamNames.MAPSETS, type_extractor_filter_1.GobiiExtractFilterType.BY_SAMPLE, type_entity_1.EntityType.MAPSET)
                        .setIsDynamicFilterValue(false)
                        .setNameIdLabelType(name_id_label_type_1.NameIdLabelType.NO));
                    this.addFilter(file_item_params_1.FileItemParams
                        .build(file_item_param_names_1.FileItemParamNames.MAPSETS, type_extractor_filter_1.GobiiExtractFilterType.BY_MARKER, type_entity_1.EntityType.MAPSET)
                        .setIsDynamicFilterValue(false)
                        .setNameIdLabelType(name_id_label_type_1.NameIdLabelType.NO));
                    this.addFilter(file_item_params_1.FileItemParams
                        .build(file_item_param_names_1.FileItemParamNames.PLATFORMS, type_extractor_filter_1.GobiiExtractFilterType.BY_SAMPLE, type_entity_1.EntityType.PLATFORM)
                        .setIsDynamicFilterValue(false));
                    this.addFilter(file_item_params_1.FileItemParams
                        .build(file_item_param_names_1.FileItemParamNames.PLATFORMS, type_extractor_filter_1.GobiiExtractFilterType.BY_MARKER, type_entity_1.EntityType.PLATFORM)
                        .setIsDynamicFilterValue(false));
                    this.addFilter(file_item_params_1.FileItemParams
                        .build(file_item_param_names_1.FileItemParamNames.MARKER_GROUPS, type_extractor_filter_1.GobiiExtractFilterType.BY_MARKER, type_entity_1.EntityType.MARKER_GROUP)
                        .setIsDynamicFilterValue(false));
                    this.addFilter(file_item_params_1.FileItemParams
                        .build(file_item_param_names_1.FileItemParamNames.PROJECTS, type_extractor_filter_1.GobiiExtractFilterType.BY_SAMPLE, type_entity_1.EntityType.PROJECT)
                        .setIsDynamicFilterValue(false)
                        .setNameIdLabelType(name_id_label_type_1.NameIdLabelType.ALL));
                    this.addFilter(file_item_params_1.FileItemParams
                        .build(file_item_param_names_1.FileItemParamNames.CONTACT_PI, type_extractor_filter_1.GobiiExtractFilterType.BY_SAMPLE, type_entity_1.EntityType.CONTACT)
                        .setIsDynamicFilterValue(false)
                        .setEntitySubType(type_entity_1.EntitySubType.CONTACT_PRINCIPLE_INVESTIGATOR));
                    this.addFilter(file_item_params_1.FileItemParams
                        .build(file_item_param_names_1.FileItemParamNames.DATASETS, type_extractor_filter_1.GobiiExtractFilterType.WHOLE_DATASET, type_entity_1.EntityType.DATASET)
                        .setFilterType(filter_type_1.FilterType.ENTITY_LIST));
                    //for hierarchical items, we need to crate the nameid requests separately from the
                    //flat map: they _will_ need to be in the flat map; however, they all need to be
                    //useed to set up the filtering hierarchy
                    var nameIdRequestParamsContactsPi = file_item_params_1.FileItemParams
                        .build(file_item_param_names_1.FileItemParamNames.CONTACT_PI, type_extractor_filter_1.GobiiExtractFilterType.WHOLE_DATASET, type_entity_1.EntityType.CONTACT)
                        .setIsDynamicFilterValue(true)
                        .setEntitySubType(type_entity_1.EntitySubType.CONTACT_PRINCIPLE_INVESTIGATOR);
                    var nameIdRequestParamsProjectByPiContact = file_item_params_1.FileItemParams
                        .build(file_item_param_names_1.FileItemParamNames.PROJECTS_BY_CONTACT, type_extractor_filter_1.GobiiExtractFilterType.WHOLE_DATASET, type_entity_1.EntityType.PROJECT)
                        .setIsDynamicFilterValue(true)
                        .setFilterType(filter_type_1.FilterType.NAMES_BY_TYPEID);
                    var nameIdRequestParamsExperiments = file_item_params_1.FileItemParams
                        .build(file_item_param_names_1.FileItemParamNames.EXPERIMENTS_BY_PROJECT, type_extractor_filter_1.GobiiExtractFilterType.WHOLE_DATASET, type_entity_1.EntityType.EXPERIMENT)
                        .setIsDynamicFilterValue(true)
                        .setFilterType(filter_type_1.FilterType.NAMES_BY_TYPEID);
                    var nameIdRequestParamsDatasets = file_item_params_1.FileItemParams
                        .build(file_item_param_names_1.FileItemParamNames.DATASETS_BY_EXPERIMENT, type_extractor_filter_1.GobiiExtractFilterType.WHOLE_DATASET, type_entity_1.EntityType.DATASET)
                        .setIsDynamicFilterValue(true)
                        .setFilterType(filter_type_1.FilterType.NAMES_BY_TYPEID);
                    //add the individual requests to the map
                    this.addFilter(nameIdRequestParamsContactsPi);
                    this.addFilter(nameIdRequestParamsProjectByPiContact);
                    this.addFilter(nameIdRequestParamsExperiments);
                    this.addFilter(nameIdRequestParamsDatasets);
                    //build the parent-child request params graph
                    nameIdRequestParamsContactsPi
                        .setChildNameIdRequestParams([nameIdRequestParamsProjectByPiContact
                            .setParentFileItemParams(nameIdRequestParamsContactsPi)
                            .setChildNameIdRequestParams([nameIdRequestParamsExperiments
                                .setParentFileItemParams(nameIdRequestParamsProjectByPiContact)
                                .setChildNameIdRequestParams([nameIdRequestParamsDatasets
                                    .setParentFileItemParams(nameIdRequestParamsExperiments)
                            ])
                        ])
                    ]);
                } // constructor
                FileItemService.prototype.addFilter = function (filterParamsToAdd) {
                    var existingFilterParams = this.fileFilterParams
                        .find(function (ffp) {
                        return ffp.getQueryName() === filterParamsToAdd.getQueryName()
                            && ffp.getGobiiExtractFilterType() === filterParamsToAdd.getGobiiExtractFilterType();
                    });
                    if (!existingFilterParams) {
                        this.fileFilterParams.push(filterParamsToAdd);
                    }
                    else {
                        this.store.dispatch(new historyAction.AddStatusMessageAction("The query "
                            + filterParamsToAdd.getQueryName()
                            + " because there is already a filter by that name for this extract type "
                            + type_extractor_filter_1.GobiiExtractFilterType[filterParamsToAdd.getQueryName()]));
                    }
                };
                FileItemService.prototype.getFilter = function (nameIdFilterParamTypes, gobiiExtractFilterType) {
                    return this.fileFilterParams.find(function (ffp) {
                        return ffp.getQueryName() === nameIdFilterParamTypes &&
                            ffp.getGobiiExtractFilterType() === gobiiExtractFilterType;
                    });
                };
                FileItemService.prototype.getForFilter = function (nameIdFilterParamTypes) {
                    //Wrapping an Observable around the select functions just doesn't work. So at leaset this
                    //function can encapsualte getting the correct selector for the filter
                    var returnVal;
                    switch (nameIdFilterParamTypes) {
                        case file_item_param_names_1.FileItemParamNames.MARKER_GROUPS:
                            returnVal = this.store.select(fromRoot.getMarkerGroups);
                            break;
                        case file_item_param_names_1.FileItemParamNames.PROJECTS:
                            returnVal = this.store.select(fromRoot.getProjects);
                            break;
                        case file_item_param_names_1.FileItemParamNames.PROJECTS_BY_CONTACT:
                            returnVal = this.store.select(fromRoot.getProjectsByPI);
                            break;
                        case file_item_param_names_1.FileItemParamNames.EXPERIMENTS_BY_PROJECT:
                            returnVal = this.store.select(fromRoot.getExperimentsByProject);
                            break;
                        case file_item_param_names_1.FileItemParamNames.EXPERIMENTS:
                            returnVal = this.store.select(fromRoot.getExperiments);
                            break;
                        case file_item_param_names_1.FileItemParamNames.DATASETS_BY_EXPERIMENT:
                            returnVal = this.store.select(fromRoot.getDatasetsByExperiment);
                            break;
                        case file_item_param_names_1.FileItemParamNames.PLATFORMS:
                            returnVal = this.store.select(fromRoot.getPlatforms);
                            break;
                        case file_item_param_names_1.FileItemParamNames.CV_DATATYPE:
                            returnVal = this.store.select(fromRoot.getCvTermsDataType);
                            break;
                        case file_item_param_names_1.FileItemParamNames.MAPSETS:
                            returnVal = this.store.select(fromRoot.getMapsets);
                            break;
                        case file_item_param_names_1.FileItemParamNames.CONTACT_PI:
                            returnVal = this.store.select(fromRoot.getPiContacts);
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
                    //                 let nameIdRequestParams: FileItemParams = this.nameIdRequestParams.get(nameIdFilterParamTypes);
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
                };
                FileItemService.prototype.setItemLabelType = function (gobiiExtractFilterType, nameIdFilterParamTypes, nameIdLabelType) {
                    var nameIdRequestParamsFromType = this.getFilter(nameIdFilterParamTypes, gobiiExtractFilterType);
                    if (nameIdRequestParamsFromType) {
                        nameIdRequestParamsFromType.setNameIdLabelType(nameIdLabelType);
                    }
                    else {
                        this.store.dispatch(new historyAction.AddStatusMessageAction("Error setting label type: there is no query params object for query "
                            + nameIdFilterParamTypes
                            + " with extract filter type "
                            + type_extractor_filter_1.GobiiExtractFilterType[gobiiExtractFilterType]));
                    }
                };
                FileItemService.prototype.loadNameIdsFromFilterParams = function (gobiiExtractFilterType, nameIdFilterParamTypes, filterValue) {
                    var _this = this;
                    var nameIdRequestParamsFromType = this.getFilter(nameIdFilterParamTypes, gobiiExtractFilterType);
                    if (nameIdRequestParamsFromType) {
                        this.makeNameIdLoadActions(gobiiExtractFilterType, nameIdFilterParamTypes, filterValue)
                            .subscribe(function (action) {
                            if (action) {
                                _this.store.dispatch(action);
                            }
                        });
                    }
                    else {
                        this.store.dispatch(new historyAction.AddStatusMessageAction("Error loading with filter params: there is no query params object for query "
                            + nameIdFilterParamTypes
                            + " with extract filter type "
                            + type_extractor_filter_1.GobiiExtractFilterType[gobiiExtractFilterType]));
                    }
                };
                FileItemService.prototype.loadFileItem = function (gobiiFileItem, selectForExtract) {
                    var loadAction = new fileItemActions.LoadFileItemtAction({
                        gobiiFileItem: gobiiFileItem,
                        selectForExtract: selectForExtract
                    });
                    this.store.dispatch(loadAction);
                };
                /***
                 * This is a hard-repalce: the item is not just removed from the extract, but nuked entirely from
                 * the store
                 * @param {GobiiFileItem} gobiiFileItem
                 * @param {boolean} selectForExtract
                 */
                FileItemService.prototype.replaceFileItemByCompoundId = function (gobiiFileItem, selectForExtract) {
                    var loadAction = new fileItemActions.ReplaceItemOfSameCompoundIdAction({
                        gobiiFileitemToReplaceWith: gobiiFileItem,
                        selectForExtract: selectForExtract
                    });
                    this.store.dispatch(loadAction);
                };
                FileItemService.prototype.unloadFileItemFromExtract = function (gobiiFileItem) {
                    var loadAction = new fileItemActions.RemoveFromExtractAction(gobiiFileItem);
                    this.store.dispatch(loadAction);
                };
                FileItemService.prototype.makeNameIdLoadActions = function (gobiiExtractFilterType, nameIdFilterParamTypes, filterValue) {
                    var nameIdRequestParamsFromType = this.getFilter(nameIdFilterParamTypes, gobiiExtractFilterType);
                    if (nameIdRequestParamsFromType) {
                        return this.makeFileItemActionsFromNameIds(gobiiExtractFilterType, nameIdRequestParamsFromType, filterValue, true);
                    }
                    else {
                        this.store.dispatch(new historyAction.AddStatusMessageAction("Undefined FileItemParams filter: "
                            + nameIdFilterParamTypes.toString()
                            + " for extract type " + type_extractor_filter_1.GobiiExtractFilterType[gobiiExtractFilterType]));
                    }
                };
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
                 * @param {FileItemParams} filterParamsToLoad
                 * @param {string} filterValue
                 * @param {boolean} recurse
                 * @returns {Observable<LoadFileItemListWithFilterAction>}
                 */
                FileItemService.prototype.makeFileItemActionsFromNameIds = function (gobiiExtractFilterType, filterParamsToLoad, filterValue, recurse) {
                    var _this = this;
                    return Observable_1.Observable.create(function (observer) {
                        if (filterParamsToLoad.getIsDynamicFilterValue()) {
                            filterParamsToLoad.setFkEntityFilterValue(filterValue);
                        }
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
                        _this.entityStatsService.get(new dto_request_item_entity_stats_1.DtoRequestItemEntityStats(dto_request_item_entity_stats_1.EntityRequestType.LasetUpdated, filterParamsToLoad.getEntityType(), null, null))
                            .subscribe(function (entityStats) {
                            _this.store.select(fromRoot.getFiltersRetrieved)
                                .subscribe(function (filterHistoryItems) {
                                var fileHistoryItem = filterHistoryItems.find(function (fhi) {
                                    return fhi.gobiiExtractFilterType === filterParamsToLoad.getGobiiExtractFilterType()
                                        && fhi.filterId === filterParamsToLoad.getQueryName()
                                        && fhi.filterValue === filterParamsToLoad.getFkEntityFilterValue();
                                });
                                var disregardDateSensitiveQueryingForNow = false;
                                if (disregardDateSensitiveQueryingForNow ||
                                    ((!fileHistoryItem) ||
                                        (entityStats.lastModified > fileHistoryItem.entityLasteUpdated))) {
                                    // Either the data have never been retrieved at all for a given filter value,
                                    // or the server-side entity has been updated. So we shall refresh the
                                    // data and dispatch both the new filter value and the
                                    //BEGIN: nameIdService.get()
                                    _this.nameIdService.get(filterParamsToLoad)
                                        .subscribe(function (nameIds) {
                                        var minEntityLastUpdated;
                                        var fileItems = [];
                                        if (nameIds && (nameIds.length > 0)) {
                                            nameIds.forEach(function (n) {
                                                var currentFileItem = gobii_file_item_1.GobiiFileItem.build(gobiiExtractFilterType, type_process_1.ProcessType.CREATE)
                                                    .setExtractorItemType(type_extractor_item_1.ExtractorItemType.ENTITY)
                                                    .setEntityType(filterParamsToLoad.getEntityType())
                                                    .setEntitySubType(filterParamsToLoad.getEntitySubType())
                                                    .setCvFilterType(filterParamsToLoad.getCvFilterType())
                                                    .setItemId(n.id)
                                                    .setItemName(n.name)
                                                    .setSelected(false)
                                                    .setRequired(false)
                                                    .setParentItemId(filterParamsToLoad.getFkEntityFilterValue());
                                                fileItems.push(currentFileItem);
                                            });
                                            minEntityLastUpdated = new Date(Math.min.apply(null, nameIds
                                                .map(function (nameId) { return nameId.entityLasetModified; })));
                                            var temp = "foo";
                                            temp = "bar";
                                            if (filterParamsToLoad.getMameIdLabelType() != name_id_label_type_1.NameIdLabelType.UNKNOWN) {
                                                var entityName = "";
                                                if (filterParamsToLoad.getCvFilterType() !== cv_filter_type_1.CvFilterType.UNKNOWN) {
                                                    entityName += entity_labels_1.Labels.instance().cvFilterNodeLabels[filterParamsToLoad.getCvFilterType()];
                                                }
                                                else if (filterParamsToLoad.getEntitySubType() !== type_entity_1.EntitySubType.UNKNOWN) {
                                                    entityName += entity_labels_1.Labels.instance().entitySubtypeNodeLabels[filterParamsToLoad.getEntitySubType()];
                                                }
                                                else {
                                                    entityName += entity_labels_1.Labels.instance().entityNodeLabels[filterParamsToLoad.getEntityType()];
                                                }
                                                var label = "";
                                                switch (filterParamsToLoad.getMameIdLabelType()) {
                                                    case name_id_label_type_1.NameIdLabelType.SELECT_A:
                                                        label = "Select a " + entityName;
                                                        break;
                                                    // we require that these entity labels all be in the singular
                                                    case name_id_label_type_1.NameIdLabelType.ALL:
                                                        label = "All " + entityName + "s";
                                                        break;
                                                    case name_id_label_type_1.NameIdLabelType.NO:
                                                        label = "No " + entityName;
                                                        break;
                                                    default:
                                                        _this.store.dispatch(new historyAction.AddStatusAction(new dto_header_status_message_1.HeaderStatusMessage("Unknown label type "
                                                            + name_id_label_type_1.NameIdLabelType[filterParamsToLoad.getMameIdLabelType()], null, null)));
                                                }
                                                var labelFileItem = gobii_file_item_1.GobiiFileItem
                                                    .build(gobiiExtractFilterType, type_process_1.ProcessType.CREATE)
                                                    .setEntityType(filterParamsToLoad.getEntityType())
                                                    .setEntitySubType(filterParamsToLoad.getEntitySubType())
                                                    .setCvFilterType(filterParamsToLoad.getCvFilterType())
                                                    .setExtractorItemType(type_extractor_item_1.ExtractorItemType.LABEL)
                                                    .setItemName(label)
                                                    .setParentItemId(filterParamsToLoad.getFkEntityFilterValue())
                                                    .setItemId("0");
                                                fileItems.unshift(labelFileItem);
                                                //.selectedFileItemId = "0";
                                            }
                                        }
                                        else {
                                            var noneFileItem = gobii_file_item_1.GobiiFileItem
                                                .build(gobiiExtractFilterType, type_process_1.ProcessType.DUMMY)
                                                .setExtractorItemType(type_extractor_item_1.ExtractorItemType.ENTITY)
                                                .setEntityType(filterParamsToLoad.getEntityType())
                                                .setItemId("0")
                                                .setItemName("<none>")
                                                .setParentItemId(filterParamsToLoad.getFkEntityFilterValue());
                                            fileItems.push(noneFileItem);
                                        } // if/else any nameids were retrieved
                                        var loadAction = new fileItemActions.LoadFileItemListWithFilterAction({
                                            gobiiFileItems: fileItems,
                                            filterId: filterParamsToLoad.getQueryName(),
                                            filter: {
                                                gobiiExtractFilterType: gobiiExtractFilterType,
                                                filterValue: filterParamsToLoad.getFkEntityFilterValue(),
                                                entityLasteUpdated: minEntityLastUpdated
                                            }
                                        });
                                        observer.next(loadAction);
                                        // if there are children, we will load their data as well
                                        if (recurse) {
                                            if (filterParamsToLoad
                                                .getChildFileItemParams()
                                                .filter(function (rqp) { return rqp.getFilterType() === filter_type_1.FilterType.NAMES_BY_TYPEID; })
                                                .length > 0) {
                                                var parentId = fileItems[0].getItemId();
                                                for (var idx = 0; idx < filterParamsToLoad.getChildFileItemParams().length; idx++) {
                                                    var rqp = filterParamsToLoad.getChildFileItemParams()[idx];
                                                    if (rqp.getFilterType() === filter_type_1.FilterType.NAMES_BY_TYPEID) {
                                                        rqp.setFkEntityFilterValue(parentId);
                                                        _this.makeFileItemActionsFromNameIds(gobiiExtractFilterType, rqp, parentId, true)
                                                            .subscribe(function (fileItems) { return observer.next(fileItems); });
                                                    }
                                                }
                                                ;
                                            }
                                        } // if we are recursing
                                    }, // Observer=>next
                                    function (// Observer=>next
                                        responseHeader) {
                                        _this.store.dispatch(new historyAction.AddStatusAction(responseHeader));
                                    }); // subscribe
                                }
                                else {
                                    // The data for given filter value exist and do not not need to be
                                    // updated. So here we shall dispatch only the new filter value.
                                    // The empty gobiiFileItems will amount to a null op.
                                    //BEGIN: nameIdService.get()
                                    var loadAction = new fileItemActions.LoadFilterAction({
                                        filterId: filterParamsToLoad.getQueryName(),
                                        filter: {
                                            gobiiExtractFilterType: gobiiExtractFilterType,
                                            filterValue: filterParamsToLoad.getFkEntityFilterValue(),
                                            entityLasteUpdated: fileHistoryItem.entityLasteUpdated
                                        }
                                    });
                                    observer.next(loadAction);
                                    if (recurse) {
                                        if (filterParamsToLoad
                                            .getChildFileItemParams()
                                            .filter(function (rqp) { return rqp.getFilterType() === filter_type_1.FilterType.NAMES_BY_TYPEID; })
                                            .length > 0) {
                                            // we need to set the current filter in state, but with respect to
                                            // gobiiFileItems, it should be a null op
                                            //Because we don't have the data freshly from the sever, we shall need
                                            //to get the "parentId" from the file items we have in the store
                                            _this.store.select(fromRoot.getAllFileItems)
                                                .subscribe(function (allFileItems) {
                                                // Get the parent item id from the store;
                                                // however, this will only work if the parent items have been loaded
                                                // already.
                                                var candidateParentFileItems = allFileItems.filter(function (fi) {
                                                    return filterParamsToLoad.compoundIdeEquals(fi)
                                                        && fi.getParentItemId() === filterParamsToLoad.getFkEntityFilterValue();
                                                });
                                                var childItemsFilterValue = "0";
                                                if (candidateParentFileItems.length > 0) {
                                                    childItemsFilterValue = candidateParentFileItems[0].getItemId();
                                                }
                                                for (var idx = 0; idx < filterParamsToLoad.getChildFileItemParams().length; idx++) {
                                                    var rqp = filterParamsToLoad.getChildFileItemParams()[idx];
                                                    if (rqp.getFilterType() === filter_type_1.FilterType.NAMES_BY_TYPEID) {
                                                        rqp.setFkEntityFilterValue(childItemsFilterValue);
                                                        _this.makeFileItemActionsFromNameIds(gobiiExtractFilterType, rqp, childItemsFilterValue, true)
                                                            .subscribe(function (fileItems) { return observer.next(fileItems); });
                                                    }
                                                }
                                            }).unsubscribe(); //select all file items
                                        } // if we have child filters
                                    } // if we are recursing
                                } // if-else we need to refresh from server or rely on what's in the store already
                                //END: nameIdService.get()
                            })
                                .unsubscribe(); // get filter history items
                        }); //subscribe get entity stats
                    }); //return Observer.create
                }; // make file items from query
                FileItemService.prototype.loadEntityList = function (gobiiExtractFilterType, fileItemParamName) {
                    var _this = this;
                    var fileItemParams = this.getFilter(fileItemParamName, gobiiExtractFilterType);
                    if (fileItemParams && fileItemParams.getFilterType() === filter_type_1.FilterType.ENTITY_LIST) {
                        var dtoRequestItemGfi_1 = new dto_request_item_gfi_1.DtoRequestItemGfi(fileItemParams, null, new json_to_gfi_dataset_1.JsonToGfiDataset());
                        this.entityStatsService.get(new dto_request_item_entity_stats_1.DtoRequestItemEntityStats(dto_request_item_entity_stats_1.EntityRequestType.LasetUpdated, fileItemParams.getEntityType(), null, null))
                            .subscribe(function (entityStats) {
                            _this.store.select(fromRoot.getFiltersRetrieved)
                                .subscribe(function (filterHistoryItems) {
                                var fileHistoryItem = filterHistoryItems.find(function (fhi) {
                                    return fhi.gobiiExtractFilterType === gobiiExtractFilterType
                                        && fhi.filterId === fileItemParams.getQueryName();
                                });
                                if ((!fileHistoryItem) ||
                                    (entityStats.lastModified > fileHistoryItem.entityLasteUpdated)) {
                                    _this.fileItemRequestService
                                        .get(dtoRequestItemGfi_1)
                                        .subscribe(function (entityItems) {
                                        var date = new Date();
                                        var loadAction = new fileItemActions.LoadFileItemListWithFilterAction({
                                            gobiiFileItems: [],
                                            filterId: fileItemParams.getQueryName(),
                                            filter: {
                                                gobiiExtractFilterType: gobiiExtractFilterType,
                                                filterValue: null,
                                                entityLasteUpdated: date
                                            }
                                        });
                                        _this.store.dispatch(loadAction);
                                    }, function (responseHeader) {
                                        _this.store.dispatch(new historyAction.AddStatusAction(responseHeader));
                                    });
                                }
                            });
                        });
                    }
                    else {
                        this.store.dispatch(new historyAction.AddStatusMessageAction("FileItemParams "
                            + fileItemParamName.toString()
                            + " either does not exist or is not of type "
                            + filter_type_1.FilterType[filter_type_1.FilterType.ENTITY_LIST]));
                    } // if else fileItemParams are correct
                };
                FileItemService = __decorate([
                    core_1.Injectable(),
                    __metadata("design:paramtypes", [name_id_service_1.NameIdService,
                        dto_request_service_1.DtoRequestService,
                        dto_request_service_1.DtoRequestService,
                        store_1.Store])
                ], FileItemService);
                return FileItemService;
            }());
            exports_1("FileItemService", FileItemService);
        }
    };
});
//# sourceMappingURL=file-item-service.js.map