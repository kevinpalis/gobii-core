import {createSelector} from 'reselect';
import {GobiiFileItem} from "../../model/gobii-file-item";
import * as gobiiFileItemAction from "../actions/fileitem-action";
import {ExtractorItemType} from "../../model/file-model-node";
import {EntitySubType, EntityType} from "../../model/type-entity";
import {NameIdRequestParams} from "../../model/name-id-request-params";
import {NameIdFilterParamTypes} from "../../model/type-nameid-filter-params";


/***
 * By definition, GobiiFileItems are read-only; we only every get them
 * from the server and display them and select them for an extract.
 * CRUD operations on the entities themselves (e.g., Projects, Experiments, etc.)
 * will be handled each in their own fileItemsReducer.
 */
export interface State {
    fileItemUniqueIdsSelected: string[];
    fileItems: GobiiFileItem[] ;
    filters: { [id: string]: NameIdRequestParams };
};

export const initialState: State = {
    fileItemUniqueIdsSelected: [],
    fileItems: [],
    filters: {}
};

export function fileItemsReducer(state: State = initialState, action: gobiiFileItemAction.All): State {

    let returnVal: State = state;

    switch (action.type) {

        case gobiiFileItemAction.LOAD: {
            const gobiiFileItemsPayload = action.payload;

            const newGobiiFileItems = gobiiFileItemsPayload.filter(newItem =>
                state
                    .fileItems
                    .filter(stateItem =>
                        (
                            stateItem.getExtractorItemType() === newItem.getExtractorItemType() &&
                            stateItem.getEntityType() === newItem.getEntityType() &&
                            stateItem.getEntitySubType() === newItem.getEntitySubType() &&
                            stateItem.getCvFilterType() === newItem.getCvFilterType() &&
                            stateItem.getItemId() === newItem.getItemId()
                        )
                    ).length === 0
            );

            returnVal = {
                fileItemUniqueIdsSelected: state.fileItemUniqueIdsSelected,
                fileItems: [...state.fileItems, ...newGobiiFileItems],
                filters: {}
            };

            break;
        } // LOAD

        case gobiiFileItemAction.LOAD_FILTERED_ITEMS: {
            const gobiiFileItemsPayload = action.payload.gobiiFileItems;
            const nameIdRequestParamsPayload = action.payload.nameIdRequestParams;

            const newGobiiFileItems = gobiiFileItemsPayload.filter(newItem =>
                state
                    .fileItems
                    .filter(stateItem =>
                        (
                            stateItem.getExtractorItemType() === newItem.getExtractorItemType() &&
                            stateItem.getEntityType() === newItem.getEntityType() &&
                            stateItem.getEntitySubType() === newItem.getEntitySubType() &&
                            stateItem.getCvFilterType() === newItem.getCvFilterType() &&
                            stateItem.getItemId() === newItem.getItemId()
                        )
                    ).length === 0
            );

            let newFilterState = Object.assign({}, state.filters);
            newFilterState[nameIdRequestParamsPayload.getQueryName()] = nameIdRequestParamsPayload;


            returnVal = {
                fileItemUniqueIdsSelected: state.fileItemUniqueIdsSelected,
                fileItems: [...state.fileItems, ...newGobiiFileItems],
                filters: newFilterState
            };

            break;
        } // LOAD_FILTERED_ITEMS

        // Technically, and according to the ngrx/store example app,
        // it should be possible for different actions to have a different
        // payload type, such that it's possible for a payload to be a single
        // GobiiFileItem rather than an array of. However, I cannot get this
        // to compile properly. See this issue I submitted:
        // https://github.com/ngrx/platform/issues/255
        // For now in the interest of making progress I am using
        // an array type for all action payloads
        case gobiiFileItemAction.SELECT_FOR_EXTRACT: {

            const gobiiFileItemPayload: GobiiFileItem = action.payload;
            const selectedUniqueItemIds = state
                .fileItems
                .filter(fileItem =>
                    gobiiFileItemPayload.getFileItemUniqueId() !== fileItem.getFileItemUniqueId())
                .map(selectedFileItem => selectedFileItem.getFileItemUniqueId());

            returnVal = {
                fileItems: state.fileItems,
                fileItemUniqueIdsSelected: [...state.fileItemUniqueIdsSelected, ...selectedUniqueItemIds],
                filters: {} // spread syntax
            };

            break;
        } // SELECT_FOR_EXTRACT

        case gobiiFileItemAction.DESELECT_FOR_EXTRACT: {

            const gobiiFileItemPayload: GobiiFileItem = action.payload;
            const newSelectedUniqueItemIds = state
                .fileItemUniqueIdsSelected
                .filter(selectedId =>
                    gobiiFileItemPayload.getFileItemUniqueId() != selectedId
                );


            returnVal = {
                fileItems: state.fileItems,
                fileItemUniqueIdsSelected: newSelectedUniqueItemIds,
                filters: {}
            };

            break;
        }

        case gobiiFileItemAction.SET_ENTITY_FILTER: {

            const nameIdRequestParamsPayload: NameIdRequestParams = action.payload.nameIdRequestParams;
            let newFilterState = Object.assign({}, state.filters);
            newFilterState[nameIdRequestParamsPayload.getQueryName()] = nameIdRequestParamsPayload;


            returnVal = {
                fileItems: state.fileItems,
                fileItemUniqueIdsSelected: state.fileItemUniqueIdsSelected,
                filters: newFilterState
            };

            break;

        } //

        case gobiiFileItemAction.SELECT_FOR_EXTRACT_BY_FILE_ITEM_ID: {

            const fileItemUniqueIdPayload: string = action.payload;

            let newUniqueId: string[] = [];
            if (!state
                    .fileItemUniqueIdsSelected
                    .find(i => i === fileItemUniqueIdPayload)) {
                if (state
                        .fileItems
                        .find(fi => fi.getFileItemUniqueId() === fileItemUniqueIdPayload)) {
                    newUniqueId.push(fileItemUniqueIdPayload);
                }
            }


            returnVal = {
                fileItems: state.fileItems,
                fileItemUniqueIdsSelected: [...state.fileItemUniqueIdsSelected, ...newUniqueId],
                filters: state.filters
            };

            break;

        } //


    }

    return returnVal;

}

