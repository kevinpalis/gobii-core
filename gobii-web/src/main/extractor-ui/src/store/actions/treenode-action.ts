import {Action} from '@ngrx/store';
import {GobiiTreeNode} from "../../model/GobiiTreeNode";
import {GobiiExtractFilterType} from "../../model/type-extractor-filter";
import {TypeTreeNodeStatus} from "../../model/type-tree-node-status";
import {GobiiFileItemCompoundId} from "../../model/gobii-file-item-compound-id";

export const INIT = '[GobiiTreeNode] Init';
export const INIT_TREE = '[GobiiTreeNode] Load Tree Nodes';
export const PLACE_TREE_NODE = '[GobiiTreeNode] Add Tree Node';
export const ACTIVATE = '[GobiiTreeNode] Activate';
export const DEACTIVATE = '[GobiiTreeNode] Deactivate';
export const SELECT_EXTRACT_TYPE = '[GobiiTreeNode] Select Extract Type';
export const CLEAR_ALL = '[GobiiTreeNode] Clear All';
export const SET_NODE_STATUS = '[GobiiTreeNode] Set Node Status';


export class InitAction implements Action {
    readonly type = INIT;

}


/**
 * Every action is comprised of at least a type and an optional
 * payload. Expressing actions as classes enables powerful
 * type checking in fileItemsReducer functions.
 *
 * See Discriminated Unions: https://www.typescriptlang.org/docs/handbook/advanced-types.html#discriminated-unions
 */
export class InitTree implements Action {
    readonly type = INIT_TREE;

    constructor(public payload: GobiiTreeNode[]) {
    }
}

export class PlaceTreeNodeAction implements Action {
    readonly type = PLACE_TREE_NODE;

    constructor(public payload: GobiiTreeNode) {
    }
}


export class ActivateForExtractAction implements Action {
    readonly type = ACTIVATE;

    constructor(public payload: string) {
    }
}

export class DeActivateFromExtractAction implements Action {
    readonly type = DEACTIVATE;

    //fileitemuniqueid
    constructor(public payload: string) {
    }
}

export class ClearAll implements Action {
    readonly type = CLEAR_ALL;

    constructor(public payload: GobiiExtractFilterType) {
    }
}

export class SelectExtractType implements Action {
    readonly type = SELECT_EXTRACT_TYPE;

    constructor(public payload: GobiiExtractFilterType) {
    }
}

export class SetTreeNodeLook implements Action {
    readonly type = SET_NODE_STATUS;

    constructor(public payload: {
        gobiiExtractFilterType: GobiiExtractFilterType,
        gobiiFileItemCompoundId: GobiiFileItemCompoundId,
        icon: string
    }) {
    }
}


/**
 * Export a type alias of all actions in this action group
 * so that reducers can easily compose action types
 */
export type All
    = InitTree
    | PlaceTreeNodeAction
    | ActivateForExtractAction
    | DeActivateFromExtractAction
    | SelectExtractType
    | InitAction
    | ClearAll
    | SetTreeNodeLook;

