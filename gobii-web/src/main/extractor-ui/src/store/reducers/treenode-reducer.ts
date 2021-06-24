import {createSelector} from 'reselect';
import * as gobiiTreeNodeAction from "../actions/treenode-action";
import {ContainerType, GobiiTreeNode} from "../../model/GobiiTreeNode";
import {GobiiExtractFilterType} from "../../model/type-extractor-filter";
import {GobiiFileItemCompoundId} from "../../model/gobii-file-item-compound-id";
import {TypeTreeNodeStatus} from "../../model/type-tree-node-status";
import * as util from 'util';

export interface State {
    gobiiExtractFilterType: GobiiExtractFilterType;
    gobiiTreeNodesActive: string[];
    gobiiTreeNodes: GobiiTreeNode[] ;
};

export const initialState: State = {
    gobiiExtractFilterType: GobiiExtractFilterType.UNKNOWN,
    gobiiTreeNodesActive: [],
    gobiiTreeNodes: [],
};


function placeNodeInTree(nodeToPlace: GobiiTreeNode, treeNodes: GobiiTreeNode[], gobiiExtractFilterType: GobiiExtractFilterType): boolean {

    let returnVal: boolean = false;

    for (let idx: number = 0; !returnVal && (idx < treeNodes.length); idx++) {

        let currentTreenode: GobiiTreeNode = treeNodes[idx];
        if (currentTreenode.getGobiiExtractFilterType() === gobiiExtractFilterType &&
            currentTreenode.compoundIdeEquals(nodeToPlace)
            && currentTreenode.getContainerType() !== ContainerType.STRUCTURE
        ) {
            if (currentTreenode.getContainerType() === ContainerType.NONE) {
                treeNodes[idx] = nodeToPlace;
            } else if (currentTreenode.getContainerType() === ContainerType.DATA) {
                let containerNode: GobiiTreeNode = treeNodes[idx];
                nodeToPlace.parent = containerNode;
                
                containerNode.getChildren().push(nodeToPlace);
                containerNode.expanded = true;
            }

            returnVal = true;

        } else {
            returnVal = placeNodeInTree(nodeToPlace, currentTreenode.getChildren(), gobiiExtractFilterType);
        }
    }

    return returnVal;
}

function findTreeNodeByFIleItemId(treeNodes: GobiiTreeNode[], fileItemUniqueId: string): GobiiTreeNode {

    let returnVal: GobiiTreeNode = null;

    //use an oldfashioned for loop because the lambda based semantics don't have a break;
    for (let idx: number = 0; (idx < treeNodes.length) && !returnVal; idx++) {

        let currentTreeNode: GobiiTreeNode = treeNodes[idx];
        if (currentTreeNode.getFileItemId() === fileItemUniqueId) {
            returnVal = currentTreeNode;
        } else {
            returnVal = findTreeNodeByFIleItemId(currentTreeNode.getChildren(), fileItemUniqueId);
        }
    }

    return returnVal;
}

function findTreeNodeByCompoundId(treeNodes: GobiiTreeNode[],
                                  gobiiExtractFilterType: GobiiExtractFilterType,
                                  gobiiFileItemCompoundId: GobiiFileItemCompoundId): GobiiTreeNode {

    let returnVal: GobiiTreeNode = null;


    for (let idx: number = 0; (idx < treeNodes.length) && !returnVal; idx++) {

        let currentTreeNode: GobiiTreeNode = treeNodes[idx];
        if (currentTreeNode.getGobiiExtractFilterType() === gobiiExtractFilterType
            && gobiiFileItemCompoundId.compoundIdeEquals(currentTreeNode)) {
            returnVal = currentTreeNode;
        } else {
            returnVal = findTreeNodeByCompoundId(currentTreeNode.getChildren(), gobiiExtractFilterType, gobiiFileItemCompoundId);
        }
    }

    return returnVal;
}

function findTargetNode(treeNodes: GobiiTreeNode[], targetId: string): GobiiTreeNode {
    for (let i = 0; i < treeNodes.length; i++) {
        if (treeNodes[i].getId() == targetId) return treeNodes[i];
        //check children
        let fromChildren: GobiiTreeNode = findTargetNode(treeNodes[i].getChildren(), targetId);
        if (fromChildren) return fromChildren;
    }

    return null;

}

