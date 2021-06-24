import { APP_BASE_HREF } from "@angular/common";
//import {HttpModule} from "@angular/http";
import { HttpClientModule, HTTP_INTERCEPTORS, HttpClient } from '@angular/common/http';
import { APP_INITIALIZER, CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA } from "@angular/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { EffectsModule } from "@ngrx/effects";
import { StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from "@ngrx/store-devtools";
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
//import { FileUploadModule } from "ng2-file-upload";
import { FileUploadModule } from 'primeng/fileupload';
import { AccordionModule } from 'primeng/accordion';
import { SharedModule } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { DropdownModule } from 'primeng/dropdown';
import { DialogModule } from 'primeng/dialog';
import { FieldsetModule } from 'primeng/fieldset';
import { ListboxModule } from 'primeng/listbox';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { PanelModule } from 'primeng/panel';
import { RadioButtonModule } from 'primeng/radiobutton';
import { TableModule } from 'primeng/table';
import { TabViewModule } from 'primeng/tabview';
import { TooltipModule } from 'primeng/tooltip';
import { TreeModule } from 'primeng/tree';
import { AuthGuard } from "../services/core/auth.guard";
import { AuthenticationService } from "../services/core/authentication.service";
import { DtoRequestService } from "../services/core/dto-request.service";
import { FileItemService } from "../services/core/file-item-service";
import { FilterParamsColl } from "../services/core/filter-params-coll";
import { InstructionSubmissionService } from "../services/core/instruction-submission-service";
import { NameIdService } from "../services/core/name-id-service";
import { TreeStructureService } from "../services/core/tree-structure-service";
import { ViewIdGeneratorService } from "../services/core/view-id-generator-service";
import { FileItemEffects } from "../store/effects/file-item-effects";
import { TreeEffects } from "../store/effects/tree-effects";
import { reducers } from '../store/reducers';
import { CheckListBoxComponent } from "../views/checklist-box.component";
import { DatasetDatatableComponent } from "../views/dataset-datatable.component";
import { ExportFormatComponent } from "../views/export-format.component";
import { NameIdListBoxComponent } from "../views/name-id-list-box.component";
import { SampleListTypeComponent } from "../views/sample-list-type.component";
import { SampleMarkerBoxComponent } from "../views/sample-marker-box.component";
import { StatusDisplayComponent } from "../views/status-display-box.component";
import { StatusDisplayTreeComponent } from "../views/status-display-tree.component";
import { TextAreaComponent } from "../views/text-area.component";
import { UploaderComponent } from "../views/uploader.component";
import { AppComponent } from "./app.component";
import { ExtractorRoot } from "./app.extractorroot";
import { initializer } from './app.init';
//import {LoginComponent} from "../views/login.component";
import { routing } from "./app.routing";
import { SearchCriteriaBySamplesComponent } from "./page-by-samples.component";
import { DtoRequestService2 } from '../services/core/dto-request.service2';
import { SpinnerOverlayComponent } from 'src/views/spinner-overlay.component';
import { SpinnerOverlayService } from 'src/services/core/spinner-overlay.service';
import { SpinnerInterceptor } from 'src/services/core/spinner-interceptor';
import { OverlayModule } from '@angular/cdk/overlay';
import { ProgressSpinnerModule } from 'primeng/progressspinner';





@NgModule({
    imports: [BrowserModule,
        HttpClientModule,
        FormsModule,
        ReactiveFormsModule,
        TreeModule,
        ButtonModule,
        CheckboxModule,
        DropdownModule,
        SharedModule,
        TableModule,
        PanelModule,
        OverlayModule,
        OverlayPanelModule,
        AccordionModule,
        ListboxModule,
        FieldsetModule,
        TabViewModule,
        DialogModule,
        TooltipModule,
        RadioButtonModule,
        FileUploadModule,
        ProgressSpinnerModule,
        routing,
        BrowserAnimationsModule,
        StoreModule.forRoot(reducers),
        EffectsModule.forRoot([TreeEffects,FileItemEffects]),
        StoreDevtoolsModule.instrument({
            maxAge: 25 //  Retains last 25 states
        }),
        KeycloakAngularModule
    ],
    declarations: [
        AppComponent,
        ExtractorRoot,
        //LoginComponent,
        ExportFormatComponent,
        StatusDisplayComponent,
        CheckListBoxComponent,
        SampleMarkerBoxComponent,
        TextAreaComponent,
        UploaderComponent,
        SampleListTypeComponent,
        NameIdListBoxComponent,
        StatusDisplayTreeComponent,
        SearchCriteriaBySamplesComponent,
        DatasetDatatableComponent,
        SpinnerOverlayComponent],
    providers: [
        {
            provide: APP_INITIALIZER,
            useFactory: initializer,
            deps: [ KeycloakService, HttpClient ],
            multi: true
        },
        AuthGuard,
        AuthenticationService,
        DtoRequestService,
        DtoRequestService2,
        NameIdService,
        TreeStructureService,
        FileItemService,
        ViewIdGeneratorService,
        InstructionSubmissionService,
        FilterParamsColl,
        SpinnerOverlayService,
        {provide: APP_BASE_HREF, useValue: './'},
        {provide: HTTP_INTERCEPTORS, useClass: SpinnerInterceptor, multi: true}
    ],
    entryComponents: [
        SpinnerOverlayComponent
    ],
    
    bootstrap: [AppComponent],
    schemas: [
        CUSTOM_ELEMENTS_SCHEMA,
        NO_ERRORS_SCHEMA
    ]
})

export class AppModule {
}
