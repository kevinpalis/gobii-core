// import {Component, OnInit} from '@angular/core';
// import {Router, ActivatedRoute} from '@angular/router';
// import {AuthenticationService} from "../services/core/authentication.service";
// import {ServerConfig} from "../model/server-config";
// import {DtoRequestService} from "../services/core/dto-request.service";
// import {DtoRequestItemServerConfigs} from "../services/app/dto-request-item-serverconfigs";
// import {LocationStrategy} from "@angular/common";
// import {ViewIdGeneratorService} from "../services/core/view-id-generator-service";
// import {TypeControl} from "../services/core/type-control";

// @Component({
// //    moduleId: module.id,
//     template: `
//         <BR>
//         <BR>
//         <BR>
//         <BR>
//         <BR>
//         <div class="container">
//             <div class="col-md-6 col-md-offset-3">
//                 <div *ngIf="confidentialityNotice">
//                     <h3 class="text-warning">{{confidentialityNotice}}</h3>
//                     <p-checkbox label="Agree To Terms"
//                                 [(ngModel)]="userAgreed" 
//                                 binary="true"
//                                 (onChange)="onTermAgreeCheck()"
//                                 [id]="viewIdGeneratorService.makeStandardId(typeControl.LOGIN_AGREE_TO_TERMS_CHECKBOX)"
//                     ></p-checkbox>
//                 </div>
//                 <h2>GDM Login</h2>
//                 <form name="form" (ngSubmit)="f.form.valid && login()" #f="ngForm" novalidate>
//                     <div class="form-group" [ngClass]="{ 'has-error': f.submitted && !username.valid }">
//                         <label for="username">Username</label>
//                         <input [disabled]="inputDisabled" 
//                                type="text" 
//                                class="form-control" 
//                                name="username" 
//                                [(ngModel)]="model.username"
//                                #username="ngModel" required
//                                [id]="viewIdGeneratorService.makeStandardId(typeControl.LOGIN_USER_NAME_INPUT)"/>
//                         <div *ngIf="f.submitted && !username.valid" class="help-block">Username is required</div>
//                     </div>
//                     <div class="form-group" [ngClass]="{ 'has-error': f.submitted && !password.valid }">
//                         <label for="password">Password</label>
//                         <input [disabled]="inputDisabled" 
//                                type="password" 
//                                class="form-control" 
//                                name="password" 
//                                [(ngModel)]="model.password"
//                                #password="ngModel" 
//                                required
//                                [id]="viewIdGeneratorService.makeStandardId(typeControl.LOGIN_PASSWORD_INPUT)"/>
//                         <div *ngIf="f.submitted && !password.valid" class="help-block">Password is required</div>
//                     </div>
//                     <div class="form-group">
//                         <button 
//                                 [disabled]="inputDisabled" 
//                                 class="btn btn-primary"
//                                 [id]="viewIdGeneratorService.makeStandardId(typeControl.LOGIN_SUBMIT_BUTTON)">Login</button>
//                         <img *ngIf="loading"
//                              src="data:image/gif;base64,R0lGODlhEAAQAPIAAP///wAAAMLCwkJCQgAAAGJiYoKCgpKSkiH/C05FVFNDQVBFMi4wAwEAAAAh/hpDcmVhdGVkIHdpdGggYWpheGxvYWQuaW5mbwAh+QQJCgAAACwAAAAAEAAQAAADMwi63P4wyklrE2MIOggZnAdOmGYJRbExwroUmcG2LmDEwnHQLVsYOd2mBzkYDAdKa+dIAAAh+QQJCgAAACwAAAAAEAAQAAADNAi63P5OjCEgG4QMu7DmikRxQlFUYDEZIGBMRVsaqHwctXXf7WEYB4Ag1xjihkMZsiUkKhIAIfkECQoAAAAsAAAAABAAEAAAAzYIujIjK8pByJDMlFYvBoVjHA70GU7xSUJhmKtwHPAKzLO9HMaoKwJZ7Rf8AYPDDzKpZBqfvwQAIfkECQoAAAAsAAAAABAAEAAAAzMIumIlK8oyhpHsnFZfhYumCYUhDAQxRIdhHBGqRoKw0R8DYlJd8z0fMDgsGo/IpHI5TAAAIfkECQoAAAAsAAAAABAAEAAAAzIIunInK0rnZBTwGPNMgQwmdsNgXGJUlIWEuR5oWUIpz8pAEAMe6TwfwyYsGo/IpFKSAAAh+QQJCgAAACwAAAAAEAAQAAADMwi6IMKQORfjdOe82p4wGccc4CEuQradylesojEMBgsUc2G7sDX3lQGBMLAJibufbSlKAAAh+QQJCgAAACwAAAAAEAAQAAADMgi63P7wCRHZnFVdmgHu2nFwlWCI3WGc3TSWhUFGxTAUkGCbtgENBMJAEJsxgMLWzpEAACH5BAkKAAAALAAAAAAQABAAAAMyCLrc/jDKSatlQtScKdceCAjDII7HcQ4EMTCpyrCuUBjCYRgHVtqlAiB1YhiCnlsRkAAAOwAAAAAAAAAAAA=="/>
//                     </div>
//                 </form>
//                 <span>{{message}}</span>
//             </div>
//         </div>`
// })


