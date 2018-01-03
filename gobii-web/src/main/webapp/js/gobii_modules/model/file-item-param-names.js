System.register([], function (exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var FilterParamNames;
    return {
        setters: [],
        execute: function () {
            FilterParamNames = (function () {
                // The problem with using an enum is that in the debugger
                // you cannot readily see what the enum type value means
                // Knowing the nameidfilterparam type on debugger inspection is
                // particularly important because of the generic way in which nameids
                // are treated in the components and services
                function FilterParamNames() {
                }
                FilterParamNames.UNKNOWN = "UNKNOWN";
                FilterParamNames.CONTACT_PI = "Contact-PI";
                FilterParamNames.EXPERIMENTS = "Experiments";
                FilterParamNames.EXPERIMENTS_BY_PROJECT = "Experiments-by-project";
                FilterParamNames.CV_DATATYPE = "Cv-DataType";
                FilterParamNames.CV_JOB_STATUS = "Cv-JobStatus";
                FilterParamNames.MAPSETS = "Mapsets";
                FilterParamNames.PLATFORMS = "Platforms";
                FilterParamNames.DATASETS_BY_EXPERIMENT = "Datasets-by-experiment";
                FilterParamNames.DATASET_LIST = "Dataset-List";
                FilterParamNames.DATASET_BY_DATASET_ID = "Dataset-List-By-DatasetId";
                FilterParamNames.ANALYSES_BY_DATASET_ID = "Analysis-List-By-DatasetId";
                FilterParamNames.PROJECTS_BY_CONTACT = "Projects-by-contact";
                FilterParamNames.PROJECTS = "Project";
                FilterParamNames.MARKER_GROUPS = "Marker Groups";
                return FilterParamNames;
            }());
            exports_1("FilterParamNames", FilterParamNames);
        }
    };
});
//# sourceMappingURL=file-item-param-names.js.map