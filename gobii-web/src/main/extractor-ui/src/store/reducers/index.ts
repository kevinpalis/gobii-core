//import {createSelector} from 'reselect';
import {ActionReducer, ActionReducerMap, createSelector} from '@ngrx/store';
//import * as fromRouter from '@ngrx/router-store';
import {environment} from '../../environments/environment';

/**
 * The compose function is one of our most handy tools. In basic terms, you give
 * it any number of functions and it returns a function. This new function
 * takes a value and chains it through every composed function, returning
 * the output.
 *
 * More: https://drboolean.gitbooks.io/mostly-adequate-guide/content/ch5.html
 */
import {compose} from '@ngrx/store';



/**
 * combineReducers is another useful metareducer that takes a map of fileItemsReducer
 * functions and creates a new fileItemsReducer that gathers the values
 * of each fileItemsReducer and stores them using the fileItemsReducer's key. Think of it
 * almost like a database, where every fileItemsReducer is a table in the db.
 *
 * More: https://egghead.io/lessons/javascript-redux-implementing-combinereducers-from-scratch
 */
import {combineReducers} from '@ngrx/store';


/**
 * Every fileItemsReducer module's default export is the fileItemsReducer function itself. In
 * addition, each module should export a type or interface that describes
 * the state of the fileItemsReducer plus any selector functions. The `* as`
 * notation packages up all of the exports into a single object.
 */
import * as fromFileItems from './fileitems-reducer';
import * as fromGobiiTreeNodes from './treenode-reducer';
import * as fromHistory from './history-reducer';
import {GobiiFileItem} from "../../model/gobii-file-item";
// import * as fromCollection from './collection';
// import * as fromLayout from './layout';


/**
 * As mentioned, we treat each fileItemsReducer like a table in a database. This means
 * our top level state interface is just a map of keys to inner state types.
 */
export interface State {
    fileItems: fromFileItems.State;
    gobiiTreeNodes: fromGobiiTreeNodes.State;
    history: fromHistory.State;

    // collection: fromCollection.State;
    // layout: fromLayout.State;
    // router: fromRouter.RouterState;
}


export const reducers: ActionReducerMap<State> = {
    fileItems: fromFileItems.fileItemsReducer,
    gobiiTreeNodes: fromGobiiTreeNodes.gobiiTreeNodesReducer,
    history: fromHistory.historyReducer
};


/**
 * Because metareducers take a fileItemsReducer function and return a new fileItemsReducer,
 * we can use our compose helper to chain them together. Here we are
 * using combineReducers to make our top level fileItemsReducer, and then
 * wrapping that in storeLogger. Remember that compose applies
 * the result from right to left.
 */
// const reducers = {
//     fileItems: fromFileItems.fileItemsReducer
//     // books: fromBooks.fileItemsReducer,
//     // collection: fromCollection.fileItemsReducer,
//     // layout: fromLayout.fileItemsReducer,
//     // router: fromRouter.routerReducer,
// };

const developmentReducer: ActionReducer<State> = compose(combineReducers)(reducers);
const productionReducer: ActionReducer<State> = combineReducers(reducers);

// export function reducer(state: any, action: any) {
//     if (environmentSettings.production) {
//         return productionReducer(state, action);
//     } else {
//         return developmentReducer(state, action);
//     }
//  }


/**
 * A selector function is a map function factory. We pass it parameters and it
 * returns a function that maps from the larger state tree into a smaller
 * piece of state. This selector simply selects the `books` state.
 *
 * Selectors are used with the `select` operator.
 *
 * ```ts
 * class MyComponent {
 * 	constructor(state$: Observable<State>) {
 * 	  this.booksState$ = state$.select(getFileItemsState);
 * 	}
 * }
 * ```
 */
export const getFileItemsState = (state: State) => state.fileItems;

/**
 * Every fileItemsReducer module exports selector functions, however child reducers
 * have no knowledge of the overall state tree. To make them useable, we
 * need to make new selectors that wrap them.
 *
 * The createSelector function from the reselect library creates
 * very efficient selectors that are memoized and only recompute when arguments change.
 * The created selectors can also be composed together to select different
 * pieces of state.
 */

export const getCrops = createSelector(getFileItemsState, fromFileItems.getCrops);
export const getSelectedCrop = createSelector(getFileItemsState, fromFileItems.getSelectedCrop);
export const getFileItemsFilters = createSelector(getFileItemsState, fromFileItems.getFilters);
export const getAllFileItems = createSelector(getFileItemsState,  fromFileItems.getAll);
export const getAllCropFilItems  = createSelector(getFileItemsState, fromFileItems.getCropFileItems);
export const getSelectedFileItems = createSelector(getFileItemsState, fromFileItems.getSelected);



