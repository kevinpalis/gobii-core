import {Injectable} from "@angular/core";
import {ContainerType, GobiiTreeNode} from "../../model/GobiiTreeNode";
import {EntitySubType, EntityType} from "../../model/type-entity";
import {Labels} from "../../views/entity-labels";
import {ExtractorItemType} from "../../model/type-extractor-item";
import {GobiiExtractFilterType} from "../../model/type-extractor-filter";
import {CvFilterType} from "../../model/cv-filter-type";
import {GobiiFileItem} from "../../model/gobii-file-item";
import {GobiiExtractFormat} from "../../model/type-extract-format";
import {ProcessType} from "../../model/type-process";
import {GobiiFileItemCompoundId} from "../../model/gobii-file-item-compound-id";
import {TypeTreeNodeStatus} from "../../model/type-tree-node-status";
import * as treeNodeActions from '../../store/actions/treenode-action'
import {Store} from "@ngrx/store";
import * as fromRoot from '../../store/reducers';


@Injectable()
export class TreeStructureService {

    constructor(private store: Store<fromRoot.State>) {
    }


    private makeCommonNodes(gobiiExtractFilterType: GobiiExtractFilterType): GobiiTreeNode[] {

        let returnVal: GobiiTreeNode[] = [
            GobiiTreeNode.build(gobiiExtractFilterType, ExtractorItemType.CROP_TYPE)
                .setGenericLabel(Labels.instance().treeExtractorTypeLabels[ExtractorItemType.CROP_TYPE]),
            GobiiTreeNode.build(gobiiExtractFilterType, ExtractorItemType.JOB_ID)
                .setGenericLabel(Labels.instance().treeExtractorTypeLabels[ExtractorItemType.JOB_ID]),
            GobiiTreeNode.build(gobiiExtractFilterType, ExtractorItemType.ENTITY)
                .setEntityType(EntityType.CONTACT)
                .setEntitySubType(EntitySubType.CONTACT_SUBMITED_BY)
                .setGenericLabel(Labels.instance().entityNodeLabels[EntitySubType.CONTACT_SUBMITED_BY]),
            GobiiTreeNode.build(gobiiExtractFilterType, ExtractorItemType.EXPORT_FORMAT)
                .setGenericLabel(Labels.instance().treeExtractorTypeLabels[ExtractorItemType.EXPORT_FORMAT]),
            GobiiTreeNode.build(gobiiExtractFilterType, ExtractorItemType.ENTITY)
                .setEntityType(EntityType.MAPSET)
                .setGenericLabel(Labels.instance().entityNodeLabels[EntityType.MAPSET]),
        ]

        return returnVal;
    }