/**
 * Because the data structure is defined within the fileItemsReducer it is optimal to
 * locate our selector functions at this level. If store is to be thought of
 * as a database, and reducers the tables, selectors can be considered the
 * queries into said database. Remember to keep your selectors small and
 * focused so they can be combined and composed to fit each particular
 * use-case.
 */

export const getFileItems = (state: State) => state.fileItems;

export const getUniqueIds = (state: State) => state.fileItems.map(fileItem => fileItem.getFileItemUniqueId());

export const getSelectedUniqueIds = (state: State) => state.fileItemUniqueIdsSelected;

export const getFilters = (state: State) => state.filters;

export const getSelected = createSelector(getFileItems, getSelectedUniqueIds, (fileItems, selectedUniqueIds) => {
    return fileItems.filter(fileItem => {
        selectedUniqueIds.filter(uniqueId => fileItem.getFileItemUniqueId() === uniqueId)
    });
});

export const getAll = createSelector(getFileItems, getUniqueIds, (entities, ids) => {
    return ids.map(id => entities[id]);
});

// ideally, the redux way of selecting data would be for the selection to be based
// on which entity is "selected" The problem is that we have controlls in which the
// entity type is parameterized -- it is not global state


export const getContacts = createSelector(getFileItems, getUniqueIds, (fileItems, ids) => {

    return fileItems.filter(e =>
        ( e.getExtractorItemType() === ExtractorItemType.ENTITY
            || e.getExtractorItemType() === ExtractorItemType.LABEL )
        && e.getEntityType() === EntityType.Contacts)
        .map(fi => fi);
});