// // this component and the entire login mechanism (AuthGuard, etc.) are borrowed form
// // http://jasonwatmore.com/post/2016/09/29/angular-2-user-registration-and-login-example-tutorial
// export class LoginComponent implements OnInit {
//     model: any = {};
//     loading = false;
//     returnUrl: string;
//     message: string;
//     confidentialityNotice: string;
//     userAgreed:boolean = false;
//     inputDisabled:boolean = true;
//     public typeControl:any = TypeControl;

//     constructor(private route: ActivatedRoute,
//                 private router: Router,
//                 private locationStrategy: LocationStrategy,
//                 private authenticationService: AuthenticationService,
//                 private dtoRequestServiceServerConfigs: DtoRequestService<ServerConfig[]>,
//                 public viewIdGeneratorService:ViewIdGeneratorService) {
//     }

//     ngOnInit() {
//         // reset login status
//         //this.authenticationService.logout();

//         // get return url from route parameters or default to '/'
//         this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '';


//         this.dtoRequestServiceServerConfigs
//             .get(new DtoRequestItemServerConfigs(), false)
//             .subscribe(serverConfigs => {

//                     let foo: string = "foo";

//                     if (serverConfigs && ( serverConfigs.length > 0 )) {

//                         let path: string = this.locationStrategy.path();
//                         let cropServerConfig: ServerConfig =
//                             serverConfigs
//                                 .find(c => {
//                                         return path.indexOf(c.contextRoot) > -1
//                                     }
//                                 );

//                         if (cropServerConfig) {
//                             this.confidentialityNotice = cropServerConfig.confidentialityNotice;
//                             if(this.confidentialityNotice) {
//                                 this.inputDisabled = true;
//                             }  else {
//                                 this.inputDisabled = false;
//                             }
//                         }

//                     }
//                 }
//             )
//     }

//     public onTermAgreeCheck() {

//         this.inputDisabled = !this.userAgreed;
//     }

//     login() {
//         this.loading = true;
// //        this.router.navigate(['project']);
//         this.loading = false;
//         this.authenticationService.authenticate(this.model.username.trim(), this.model.password.trim())
//             .subscribe(
//                 dtoHeaderAuth => {

//                     if (dtoHeaderAuth.getToken() != null) {
// //                        this.router.navigate([this.returnUrl]);
//                         this.router.navigate(['']);
//                     }
//                 },
//                 error => {
//                     this.message = error;
//                     this.loading = false;
//                 });

//     }
// }