    // Note that we aren't storing these nodes as state -- we only make them
    // and return them. We rely on the store to make them part of state.
    // For now, this is the only context in which container type is ste on tree nodes;
    // tree nodes that are created dynamically will only ever replace or be stored within
    // one of these original structural nodes. See the tree nodes reducer for how the
    // container types are actually used
    public getInitialTree(): GobiiTreeNode[] {
        let returnVal: GobiiTreeNode[] = [

            // BY DATASET
            ...this.makeCommonNodes(GobiiExtractFilterType.WHOLE_DATASET),
            GobiiTreeNode.build(GobiiExtractFilterType.WHOLE_DATASET, ExtractorItemType.ENTITY)
                .setEntityType(EntityType.DATASET)
                .setContainerType(ContainerType.DATA)
            ,

            // BY SAMPLE
            ...this.makeCommonNodes(GobiiExtractFilterType.BY_SAMPLE),
            GobiiTreeNode.build(GobiiExtractFilterType.BY_SAMPLE, ExtractorItemType.ENTITY)
                .setEntityType(EntityType.CV)
                .setCvFilterType(CvFilterType.DATASET_TYPE),
            GobiiTreeNode.build(GobiiExtractFilterType.BY_SAMPLE, ExtractorItemType.SAMPLE_LIST_TYPE),
            GobiiTreeNode.build(GobiiExtractFilterType.BY_SAMPLE, ExtractorItemType.ENTITY)
                .setEntityType(EntityType.PLATFORM)
                .setContainerType(ContainerType.DATA),
            GobiiTreeNode.build(GobiiExtractFilterType.BY_SAMPLE, ExtractorItemType.TREE_STRUCTURE)
                .setContainerType(ContainerType.STRUCTURE)
                .setLabel("Samples Criteria")
                .setExpanded(true)
                .setChildren([
                    GobiiTreeNode.build(GobiiExtractFilterType.BY_SAMPLE, ExtractorItemType.ENTITY)
                        .setEntityType(EntityType.CONTACT)
                        .setEntitySubType(EntitySubType.CONTACT_PRINCIPLE_INVESTIGATOR)
                        .setContainerType(ContainerType.NONE),
                    GobiiTreeNode.build(GobiiExtractFilterType.BY_SAMPLE, ExtractorItemType.ENTITY)
                        .setEntityType(EntityType.PROJECT)
                        .setContainerType(ContainerType.NONE),
                    GobiiTreeNode.build(GobiiExtractFilterType.BY_SAMPLE, ExtractorItemType.SAMPLE_FILE),
                    GobiiTreeNode.build(GobiiExtractFilterType.BY_SAMPLE, ExtractorItemType.SAMPLE_LIST_ITEM)
                        .setContainerType(ContainerType.DATA),
                ]),


            // BY MARKER
            ...this.makeCommonNodes(GobiiExtractFilterType.BY_MARKER),
            GobiiTreeNode.build(GobiiExtractFilterType.BY_MARKER, ExtractorItemType.ENTITY)
                .setEntityType(EntityType.CV)
                .setCvFilterType(CvFilterType.DATASET_TYPE),
            GobiiTreeNode.build(GobiiExtractFilterType.BY_MARKER, ExtractorItemType.TREE_STRUCTURE)
                .setContainerType(ContainerType.STRUCTURE)
                .setLabel("Markers Criteria")
                .setExpanded(true)
                .setChildren([
                    GobiiTreeNode.build(GobiiExtractFilterType.BY_MARKER, ExtractorItemType.ENTITY)
                        .setEntityType(EntityType.PLATFORM)
                        .setContainerType(ContainerType.DATA),
                    GobiiTreeNode.build(GobiiExtractFilterType.BY_MARKER, ExtractorItemType.MARKER_FILE),
                    GobiiTreeNode.build(GobiiExtractFilterType.BY_MARKER, ExtractorItemType.MARKER_LIST_ITEM)
                        .setContainerType(ContainerType.DATA),
                    GobiiTreeNode.build(GobiiExtractFilterType.BY_MARKER, ExtractorItemType.ENTITY)
                        .setEntityType(EntityType.MARKER_GROUP)
                        .setContainerType(ContainerType.DATA)
                ])
        ];

        // we know we only have to go one level deep in this case -- no need to recurse
        returnVal.forEach(function (currentNode, idx, nodes) {

            currentNode.getChildren().forEach(currentChild => {
                currentChild.parent = currentNode
            })
        });

        this.setTreeNodeProperties(returnVal);
        return returnVal;

    }

    private applyLabel(gobiiTreeNode: GobiiTreeNode) {

        let labelValue: string = null;

        if (gobiiTreeNode.getItemType() === ExtractorItemType.ENTITY) {

            if (gobiiTreeNode.getEntitySubType() === EntitySubType.UNKNOWN) {

                if (gobiiTreeNode.getEntityType() !== EntityType.CV) {
                    labelValue = Labels.instance().entityNodeLabels[gobiiTreeNode.getEntityType()];
                } else {
                    labelValue = Labels.instance().cvFilterNodeLabels[gobiiTreeNode.getCvFilterType()];
                }
            } else {
                labelValue = Labels.instance().entitySubtypeNodeLabels[gobiiTreeNode.getEntitySubType()];
            }

        } else {
            labelValue = Labels.instance().treeExtractorTypeLabels[gobiiTreeNode.getItemType()];
        }

        gobiiTreeNode.setGenericLabel(labelValue);
        gobiiTreeNode.setLabel(labelValue);

    }

    private setTreeNodeProperties(treeNodes: GobiiTreeNode[]) {

        treeNodes.forEach(tn => {
            if (( tn.children === null ) || ( tn.children.length <= 0  )) {
                this.addIconsToNode(tn, false);
                this.applyLabel(tn);
            } else {
                this.setTreeNodeProperties(tn.children);
            }
        })

    }
    