export const getProjects = createSelector(getFileItems, getUniqueIds, (fileItems, ids) => {

    return fileItems.filter(e =>
        ( e.getExtractorItemType() === ExtractorItemType.ENTITY
            || e.getExtractorItemType() === ExtractorItemType.LABEL )
        && e.getEntityType() === EntityType.Projects)
        .map(fi => fi);
});

export const getExperiments = createSelector(getFileItems, getUniqueIds, (fileItems, ids) => {

    return fileItems.filter(e =>
        ( e.getExtractorItemType() === ExtractorItemType.ENTITY
            || e.getExtractorItemType() === ExtractorItemType.LABEL )
        && e.getEntityType() === EntityType.Experiments)
        .map(fi => fi);
});

export const getDatasets = createSelector(getFileItems, getUniqueIds, (fileItems, ids) => {

    return fileItems.filter(e =>
        ( e.getExtractorItemType() === ExtractorItemType.ENTITY
            || e.getExtractorItemType() === ExtractorItemType.LABEL )
        && e.getEntityType() === EntityType.DataSets)
        .map(fi => fi);
});

export const getCvTerms = createSelector(getFileItems, getUniqueIds, (fileItems, ids) => {

    return fileItems.filter(e =>
        ( e.getExtractorItemType() === ExtractorItemType.ENTITY
            || e.getExtractorItemType() === ExtractorItemType.LABEL )
        && e.getEntityType() === EntityType.CvTerms)
        .map(fi => fi);
});

export const getMapsets = createSelector(getFileItems, getUniqueIds, (fileItems, ids) => {

    return fileItems.filter(e =>
        ( e.getExtractorItemType() === ExtractorItemType.ENTITY
            || e.getExtractorItemType() === ExtractorItemType.LABEL )
        && e.getEntityType() === EntityType.Mapsets)
        .map(fi => fi);
});

export const getPlatforms = createSelector(getFileItems, getUniqueIds, (fileItems, ids) => {

    return fileItems.filter(e =>
        ( e.getExtractorItemType() === ExtractorItemType.ENTITY
            || e.getExtractorItemType() === ExtractorItemType.LABEL )
        && e.getEntityType() === EntityType.Platforms)
        .map(fi => fi);
});


export const getMarkerGroups = createSelector(getFileItems, getUniqueIds, (fileItems, ids) => {

    return fileItems.filter(e =>
        ( e.getExtractorItemType() === ExtractorItemType.ENTITY
            || e.getExtractorItemType() === ExtractorItemType.LABEL )
        && e.getEntityType() === EntityType.MarkerGroups)
        .map(fi => fi);
});

/// **************** GET SELECTED PER ENTITY TYPE
export const getSelectedPiContacts = createSelector(getFileItems, getUniqueIds, (fileItems, ids) => {

    return fileItems.filter(e =>
        ids.find(id => id === e.getFileItemUniqueId())
        && e.getExtractorItemType() === ExtractorItemType.ENTITY
        && e.getEntityType() === EntityType.Contacts
        && e.getEntitySubType() === EntitySubType.CONTACT_PRINCIPLE_INVESTIGATOR)
        .map(fi => fi);
});


export const getDatasetsForSelectedExperiment = createSelector(getFileItems, getFilters, (fileItems, filters) => {

    let returnVal: GobiiFileItem[] = [];

    if (filters[NameIdFilterParamTypes.DATASETS_BY_EXPERIMENT]) {

        let experimentId: string = filters[NameIdFilterParamTypes.DATASETS_BY_EXPERIMENT].getEntityFilterValue();
        returnVal = fileItems.filter(e =>
            ( e.getExtractorItemType() === ExtractorItemType.ENTITY )
            && ( e.getEntityType() === EntityType.DataSets )
            && (e.getParentItemId() === experimentId ))
            .map(fi => fi);

    }

    return returnVal;
});





