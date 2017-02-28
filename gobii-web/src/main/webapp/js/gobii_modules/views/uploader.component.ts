import {Component, OnInit, SimpleChange, EventEmitter} from "@angular/core";
import {
    FileSelectDirective,
    FileDropDirective,
    FileUploader, FileUploaderOptions, Headers
} from 'ng2-file-upload';
import {AuthenticationService} from "../services/core/authentication.service";
import {HeaderNames} from "../model/header-names";
import {Header} from "../model/payload/header";

const URL = 'gobii/v1/uploadfile';

@Component({
    selector: 'uploader',
    // inputs: ['dataSetCheckBoxEvents'],
    // outputs: ['onItemUnChecked', 'onItemSelected'],
    template: `<style>
    .my-drop-zone { border: dotted 3px lightgray; }
    .nv-file-over { border: dotted 3px red; } /* Default class applied to drop zones on over */
    .another-file-over-class { border: dotted 3px green; }

    html, body { height: 100%; }
</style>

<div class="container">

    <div class="row">

        <div class="col-md-3">

            <!-- DROP ZONES AND MULTI-FILE SELECT, UNUSED FOR NOW ====================== 
            <h3>Select files</h3>

            <div ng2FileDrop
                 [ngClass]="{'nv-file-over': hasBaseDropZoneOver}"
                 (fileOver)="fileOverBase($event)"
                 [uploader]="uploader"
                 class="well my-drop-zone">
                Base drop zone
            </div>

            <div ng2FileDrop
                 [ngClass]="{'another-file-over-class': hasAnotherDropZoneOver}"
                 (fileOver)="fileOverAnother($event)"
                 [uploader]="uploader"
                 class="well my-drop-zone">
                Another drop zone
            </div>

            Multiple
            <input type="file" ng2FileSelect [uploader]="uploader" multiple  /><br/>
            ================================================================================ -->

            
            <input type="file" ng2FileSelect [uploader]="uploader" />
            <!--  IF YOU REINSTATE THE QUEUES BELOW THIS BUTTON WILL BE SUPERFLUOUS -->
            <button type="button" class="btn btn-success btn-s"
                        (click)="uploader.uploadAll()" [disabled]="!uploader.getNotUploadedItems().length">
                    <span class="glyphicon glyphicon-upload"></span> Upload
            </button>

        </div>

        <div class="col-md-9" style="margin-bottom: 40px">


            <!-- UPLOAD QUEUE UNUSED FOR NOW =========================================================
            <h3>Upload queue</h3>
            <p>Queue length: {{ uploader?.queue?.length }}</p>

            <table class="table">
                <thead>
                <tr>
                    <th width="50%">Name</th>
                    <th>Size</th>
                    <th>Progress</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <tr *ngFor="let item of uploader.queue">
                    <td><strong>{{ item?.file?.name }}</strong></td>
                    <td *ngIf="uploader.isHTML5" nowrap>{{ item?.file?.size/1024/1024 | number:'.2' }} MB</td>
                    <td *ngIf="uploader.isHTML5">
                        <div class="progress" style="margin-bottom: 0;">
                            <div class="progress-bar" role="progressbar" [ngStyle]="{ 'width': item.progress + '%' }"></div>
                        </div>
                    </td>
                    <td class="text-center">
                        <span *ngIf="item.isSuccess"><i class="glyphicon glyphicon-ok"></i></span>
                        <span *ngIf="item.isCancel"><i class="glyphicon glyphicon-ban-circle"></i></span>
                        <span *ngIf="item.isError"><i class="glyphicon glyphicon-remove"></i></span>
                    </td>
                    <td nowrap>
                        <button type="button" class="btn btn-success btn-xs"
                                (click)="item.upload()" [disabled]="item.isReady || item.isUploading || item.isSuccess">
                            <span class="glyphicon glyphicon-upload"></span> Upload
                        </button>
                        <button type="button" class="btn btn-warning btn-xs"
                                (click)="item.cancel()" [disabled]="!item.isUploading">
                            <span class="glyphicon glyphicon-ban-circle"></span> Cancel
                        </button>
                        <button type="button" class="btn btn-danger btn-xs"
                                (click)="item.remove()">
                            <span class="glyphicon glyphicon-trash"></span> Remove
                        </button>
                    </td>
                </tr>
                </tbody>
            </table>

            <div>
                <div>
                    Queue progress:
                    <div class="progress" style="">
                        <div class="progress-bar" role="progressbar" [ngStyle]="{ 'width': uploader.progress + '%' }"></div>
                    </div>
                </div>
                <button type="button" class="btn btn-success btn-s"
                        (click)="uploader.uploadAll()" [disabled]="!uploader.getNotUploadedItems().length">
                    <span class="glyphicon glyphicon-upload"></span> Upload all
                </button>
                <button type="button" class="btn btn-warning btn-s"
                        (click)="uploader.cancelAll()" [disabled]="!uploader.isUploading">
                    <span class="glyphicon glyphicon-ban-circle"></span> Cancel all
                </button>
                <button type="button" class="btn btn-danger btn-s"
                        (click)="uploader.clearQueue()" [disabled]="!uploader.queue.length">
                    <span class="glyphicon glyphicon-trash"></span> Remove all
                </button>
            </div>
            == UPLOAD QUEUE UNUSED FOR NOW ========================================================= -->


        </div>

    </div>

</div>`

})

export class UploaderComponent implements OnInit {


    constructor(private _authenticationService: AuthenticationService) {

        let fileUploaderOptions: FileUploaderOptions = {}
        fileUploaderOptions.url = URL;
        fileUploaderOptions.headers = [];

        let authHeader: Headers = {name: '', value: ''};
        authHeader.name = HeaderNames.headerToken;

        _authenticationService
            .getToken()
            .subscribe(token => {
                authHeader.value = token;
            });

        fileUploaderOptions.headers.push(authHeader);

        this.uploader = new FileUploader(fileUploaderOptions);

    } // ctor


    public uploader: FileUploader;

    public hasBaseDropZoneOver: boolean = false;
    public hasAnotherDropZoneOver: boolean = false;

    public fileOverBase(e: any): void {
        this.hasBaseDropZoneOver = e;
    }

    public fileOverAnother(e: any): void {
        this.hasAnotherDropZoneOver = e;
    }


    ngOnInit(): any {
        return null;
    }


}