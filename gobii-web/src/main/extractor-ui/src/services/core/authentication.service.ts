import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import { map } from 'rxjs/operators';
import { from } from 'rxjs';
import { KeycloakService } from 'keycloak-angular';


import {DtoHeaderAuth} from "../../model/dto-header-auth";
import {HttpValues} from "../../model/http-values";
import { KeycloakProfile } from 'keycloak-js';
import { trigger } from '@angular/animations';

@Injectable()
export class AuthenticationService {
    setProfile(profile: KeycloakProfile) {
        this.profile = profile;
        this.profileLoaded = true;
    }


    constructor(private _http: HttpClient,
                private _keycloakService: KeycloakService) {
    }

    private defaultUser: string = 'USER_READER';
    private defaultPassword: string = 'reader';
    private token: string = null;
    private userName: string = null;
    private userEmail: string = null;
    private profileLoaded: boolean = false;
    private _gobiiCropType: string = "";
    private profile: KeycloakProfile = null;
    //private authUrl: string = "gobii/v1/auth";


    public getToken() {
        return from(this._keycloakService.getToken());
    } // getToken()

    private setToken(token: string) {
        this.token = token;
    }

    public loadUserProfile() {
        //let scope$ = this;
        return from(this._keycloakService.loadUserProfile())
    }


    public getGobiiCropType(): string {
        //return "dev";
        return this._gobiiCropType;
    }

    public setGobiiCropType(gobiiCropType: string) {
         this._gobiiCropType = gobiiCropType;
    }

    // public getUserName(): string {
    //     return this.userName;
    // }
    public getUserName() {
        if (this.profileLoaded) return this.profile.username;

        return this._keycloakService.getUsername();
    }

    public getUserEmail() {
        return this.userEmail;
    }

    public isProfileLoaded() {
        return this.profileLoaded;
    }
    
    
    public logout() {
        return from(this._keycloakService.logout());
    }
}