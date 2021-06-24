import {Component, EventEmitter, OnInit, ViewChild} from "@angular/core";
import {FileItem, FileUploader, FileUploaderOptions, Headers} from 'ng2-file-upload';
import {AuthenticationService} from "../services/core/authentication.service";
import {HeaderNames} from "../model/header-names";
import {HeaderStatusMessage} from "../model/dto-header-status-message";
import {FileName} from "../model/file_name";
import {GobiiFileItem} from "../model/gobii-file-item";
import {GobiiExtractFilterType} from "../model/type-extractor-filter";
import {ProcessType} from "../model/type-process";
import {ExtractorItemType} from "../model//type-extractor-item";
import {Store} from "@ngrx/store";
import * as fromRoot from '../store/reducers';
import {Observable} from "rxjs/Observable";
import {FileItemService} from "../services/core/file-item-service";
import * as historyAction from '../store/actions/history-action';
import {ViewIdGeneratorService} from "../services/core/view-id-generator-service";
import {TypeControl} from "../services/core/type-control";

const URL = 'gobii/v1/files/{gobiiJobId}/EXTRACTOR_INSTRUCTIONS?fileName=';

@Component({
    selector: 'uploader',
    inputs: ['gobiiExtractFilterType'],
    outputs: ['onUploaderError', 'onClickBrowse'],
    templateUrl: "uploader.component.html"
})

export class UploaderComponent implements OnInit {

    public typeControl:any = TypeControl;
    private onUploaderError: EventEmitter<HeaderStatusMessage> = new EventEmitter();
    private gobiiExtractFilterType: GobiiExtractFilterType = GobiiExtractFilterType.UNKNOWN;
    public uploadComplete = false;

    constructor(private _authenticationService: AuthenticationService,
                private store: Store<fromRoot.State>,
                private fileItemService: FileItemService,
                public viewIdGeneratorService: ViewIdGeneratorService) {


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

    @ViewChild('selectedFile') selectedFile: any;

    clearSelectedFile() {
        this.selectedFile.nativeElement.value = '';
    }

    private onClickBrowse: EventEmitter<any> = new EventEmitter();

    public handleClickBrowse(event: any) {

        this.onClickBrowse.emit(event);

    }

    public onFileSelected(e: any) {
        for(let fileItem of this.uploader.getNotUploadedItems()) {
            if(fileItem.file.type !== "text/plain") {
                this.onUploaderError.emit(new HeaderStatusMessage(
                    "Invalid Input.\nInput should be a text file with '.txt' extension.", null, null));
                this.uploader.clearQueue();
                this.clearSelectedFile();
                break;
            }
        }
    }

    ngOnInit(): any {

        let JobId$: any = this.store.select(fromRoot.getJobId);
        //Observable<GobiiFileItem> = this.store.select(fromRoot.getJobId);

        JobId$.subscribe(
            fileItemJobId => {

                let jobId: string = fileItemJobId.getItemId();
                let fileUploaderOptions: FileUploaderOptions = {}
                let url: string = URL.replace("{gobiiJobId}", jobId);
                let fileName = FileName.makeFileNameFromJobId(this.gobiiExtractFilterType, jobId);

                url += fileName;
                fileUploaderOptions.url = url;
                fileUploaderOptions.headers = [];
                fileUploaderOptions.removeAfterUpload = true;

                let authHeader: Headers = {name: '', value: ''};
                authHeader.name =  "Authorization";

                let token: string = "";//TODO: get back to this this._authenticationService.getToken().pipe(
                this._authenticationService.getToken().subscribe(
                    token => {
                        authHeader.value = 'Bearer ' + token;
                        fileUploaderOptions.headers.push(authHeader);
                    }
                );    

                // if (token) {

                //authHeader.value = token;

                //fileUploaderOptions.headers.push(authHeader);

                this.uploader = new FileUploader(fileUploaderOptions);

                this.uploader.onBeforeUploadItem = (fileItem: FileItem) => {

                    fileItem.file.name = fileName;

                };



                this.uploader.onCompleteItem = (item: any, response: any, status: any, headers: any) => {

                    if (status == 200) {
                        let listItemType: ExtractorItemType =
                            this.gobiiExtractFilterType === GobiiExtractFilterType.BY_MARKER ?
                                ExtractorItemType.MARKER_FILE : ExtractorItemType.SAMPLE_FILE;

                        this.fileItemService.loadFileItem(GobiiFileItem
                                .build(this.gobiiExtractFilterType, ProcessType.CREATE)
                                .setExtractorItemType(listItemType)
                                .setItemId(item.file.name)
                                .setItemName(item.file.name)
                                .setIsEphemeral(true),
                            true);

                    } else {

                        this.onUploaderError.emit(new HeaderStatusMessage(response, null, null));
                        this.uploader.clearQueue();
                        this.clearSelectedFile();

                    }

                };
                // } else {
                //     this.onUploaderError.emit(new HeaderStatusMessage("Unauthenticated", null, null));
                // }
            });


        this.store.select(fromRoot.getUploadFiles)
            .subscribe(fileFileItems => {


                if (fileFileItems && fileFileItems.length > 0) {
                    this.clearSelectedFile();
                    this.uploadComplete = false;
                }
            }, errorMessage => {
                this.store.dispatch(new historyAction.AddStatusMessageAction(errorMessage));

            });

        // this._fileModelTreeService
        //     .fileItemNotifications()
        //     .subscribe(eventedFileItem => {
        //             if (eventedFileItem.getProcessType() === ProcessType.DELETE) {
        //                 let currentItemType: ExtractorItemType = ExtractorItemType.UNKNOWN;
        //                 if (this.gobiiExtractFilterType === GobiiExtractFilterType.BY_SAMPLE) {
        //                     currentItemType = ExtractorItemType.SAMPLE_FILE;
        //                 } else if (this.gobiiExtractFilterType === GobiiExtractFilterType.BY_MARKER) {
        //                     currentItemType = ExtractorItemType.MARKER_FILE;
        //                 }
        //
        //                 if ((eventedFileItem.getGobiiEventOrigin() === GobiiUIEventOrigin.CRITERIA_TREE)
        //                     && (eventedFileItem.getExtractorItemType() === currentItemType )) {
        //                     this.clearSelectedFile();
        //                     this.uploadComplete = false;
        //                 }
        //             }
        //         },
        //         responseHeader => {
        //             this.onUploaderError.emit(responseHeader);
        //         });
    }

}
