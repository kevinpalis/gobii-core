import {Action} from '@ngrx/store';
import {HeaderStatusMessage} from "../../model/dto-header-status-message";
import {FilterHistory} from "../reducers/history-reducer";

export const ADD_STATUS = '[GobiiHistory] Add Status';
export const ADD_STATUS_MESSAGE = '[GobiiHistory] Add Status Message';
export const CLEAR_STATUS = '[GobiiHistory] Clear Status';
export const ADD_FILTER_RETRIEVED = '[GobiiHistory] Add Filter Submitted';

/**
 * Every action is comprised of at least a type and an optional
 * payload. Expressing actions as classes enables powerful
 * type checking in fileItemsReducer functions.
 *
 * See Discriminated Unions: https://www.typescriptlang.org/docs/handbook/advanced-types.html#discriminated-unions
 */
export class AddStatusAction implements Action {
    readonly type = ADD_STATUS;

    constructor(public payload: HeaderStatusMessage) {
    }
}

export class AddStatusMessageAction implements Action {
    readonly type = ADD_STATUS_MESSAGE;

    constructor(public payload: string) {
    }
}

export class AddFilterRetrieved implements Action {
    readonly type = ADD_FILTER_RETRIEVED;

    constructor(public payload: FilterHistory) {
    }
}

export class ClearStatusesAction implements Action {
    readonly type = CLEAR_STATUS;

    constructor() {
    }
}



/**
 * Export a type alias of all actions in this action group
 * so that reducers can easily compose action types
 */
export type All
    = AddStatusAction
    | AddStatusMessageAction
    | AddFilterRetrieved
    | ClearStatusesAction;

