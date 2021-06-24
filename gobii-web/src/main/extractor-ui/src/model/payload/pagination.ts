

export class Pagination {

    public constructor(public currentPage: number,
                       public pageSize: number,
                       public pagedQueryId: string,
                       public queryTime: string,
                       public totalPages: number) {
    }

    public static fromJSON(json: any): Pagination {


        let currentPage: number = json.currentPage;
        let pageSize: number = json.pageSize;
        let pagedQueryId: string = json.pagedQueryId;
        let queryTime: string = json.queryTime;
        let totalPages: number = json.totalPages;


        return new Pagination(
            currentPage,
            pageSize,
            pagedQueryId,
            queryTime,
            totalPages
        ); // new

    } // fromJson()
}