    private getEntityIcon(gobiiFileItemCompoundId:GobiiFileItemCompoundId):{ icon: string, expandedIcon: string, collapsedIcon: string } {

        let icon: string;
        let expandedIcon: string;
        let collapsedIcon: string;
        
        if (gobiiFileItemCompoundId.getEntityType() === EntityType.DATASET) {

            icon = "pi pi-copy";
            expandedIcon = "pi pi-folder-open";
            collapsedIcon = "pi pi-copy";

        } else if (gobiiFileItemCompoundId.getEntityType() === EntityType.CONTACT) {

            icon = "pi pi-user";
            expandedIcon = "pi pi-user";
            collapsedIcon = "pi pi-user";

        } else if (gobiiFileItemCompoundId.getEntityType() === EntityType.MAPSET) {

            icon = "pi pi-map-marker";
            expandedIcon = "pi pi-map-marker";
            collapsedIcon = "pi pi-map-marker";

        } else if (gobiiFileItemCompoundId.getEntityType() === EntityType.PLATFORM) {

            icon = "pi pi-bookmark";
            expandedIcon = "pi pi-bookmark";
            collapsedIcon = "pi pi-bookmark";

        } else if (gobiiFileItemCompoundId.getEntityType() === EntityType.PROJECT) {

            icon = "pi pi-compass";
            expandedIcon = "pi pi-compass";
            collapsedIcon = "pi pi-compass";

        } else if (gobiiFileItemCompoundId.getEntityType() === EntityType.CV) {

            if (gobiiFileItemCompoundId.getCvFilterType() === CvFilterType.DATASET_TYPE) {
                icon = "pi pi-file-excel";
                expandedIcon = "pi pi-file-excel";
                collapsedIcon = "pi pi-file-excel";
            }

        } else if (gobiiFileItemCompoundId.getEntityType() === EntityType.MARKER_GROUP) {

            // if (isParent) {
            icon = "pi pi-sitemap";
            expandedIcon = "pi pi-sitemap";
            collapsedIcon = "pi pi-sitemap";

        }

        return {icon: icon, expandedIcon: expandedIcon, collapsedIcon: collapsedIcon};
    }


    private addEntityIconToNode(entityType: EntityType, cvFilterType: CvFilterType, treeNode: GobiiTreeNode) {

        if (entityType === EntityType.DATASET) {

            treeNode.icon = "pi pi-copy";
            treeNode.expandedIcon = "pi pi-folder-open";
            treeNode.collapsedIcon = "pi pi-copy";

        } else if (entityType === EntityType.CONTACT) {

            treeNode.icon = "pi pi-user";
            treeNode.expandedIcon = "pi pi-user";
            treeNode.collapsedIcon = "pi pi-user";

        } else if (entityType === EntityType.MAPSET) {

            treeNode.icon = "pi pi-map-marker";
            treeNode.expandedIcon = "pi pi-map-marker";
            treeNode.collapsedIcon = "pi pi-map-marker";

        } else if (entityType === EntityType.PLATFORM) {

            treeNode.icon = "pi pi-bookmark";
            treeNode.expandedIcon = "pi pi-bookmark";
            treeNode.collapsedIcon = "pi pi-bookmark";

        } else if (entityType === EntityType.PROJECT) {

            treeNode.icon = "pi pi-compass";
            treeNode.expandedIcon = "pi pi-compass";
            treeNode.collapsedIcon = "pi pi-compass";

        } else if (entityType === EntityType.CV) {

            if (cvFilterType === CvFilterType.DATASET_TYPE) {
                treeNode.icon = "pi pi-file-excel";
                treeNode.expandedIcon = "pi pi-file-excel";
                treeNode.collapsedIcon = "pi pi-file-excel";
            }

        } else if (entityType === EntityType.MARKER_GROUP) {

            // if (isParent) {
            treeNode.icon = "pi pi-sitemap";
            treeNode.expandedIcon = "pi pi-sitemap";
            treeNode.collapsedIcon = "pi pi-sitemap";
          
        }
    }

