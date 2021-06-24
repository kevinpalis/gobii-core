import {Component, EventEmitter, OnChanges, OnInit, SimpleChange} from "@angular/core";
import {TreeNode} from "primeng/api";
import {GobiiFileItem} from "../model/gobii-file-item";
import {GobiiTreeNode} from "../model/GobiiTreeNode";
import {GobiiExtractFilterType} from "../model/type-extractor-filter";
import {HeaderStatusMessage} from "../model/dto-header-status-message";
import * as fromRoot from '../store/reducers';
import {Store} from "@ngrx/store";
import {Observable} from "rxjs/Observable";
import {ViewIdGeneratorService} from "../services/core/view-id-generator-service";
import {TypeControl} from "../services/core/type-control";


//Documentation of p-tree: http://www.primefaces.org/primeng/#/tree
//width of p-tree does not take if it's in the style sheet class; you have to inline it
@Component({
    selector: 'status-display-tree',
    inputs: ['fileItemEventChange', 'gobiiExtractFilterTypeEvent'],
    outputs: ['onItemSelected', 'onItemChecked', 'onAddMessage', 'onTreeReady'],
    template: `
        <p-tree [value]="gobiiTreeNodesFromStore$ | async"
                [propagateSelectionUp]="false"
                [propagateSelectionDown]="false"
                [selection]="gobiiSelectedNodesFromStore$ | async"
                (onNodeUnselect)="nodeUnselect($event)"
                (onNodeSelect)="nodeSelect($event)"
                (onNodeExpand)="nodeExpand($event)"
                (onNodeCollapse)="nodeCollapse($event)"
                [style]="{'width':'100%'}"
                styleClass="criteria-tree"
                [id]="viewIdGeneratorService.makeStandardId(typeControl.CRITERIA_TREE)"></p-tree>
        
    `
})
export class StatusDisplayTreeComponent implements OnInit, OnChanges {


    public typeControl: any = TypeControl;
    private containerCollapseThreshold = 10;
    private onAddMessage: EventEmitter<HeaderStatusMessage> = new EventEmitter();
    private onTreeReady: EventEmitter<HeaderStatusMessage> = new EventEmitter();

    gobiiTreeNodesFromStore$: Observable<GobiiTreeNode[]>;
    gobiiSelectedNodesFromStore$: Observable<GobiiTreeNode[]>;

    constructor(private store: Store<fromRoot.State>,
                public viewIdGeneratorService: ViewIdGeneratorService) {
        //console.log("Tree constructed");
        this.gobiiTreeNodesFromStore$ = store
            .select(fromRoot.getGobiiTreeNodesForExtractFilter);

        this.gobiiSelectedNodesFromStore$ = store
            .select(fromRoot.getSelectedGobiiTreeNodes);
    }

    ngOnInit() {


    }


// *****************************************************************
// *********************  TREE NODE DATA STRUCTURES AND EVENTS

    // demoTreeNodes: TreeNode[] = [];
    // selectedDemoNodes: TreeNode[] = [];


    selectedGobiiNodes: GobiiTreeNode[] = [];

    experimentId: string;


    nodeSelect(event) {

        // for now the selectable property on nodes is set to false by default, so we aren't using this yet
    }


    nodeUnselect(event) {

    }


    nodeExpand(event) {
        if (event.node) {
        }
    }

    nodeCollapse(event) {
        if (event.node) {

        }
    }

    addCountToContainerNode(node: TreeNode) {
        let foo: string = "foo";

        let parenPosition: number = node.label.indexOf("(");
        if (parenPosition > 0) {
            node.label = node.label.substring(0, parenPosition);
        }

        if (node.children.length > 0) {

            node.label += " (" + node.children.length + ")";

        }


    }

    expandRecursive(node: TreeNode, isExpand: boolean) {
        node.expanded = isExpand;
        if (node.children) {
            node.children.forEach(childNode => {
                this.expandRecursive(childNode, isExpand);
            });
        }
    }


// ********************************************************************************
// ********************* CHECKBOX/TREE NODE CONVERSION FUNCTIONS


// ********************************************************************************
// ********************* CHECKBOX (GOBII-SPECIFIC)  NODE DATA STRUCTURES AND EVENTS


    gobiiExtractFilterType: GobiiExtractFilterType = GobiiExtractFilterType.UNKNOWN;

    onItemChecked: EventEmitter<GobiiFileItem> = new EventEmitter();
    onItemSelected: EventEmitter<GobiiFileItem> = new EventEmitter();


    ngOnChanges(changes: {
        [propName: string
            ]: SimpleChange
    }) {

        if (changes['fileItemEventChange'] && changes['fileItemEventChange'].currentValue) {

        } else if (changes['gobiiExtractFilterTypeEvent']
            && (changes['gobiiExtractFilterTypeEvent'].currentValue != null)
            && (changes['gobiiExtractFilterTypeEvent'].currentValue != undefined)) {


        }
    }


}
