import {KeycloakService} from 'keycloak-angular';
import { HttpClient } from '@angular/common/http';

export function initializer(keycloak: KeycloakService, http: HttpClient): () => Promise<any> {
    return (): Promise<any> => {
        return new Promise(async (resolve, reject) => {
            try {
                let loc = window.location.pathname;
                let keycloakConfig = {
                    url: '',
                    realm: '',
                    clientId: ''
                }
                //call /config/extractor here
                await http.get("config/extractor").toPromise()
                .then(
                    (data: any) => {
                        keycloakConfig.url = data.result.authUrl;
                        keycloakConfig.realm = data.result.realm;
                        keycloakConfig.clientId = data.result.client;
                    },

                    (failure) => {
                        reject(failure);
                    }
                )


                await keycloak.init({
                    config: keycloakConfig,
                    loadUserProfileAtStartUp: false,
                    initOptions: {
                        onLoad: 'check-sso',
                        silentCheckSsoRedirectUri:
                            window.location.origin + loc  + '/assets/silent-check-sso.html',
                        checkLoginIframe: false
                      },
                    bearerExcludedUrls: [],
                });
                resolve();
            } catch (error) {
                reject(error);
            }
        });
    };
}