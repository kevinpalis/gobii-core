export class ServerConfig {
    
    constructor(public crop:string,
                public domain:string,
                public contextRoot:string,
                public port:number,
                public confidentialityNotice:string) {
        
        this.crop = crop;
        this.domain = domain;
        this.port = port;
        this.confidentialityNotice = confidentialityNotice;
    }
}