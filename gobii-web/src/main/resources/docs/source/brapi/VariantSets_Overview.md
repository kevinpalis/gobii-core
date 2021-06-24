
VariantSet corresponds to Dataset in GDM system.
A dataset belongs to an experiment (study in BrAPI) and describes the suite of analyses applied to the vendor-delivered data to generate the dataset, as well as the dataset type.

<a name="variantsetresource">**Resource Description**</a>

Field | Type | Description
------|------|------------
variantSetDbId | Integer | Id of the VariantSet. Corresponds to dataset Id.
variantSetName | String | The name of the dataset. VariantSet name is unique under a study.
studyDbId | Integer | Study under which the VariantSet is created. Corresponds to the experiment Id.
analyses | Array | List of Analysis Resource. Lists all the analyses associated with the dataset.
availableFormats | Array | List of [File Format Objects](#fileformatobject).
referenceSetDbId | String | The ID of the reference set that describes the sequences used by the variants in this set.
variantCount | Integer | Number of Variants(Markers or SNPs) in corresponding VariantSet
callSetCount | Integer | Number of CallSets (DnaRuns) in a corresponding VariantSet
additionalInfo | Object | [Additional Information](#variantsetadditionalinfo).
createdBy | Integer | Contact Id of the user who created the VariantSet.
created | String | Created date string in UTC
modifiedBy | Integer | Contact Id of the user who modified the VariantSet.
modified | Integer | Modified date string in UTC

<a name="variantsetresourceexample">**Resource Example**</a>

```json
    
    {
        "createdBy": "1",
        "variantSetDbId": "13",
        "variantSetName": "foo dataset",
        "studyDbId": "11",
        "studyName": "bar study",
        "availableFormats": [
            {
                "dataFormat": "tabular",
                "fileFormat": "text/csv",
                "fileURL": "/variantsets/13/calls/download"
            }
        ],
        "analyses": [
            {
                "analysisDbId": "2",
                "analysisName": "calling",
                "description": "",
                "created": "2019-12-19T05:00:00"
            }
        ],
        "callSetCount": 0,
        "variantCount": 100,
        "additionalInfo": {
            "extractReady": true
        },
        "created": "2020-01-08T05:00:00"
    } 

```

<a name="fileformatobject">**File Format Object**</a>

Field | Type | Description
------|------|------------
dataFormat | String | Structure of the data within a file (ie DartSeq, VCF, Hapmap, tabular, etc)
fileFormat | String | MIME type of the file (ie text/csv, application/excel, application/zip).
fileUrl | String | Relative URL to download variantset.


<a name="fileformatobjectexample">**Resource Example**</a>

```json
    {
        "fileUrl": "/variantsets/13/calls/download",
        "fileFormat": "text/csv",
        "dataFormat": "tabular"
    }
```
<a name="variantsetadditionalinfo">**Additional Info Object**</a>

Field | Type | Description
------|------|------------
extractReady | Boolean | Tells whether the dataset is extract ready or not.

<a name="variantsetadditionalinfoexample">**Resource Example**</a>

```json
    {
        "extractReady" : true
    }
```