    private getIcons(gobiiFileItemCompoundId: GobiiFileItemCompoundId, isParent:boolean): { icon: string, expandedIcon: string, collapsedIcon: string } {


        let icon: string;
        let expandedIcon: string;
        let collapsedIcon: string;

        if (gobiiFileItemCompoundId.getEntityType() != null
            && gobiiFileItemCompoundId.getEntityType() != EntityType.UNKNOWN) {

            let entityIcons = this.getEntityIcon(gobiiFileItemCompoundId);
            icon = entityIcons.icon;
            expandedIcon = entityIcons.expandedIcon;
            collapsedIcon = entityIcons.collapsedIcon;


            //this.addEntityIconToNode(gobiiFileItemCompoundId.getEntityType(), gobiiFileItemCompoundId.getCvFilterType(), treeNode);

        } else if (gobiiFileItemCompoundId.getExtractorItemType() === ExtractorItemType.EXPORT_FORMAT) {
            icon = "pi pi-pencil";
            expandedIcon = "pi pi-pencil";
            collapsedIcon = "pi pi-pencil";
        } else if (gobiiFileItemCompoundId.getExtractorItemType() === ExtractorItemType.SAMPLE_FILE) {
            icon = "pi pi-paperclip";
            expandedIcon = "pi pi-paperclip";
            collapsedIcon = "pi pi-paperclip";
        } else if (gobiiFileItemCompoundId.getExtractorItemType() === ExtractorItemType.SAMPLE_LIST_ITEM) {
            if (isParent) {
                icon = "pi pi-list";
                expandedIcon = "pi pi-list";
                collapsedIcon = "pi pi-list";
            } else {
                icon = "pi pi-list";
                expandedIcon = "pi pi-list";
                collapsedIcon = "pi pi-list";
            }
        } else if (gobiiFileItemCompoundId.getExtractorItemType() === ExtractorItemType.MARKER_FILE) {
            icon = "pi pi-paperclip";
            expandedIcon = "pi pi-paperclip";
            collapsedIcon = "pi pi-paperclip";
        } else if (gobiiFileItemCompoundId.getExtractorItemType() === ExtractorItemType.MARKER_LIST_ITEM) {

            if (isParent) {
                icon = "pi pi-list";
                expandedIcon = "pi pi-list";
                collapsedIcon = "pi pi-list";
            } else {
                icon = "pi pi-list";
                expandedIcon = "pi pi-list";
                collapsedIcon = "pi pi-list";
            }
        } else if (gobiiFileItemCompoundId.getExtractorItemType() === ExtractorItemType.JOB_ID) {
            icon = "pi pi-ticket";
            expandedIcon = "pi pi-ticket";
            collapsedIcon = "pi pi-ticket";
        } else if (gobiiFileItemCompoundId.getExtractorItemType() === ExtractorItemType.SAMPLE_LIST_TYPE) {
            icon = "pi pi-list";
            expandedIcon = "pi pi-list";
            collapsedIcon = "pi pi-list";
        } else {
            //     }
            // } else if (fileModelNode.getItemType() == ExtractorItemType.CATEGORY ) {
            icon = "pi pi-folder";
            expandedIcon = "pi pi-folder-open";
            collapsedIcon = "pi pi-folder";
        }

        return {icon: icon, expandedIcon: expandedIcon, collapsedIcon: collapsedIcon};
    }

    private addIconsToNode(treeNode: GobiiTreeNode, isParent: boolean) {

        let icons = this.getIcons(treeNode,isParent);
        treeNode.icon = icons.icon;
        treeNode.expandedIcon = icons.expandedIcon;
        treeNode.collapsedIcon = icons.collapsedIcon;

        // if (treeNode.getEntityType() != null
        //     && treeNode.getEntityType() != EntityType.UNKNOWN) {
        //
        //     this.addEntityIconToNode(treeNode.getEntityType(), treeNode.getCvFilterType(), treeNode);
        //
        // } else if (treeNode.getItemType() === ExtractorItemType.EXPORT_FORMAT) {
        //     treeNode.icon = "pi pi-pencil";
        //     treeNode.expandedIcon = "pi pi-pencil";
        //     treeNode.collapsedIcon = "pi pi-pencil";
        // } else if (treeNode.getItemType() === ExtractorItemType.SAMPLE_FILE) {
        //     treeNode.icon = "pi pi-paperclip";
        //     treeNode.expandedIcon = "pi pi-paperclip";
        //     treeNode.collapsedIcon = "pi pi-paperclip";
        // } else if (treeNode.getItemType() === ExtractorItemType.SAMPLE_LIST_ITEM) {
        //     if (isParent) {
        //         treeNode.icon = "pi pi-list";
        //         treeNode.expandedIcon = "pi pi-list";
        //         treeNode.collapsedIcon = "pi pi-list";
        //     } else {
        //         treeNode.icon = "pi pi-list";
        //         treeNode.expandedIcon = "pi pi-list";
        //         treeNode.collapsedIcon = "pi pi-list";
        //     }
        // } else if (treeNode.getItemType() === ExtractorItemType.MARKER_FILE) {
        //     treeNode.icon = "pi pi-paperclip";
        //     treeNode.expandedIcon = "pi pi-paperclip";
        //     treeNode.collapsedIcon = "pi pi-paperclip";
        // } else if (treeNode.getItemType() === ExtractorItemType.MARKER_LIST_ITEM) {
        //
        //     if (isParent) {
        //         treeNode.icon = "pi pi-list";
        //         treeNode.expandedIcon = "pi pi-list";
        //         treeNode.collapsedIcon = "pi pi-list";
        //     } else {
        //         treeNode.icon = "pi pi-list";
        //         treeNode.expandedIcon = "pi pi-list";
        //         treeNode.collapsedIcon = "pi pi-list";
        //     }
        // } else if (treeNode.getItemType() === ExtractorItemType.JOB_ID) {
        //     treeNode.icon = "pi pi-list";
        //     treeNode.expandedIcon = "pi pi-list";
        //     treeNode.collapsedIcon = "pi pi-list";
        // } else if (treeNode.getItemType() === ExtractorItemType.SAMPLE_LIST_TYPE) {
        //     treeNode.icon = "pi pi-list";
        //     treeNode.expandedIcon = "pi pi-list";
        //     treeNode.collapsedIcon = "pi pi-list";
        // } else {
        //     //     }
        //     // } else if (fileModelNode.getItemType() == ExtractorItemType.CATEGORY ) {
        //     treeNode.icon = "pi pi-folder";
        //     treeNode.expandedIcon = "pi pi-folder-open";
        //     treeNode.collapsedIcon = "pi pi-folder";
        // }
    }

