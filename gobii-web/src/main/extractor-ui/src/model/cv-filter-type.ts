export enum CvFilterType {UNKNOWN,
ANALYSIS_TYPE,
DATASET_TYPE,
JOB_STATUS}
;

export class CvFilters {


    private static cvValues: Map<CvFilterType,string> = null;

    // These are the cvgroup values for the terms that are to be retrieved
    public static get(cvFilterType:CvFilterType): string {

        if ( this.cvValues === null ) {
            this.cvValues = new Map<CvFilterType,string>();
            this.cvValues[CvFilterType.ANALYSIS_TYPE] = "analysis_type";
            this.cvValues[CvFilterType.DATASET_TYPE] = "dataset_type";
            this.cvValues[CvFilterType.JOB_STATUS] = "job_status";
        }

        return this.cvValues[cvFilterType];
    }

}