export const getPiContacts = createSelector(getFileItemsState, fromFileItems.getPiContacts);
export const getProjects = createSelector(getFileItemsState, fromFileItems.getProjects);
export const getExperiments = createSelector(getFileItemsState, fromFileItems.getExperiments);
export const getDatasets = createSelector(getFileItemsState, fromFileItems.getDatasets);
export const getCvTermsDataType = createSelector(getFileItemsState, fromFileItems.getCvTermsDataType);
export const getCvTermsJobStatus = createSelector(getFileItemsState, fromFileItems.getCvTermsJobStatus);
export const getMapsets = createSelector(getFileItemsState, fromFileItems.getMapsets);
export const getPlatforms = createSelector(getFileItemsState, fromFileItems.getPlatforms);
export const getMarkerGroups = createSelector(getFileItemsState, fromFileItems.getMarkerGroups);
export const getProjectsByPI = createSelector(getFileItemsState, fromFileItems.getProjectsForSelectedPi);
export const getExperimentsByProject = createSelector(getFileItemsState, fromFileItems.getExperimentsForSelectedProject);
export const getDatasetsByExperiment = createSelector(getFileItemsState, fromFileItems.getDatasetsForSelectedExperiment);
export const getSelectedPiContacts = createSelector(getFileItemsState, fromFileItems.getSelectedPiContacts);
export const getDatasetEntities = createSelector(getFileItemsState, fromFileItems.getDatasetEntities);
export const getDatasetEntitiesPaged = createSelector(getFileItemsState, fromFileItems.getDatasetEntitiesPaged);

export const getPiContactsFilterOptional = createSelector(getFileItemsState, fromFileItems.getPiContactsFilterOptional)
export const getProjectsFilterOptional = createSelector(getFileItemsState, fromFileItems.getProjectsFilterOptional)
export const getExperimentsFilterOptional = createSelector(getFileItemsState, fromFileItems.getExperimentsFilterOptional)

export const getSelectedFileFormat = createSelector(getFileItemsState, fromFileItems.getSelectedFileFormat);
export const getSelectedSampleType = createSelector(getFileItemsState, fromFileItems.getSelectedSampleType);
export const getJobId = createSelector(getFileItemsState, fromFileItems.getJobId);
export const getUploadFiles = createSelector(getFileItemsState, fromFileItems.getUploadFiles);

export const getGobiiTreeNodesState = (state: State) => state.gobiiTreeNodes;
export const getAllGobiiTreeNodes = createSelector(getGobiiTreeNodesState, fromGobiiTreeNodes.getAll);
export const getGobiiTreeNodesForExtractFilter = createSelector(getGobiiTreeNodesState, fromGobiiTreeNodes.getForSelectedFilter);
export const getSelectedGobiiTreeNodes = createSelector(getGobiiTreeNodesState, fromGobiiTreeNodes.getSelected);
export const getSelectedGobiiTreeNodeIds = createSelector(getGobiiTreeNodesState, fromGobiiTreeNodes.getGobiiTreeItemIds);
export const getIdsOfActivatedGobiiTreeNodes = createSelector(getGobiiTreeNodesState, fromGobiiTreeNodes.getIdsOfActivated);

export const gobiiHistoryState = (state: State) => state.history;
export const getStatusMessages = createSelector(gobiiHistoryState, fromHistory.getStatusMessages);
export const getLasetStatusMessage = createSelector(gobiiHistoryState, fromHistory.getLastMessage);
export const getFiltersRetrieved = createSelector(gobiiHistoryState, fromHistory.getFiltersRetrieved);


/**
 * Just like with the books selectors, we also have to compose the search
 * fileItemsReducer's and collection fileItemsReducer's selectors.
 */
// export const getSearchState = (state: State) => state.search;
// export const getSearchBookIds = createSelector(getSearchState, fromSearch.getGobiiTreeItemIds);
// export const getSearchQuery = createSelector(getSearchState, fromSearch.getQuery);
// export const getSearchLoading = createSelector(getSearchState, fromSearch.getLoading);


/**
 * Some selector functions create joins across parts of state. This selector
 * composes the search result IDs to return an array of books in the store.
 */
// export const getSearchResults = createSelector(getAllFileItems, getSearchBookIds, (books, searchIds) => {
//     return searchIds.map(id => books[id]);
// });


// export const getCollectionState = (state: State) => state.collection;
//
// export const getCollectionLoaded = createSelector(getCollectionState, fromCollection.getLoaded);
// export const getCollectionLoading = createSelector(getCollectionState, fromCollection.getLoading);
// export const getCollectionBookIds = createSelector(getCollectionState, fromCollection.getGobiiTreeItemIds);
//
// export const getBookCollection = createSelector(getAllFileItems, getCollectionBookIds, (entities, ids) => {
//     return ids.map(id => entities[id]);
// });
//
// export const isSelectedBookInCollection = createSelector(getCollectionBookIds, getFileItems, (ids, selected) => {
//     return ids.indexOf(selected) > -1;
// });

/**
 * Layout Reducers
 */
// export const getLayoutState = (state: State) => state.layout;
//
// export const getShowSidenav = createSelector(getLayoutState, fromLayout.getShowSidenav);
