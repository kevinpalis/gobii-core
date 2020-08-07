import {NgModule, APP_INITIALIZER, NO_ERRORS_SCHEMA, CUSTOM_ELEMENTS_SCHEMA} from "@angular/core";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
//import {HttpModule} from "@angular/http";
import { HttpClientModule } from  '@angular/common/http';
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {ExportFormatComponent} from "../views/export-format.component";
import {StatusDisplayComponent} from "../views/status-display-box.component";
import {CheckListBoxComponent} from "../views/checklist-box.component";
import {SampleMarkerBoxComponent} from "../views/sample-marker-box.component";
import {FileUploadModule} from "ng2-file-upload";
import {ExtractorRoot} from "./app.extractorroot";
import {DtoRequestService} from "../services/core/dto-request.service";
import {AuthenticationService} from "../services/core/authentication.service";
import {TextAreaComponent} from "../views/text-area.component";
import {UploaderComponent} from "../views/uploader.component";
import {SampleListTypeComponent} from "../views/sample-list-type.component";
import {
    CheckboxModule, SharedModule, TreeModule, 
    AccordionModule, ListboxModule, FieldsetModule, TabViewModule, TooltipModule, RadioButtonModule,
} from 'primeng/primeng';
import { TableModule } from 'primeng/table';
import { PanelModule } from 'primeng/panel';

import { OverlayPanelModule} from 'primeng/overlaypanel';
import { DialogModule } from 'primeng/dialog';

import {ButtonModule} from 'primeng/button';
import {StatusDisplayTreeComponent} from "../views/status-display-tree.component";
import {NameIdListBoxComponent} from "../views/name-id-list-box.component";
import {NameIdService} from "../services/core/name-id-service";
import {AppComponent} from "./app.component";
import {LoginComponent} from "../views/login.component";
import {routing} from "./app.routing";
import {APP_BASE_HREF} from "@angular/common";
import {AuthGuard} from "../services/core/auth.guard";
import {SearchCriteriaBySamplesComponent} from "./page-by-samples.component";
import {StoreModule} from '@ngrx/store';
import {reducers} from '../store/reducers';
import {StoreDevtoolsModule} from "@ngrx/store-devtools";
import {EffectsModule} from "@ngrx/effects";
import {TreeEffects} from "../store/effects/tree-effects";
import {TreeStructureService} from "../services/core/tree-structure-service";
import {FileItemEffects} from "../store/effects/file-item-effects";
import {FileItemService} from "../services/core/file-item-service";
import {InstructionSubmissionService} from "../services/core/instruction-submission-service";
import {DatasetDatatableComponent} from "../views/dataset-datatable.component";
import {FilterParamsColl} from "../services/core/filter-params-coll";
import {ViewIdGeneratorService} from "../services/core/view-id-generator-service";

import { KeycloakService, KeycloakAngularModule} from 'keycloak-angular';
import { initializer } from './app.init';


@NgModule({
    imports: [BrowserModule,
        HttpClientModule,
        FormsModule,
        ReactiveFormsModule,
        TreeModule,
        ButtonModule,
        CheckboxModule,
        SharedModule,
        TableModule,
        PanelModule,
        OverlayPanelModule,
        AccordionModule,
        ListboxModule,
        FieldsetModule,
        TabViewModule,
        DialogModule,
        TooltipModule,
        RadioButtonModule,
        FileUploadModule,
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
        LoginComponent,
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
        DatasetDatatableComponent],
    providers: [
        {
            provide: APP_INITIALIZER,
            useFactory: initializer,
            deps: [ KeycloakService ],
            multi: true
        },
        AuthGuard,
        AuthenticationService,
        DtoRequestService,
        NameIdService,
        TreeStructureService,
        FileItemService,
        ViewIdGeneratorService,
        InstructionSubmissionService,
        FilterParamsColl,
        {provide: APP_BASE_HREF, useValue: './'}],
    
    bootstrap: [AppComponent],
    schemas: [
        CUSTOM_ELEMENTS_SCHEMA,
        NO_ERRORS_SCHEMA
      ]
})

export class AppModule {
}