export function gobiiTreeNodesReducer(state: State = initialState, action: gobiiTreeNodeAction.All): State {
    let returnVal: State = state;

    switch (action.type) {

        case gobiiTreeNodeAction.INIT_TREE: {
            const gobiigobiiTreeItemsPayload = action.payload.map(node => GobiiTreeNode.copy(node));
            
            returnVal = {
                gobiiExtractFilterType: state.gobiiExtractFilterType,
                gobiiTreeNodesActive: state.gobiiTreeNodesActive,
                gobiiTreeNodes: gobiigobiiTreeItemsPayload
            };
            
            break;
        } // INIT_TREE


        case gobiiTreeNodeAction.PLACE_TREE_NODE: {

            const gobiiTreeNodePayload: GobiiTreeNode = GobiiTreeNode.copy(action.payload);

            // copy the existing
            const newTreeNodes = state.gobiiTreeNodes.slice().map(node => GobiiTreeNode.copy(node));
            if (placeNodeInTree(gobiiTreeNodePayload, newTreeNodes, state.gobiiExtractFilterType)) {

                returnVal = {
                    gobiiExtractFilterType: state.gobiiExtractFilterType,
                    gobiiTreeNodesActive: state.gobiiTreeNodesActive,
                    gobiiTreeNodes: newTreeNodes
                };
            }

            break;
        } // INIT_TREE


        case gobiiTreeNodeAction.ACTIVATE: {

            const fileItemUniqueId: string = action.payload;

            let newNodes: GobiiTreeNode[] = state.gobiiTreeNodes.map(node => GobiiTreeNode.copy(node));
            returnVal = {
                gobiiExtractFilterType: state.gobiiExtractFilterType,
                gobiiTreeNodesActive: [...state.gobiiTreeNodesActive, ...[fileItemUniqueId]],
                gobiiTreeNodes: newNodes
            };

            break;

        } // ADD_TO_EXTRACT

        case gobiiTreeNodeAction.DEACTIVATE: {

            const gobiiFileItemUniqueId = action.payload;
            const newTreeNodes = state.gobiiTreeNodes.map(node => GobiiTreeNode.copy(node));

            const gobiiTreeNodeToDeActivate: GobiiTreeNode = findTreeNodeByFIleItemId(newTreeNodes
                    .filter(tn => tn.getGobiiExtractFilterType() === state.gobiiExtractFilterType),
                gobiiFileItemUniqueId);

            // not all file items are in the tree
            if( gobiiTreeNodeToDeActivate ) {

                let containerType: ContainerType = gobiiTreeNodeToDeActivate.parent ?
                    gobiiTreeNodeToDeActivate.parent.getContainerType() :
                    ContainerType.NONE;

                if (containerType === ContainerType.NONE
                    || containerType === ContainerType.STRUCTURE) {
                    gobiiTreeNodeToDeActivate.resetLabel();
                } else {
                    let targetId: string = gobiiTreeNodeToDeActivate.parent.getId();
                    let parent: GobiiTreeNode = findTargetNode(newTreeNodes, targetId);
                    let children: GobiiTreeNode[] = parent.getChildren();
                    children.splice(children.indexOf(gobiiTreeNodeToDeActivate, 0), 1);
                }


                let newAcxtiveNodeState = state.gobiiTreeNodesActive
                    .filter(gtn => gtn !== gobiiTreeNodeToDeActivate.getId());

                returnVal = {
                    gobiiExtractFilterType: state.gobiiExtractFilterType,
                    gobiiTreeNodesActive: newAcxtiveNodeState,
                    gobiiTreeNodes: newTreeNodes
                };
            } else {
                returnVal = {
                    gobiiExtractFilterType: state.gobiiExtractFilterType,
                    gobiiTreeNodesActive: state.gobiiTreeNodesActive,
                    gobiiTreeNodes: state.gobiiTreeNodes.map(node => GobiiTreeNode.copy(node))
                };
            }

            break;

        } // ADD_TO_EXTRACT

        case gobiiTreeNodeAction.SELECT_EXTRACT_TYPE: {

            returnVal = {
                gobiiExtractFilterType: action.payload,
                gobiiTreeNodesActive: state.gobiiTreeNodesActive,
                gobiiTreeNodes: state.gobiiTreeNodes.map(node => GobiiTreeNode.copy(node))
            };

            break;

        } // SELECT_EXTRACT_TYPE

        case gobiiTreeNodeAction.SET_NODE_STATUS: {

            const gobiiExtractFilterType: GobiiExtractFilterType = action.payload.gobiiExtractFilterType;
            const gobiiFileItemCompoundId: GobiiFileItemCompoundId = action.payload.gobiiFileItemCompoundId;
            const icon: string = action.payload.icon;

            const newTreeNodesState = state.gobiiTreeNodes.slice().map(node => GobiiTreeNode.copy(node));

            let treeNodeToMutate: GobiiTreeNode = findTreeNodeByCompoundId(newTreeNodesState,
                gobiiExtractFilterType,
                gobiiFileItemCompoundId);

            treeNodeToMutate.icon = icon;

            returnVal = {
                gobiiExtractFilterType: state.gobiiExtractFilterType,
                gobiiTreeNodesActive: state.gobiiTreeNodesActive,
                gobiiTreeNodes: newTreeNodesState
            };

            break;

        }

        case gobiiTreeNodeAction.CLEAR_ALL: {

            let newSelectedNodes =
                state
                    .gobiiTreeNodesActive
                    .filter(id => state
                        .gobiiTreeNodes
                        .find(gtn => gtn.getFileItemId() === id
                            && gtn.getGobiiExtractFilterType() != action.payload));

            returnVal = {
                gobiiExtractFilterType: state.gobiiExtractFilterType,
                gobiiTreeNodesActive: newSelectedNodes,
                gobiiTreeNodes: state.gobiiTreeNodes.map(node => GobiiTreeNode.copy(node))
            };

            break;
        }
    }
    return returnVal;

}

