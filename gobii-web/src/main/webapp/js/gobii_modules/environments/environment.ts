export const environmentSettings = {
  production: false,
  keycloak: {
    issuer: 'http://localhost:8181/auth', //TODO: will need to script this
    realm: 'Gobii',
    clientId: 'extractor-ui-gobii-dev'
  }
};
