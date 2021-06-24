import {Injectable} from '@angular/core';
import {Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot} from '@angular/router';
import {AuthenticationService} from "./authentication.service";
import { KeycloakAuthGuard, KeycloakService } from 'keycloak-angular';

// @Injectable()
// export class AuthGuard implements CanActivate {

//     constructor(private router: Router,
//                 private authenticationService: AuthenticationService) {
//     }

//     canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {

//         let returnVal: boolean = false;


//         returnVal = (this.authenticationService.getToken() != null);

//         // not logged in so redirect to login page with the return url
//         if (!returnVal) {
// //            this.router.navigate(['/login'], {queryParams: {returnUrl: state.url}});
//             this.router.navigate(['/login']);
//         }

//         return returnVal;
//     }
// }

@Injectable({
    providedIn: 'root'
})
export class AuthGuard extends KeycloakAuthGuard {
    constructor(
        protected readonly router: Router,
        protected readonly keycloak: KeycloakService
    ) {
        super(router, keycloak);
    }

    // public async isAccessAllowed(
    //     route: ActivatedRouteSnapshot,
    //     state: RouterStateSnapshot
    // ) {
    //     if (!this.authenticated) {
    //         await this.keycloak.login({
    //             redirectUri: window.location.origin + state.url
    //         });
    //     }

    //     return true;
    // }
    isAccessAllowed(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
        return new Promise(async (resolve, reject) => {    
            if (!this.authenticated) {
                this.keycloak.login({
                    redirectUri: window.location.origin + window.location.pathname  + state.url
                });
                return;
            }
            resolve(true);
        });
    }

}