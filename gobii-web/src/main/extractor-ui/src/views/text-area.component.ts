import {Component, EventEmitter} from "@angular/core";

@Component({
    selector: 'text-area',
    outputs: ['onTextboxDataComplete','onTextboxClicked'],
    template: `
        <textarea ref-textarea 
        [(ngModel)]="textValue" rows="4" style="width: 100%;"
        (click)="handleTextboxClicked($event)"></textarea><br/>
        <button class="btn btn-success" (click)="handleTextboxDataComplete(textarea.value)">Add To Extract</button>
        
        <button class="btn btn-primary" (click)="textValue=''">Clear</button>
        
         <!--<h2>Log <button (click)="log=''">Clear</button></h2>-->
        <!--<pre>{{log}}</pre>-->
`
})
export class TextAreaComponent {

    public textValue;
    public log: string ='';

    private logText(value: string): void {
        this.log += `Text changed to '${value}'\n`
    }

    private onTextboxClicked:EventEmitter<any> = new EventEmitter();
    public handleTextboxClicked(arg) {
        this.onTextboxClicked.emit(arg);
    }

    private onTextboxDataComplete:EventEmitter<string[]> = new EventEmitter();
    public handleTextboxDataComplete(arg) {
        let items:string[] = arg.split("\n");
        this.onTextboxDataComplete.emit(items);
        this.textValue = '';
    }

}