    public makeTreeNodeFromFileItem(gobiiFileItem: GobiiFileItem): GobiiTreeNode {

        let returnVal: GobiiTreeNode = GobiiTreeNode
            .build(gobiiFileItem.getGobiiExtractFilterType(), gobiiFileItem.getExtractorItemType())
            .setFileItemId(gobiiFileItem.getFileItemUniqueId())
            .setEntityType(gobiiFileItem.getEntityType())
            .setEntitySubType(gobiiFileItem.getEntitySubType())
            .setCvFilterType(gobiiFileItem.getCvFilterType());

        this.addIconsToNode(returnVal, false);
        this.applyLabel(returnVal);
        this.addFileItemNameToNode(returnVal, gobiiFileItem);

        return returnVal;
    }

    addFileItemNameToNode(gobiiTreeNode: GobiiTreeNode, gobiiFileItem: GobiiFileItem) {

        if (gobiiTreeNode.getContainerType() === ContainerType.DATA) {
            gobiiTreeNode.label = gobiiFileItem.getItemName();
        } else { // coves the LEAF node use case
            if (gobiiFileItem.getExtractorItemType() == ExtractorItemType.EXPORT_FORMAT) {
                let gobiiExtractFormat: GobiiExtractFormat = <GobiiExtractFormat> GobiiExtractFormat[gobiiFileItem.getItemId()];
                gobiiTreeNode.label += ": " + Labels.instance().extractFormatTypeLabels[gobiiExtractFormat];
            } else if (gobiiFileItem.getExtractorItemType() == ExtractorItemType.JOB_ID) {
                gobiiTreeNode.label = Labels.instance().treeExtractorTypeLabels[ExtractorItemType.JOB_ID]
                    + ": " + gobiiFileItem.getItemId();
            } else {
                if (gobiiFileItem.getProcessType() !== ProcessType.DELETE) {
                    gobiiTreeNode.label += ": " + gobiiFileItem.getItemName();
                } else {
                    gobiiTreeNode.label = gobiiFileItem.getItemName();
                }
            }
        }
    }

    public markTreeItemMissing(gobiiExtractFilterType: GobiiExtractFilterType, gobiiFileItemCompoundId: GobiiFileItemCompoundId) {


        //let icon: string = "fa-chevron-circle-right";
        let icon: string = "pi pi-circle-on";
        //let icon: string = "fa-chevron-right";

        this.store.dispatch(new treeNodeActions.SetTreeNodeLook(
            {
                gobiiExtractFilterType: gobiiExtractFilterType,
                gobiiFileItemCompoundId: gobiiFileItemCompoundId,
                icon: icon
            }
        ))
    }

    public unMarkTreeItemMissing(gobiiExtractFilterType: GobiiExtractFilterType, gobiiFileItemCompoundId: GobiiFileItemCompoundId) {

        let icons = this.getIcons(gobiiFileItemCompoundId, false);

        this.store.dispatch(new treeNodeActions.SetTreeNodeLook(
            {
                gobiiExtractFilterType: gobiiExtractFilterType,
                gobiiFileItemCompoundId: gobiiFileItemCompoundId,
                icon: icons.icon
            }
        ))
    }

}
