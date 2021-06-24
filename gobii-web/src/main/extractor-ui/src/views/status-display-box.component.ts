import {Component, OnInit, SimpleChange, OnChanges, Input} from "@angular/core";
import {GobiiDataSetExtract} from "../model/extractor-instructions/data-set-extract";
import {Observable} from "rxjs/Observable";


@Component({
    selector: 'status-display',
    inputs: [],
    //directives: [RADIO_GROUP_DIRECTIVES]
    template: `<div style="overflow:auto; height: 240px; border: 1px solid #336699; padding-left: 5px;">
                    <div *ngFor="let message of messages$ | async ">{{message}}
                    <hr style="height:1px;border:none;color:#333;background-color:#333;">
                    </div>
                </div>` // end template

})

export class StatusDisplayComponent implements OnInit, OnChanges {


    @Input()
    public messages$:Observable<string[]>;
    constructor() {
    } // ctor

    // private handleClearMessages() {
    //     this.messages$ = [];
    // }

    ngOnInit():any {
        return null;
    }

    ngOnChanges(changes:{[propName:string]:SimpleChange}) {
    }

}