export const getGobiiTreeNodes = (state: State) => state.gobiiTreeNodes.map(gti => GobiiTreeNode.copy(gti));
export const getGobiiTreeItemIds = (state: State) => state.gobiiTreeNodes.map(gti => gti.getId());

export const getIdsOfActivated = (state: State) => state.gobiiTreeNodesActive;

export const getExtractFilterType = (state: State) => state.gobiiExtractFilterType;

export const getSelected = createSelector(getGobiiTreeNodes, getIdsOfActivated, getExtractFilterType, (gobiiTreeNodes, selectedUniqueIds, getExtractFilterType) => {

        let returnVal: GobiiTreeNode[] = [];

        gobiiTreeNodes
            .forEach(function find(gtn) {
                if (selectedUniqueIds.filter(id => id === gtn.getId()).length > 0) {
                    returnVal.push(gtn);
                }
                gtn.getChildren().forEach(find);
            });


        return returnVal;

        // this needs to be done in a more filterish way. For now it works
        // let returnVal: GobiiTreeNode[] =
        //     gobiiTreeNodes
        //         .filter(gtn => gtn.getGobiiExtractFilterType() === getExtractFilterType)
        //         .filter(function find(gtn) {
        //             let returnVal: boolean = selectedUniqueIds.filter(id => id === gtn.getId()).length > 0;
        //             if (!returnVal) {
        //                 returnVal = ( gtn.getContainerType() != ContainerType.STRUCTURE )
        //                     && (gtn.getChildren().filter(find).length > 0);
        //             }
        //
        //             return returnVal;
        //         });
        //
        // return returnVal;
    }
);


//    let returnVal: GobiiTreeNode[] = [];
// gobiiTreeNodes
//     .filter(gtn => gtn.getGobiiExtractFilterType() === getExtractFilterType )
//     .forEach(n => {
//         selectedUniqueIds.forEach(i => {
//             if (n.getId() === i)
//                 returnVal.push(n);
//         })
//
//     }
// );


export const getAll = createSelector(getGobiiTreeNodes, getGobiiTreeItemIds, (treeItems, ids) => {
    return ids.map(id => treeItems[id]);
});

export const getForSelectedFilter = createSelector(getGobiiTreeNodes, getExtractFilterType, (treeItems, extractFilterType) => {
    return treeItems.filter(ti => ti.getGobiiExtractFilterType() === extractFilterType).map(node => GobiiTreeNode.copy(node));
});