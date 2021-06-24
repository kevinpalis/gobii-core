
Brapi Samples corresponds to Dna Samples in GDM system.

<a name="samplesresource">**Resource Description**</a>

Field | Type | Description
------|------|------------
sampleDbId | String | Id of the DnaSample in GDM system.
sampleName | String | Name of the DnaSample.
germplasmDbId | String | Germplasm to which dna sample of the dna run belongs to.
germplasmName | String | Name of the germplasm to which dna sample of dna run belongs to.
observationUnitDbId | String | Corresponds to germplasm external code in GDM.
germplasmGroupDbId | String | Corresponds to projectId in GDM.
well | String | Sample location in plate.
row | String | Row identifier for sample location in plate
column | String | Column identifier for sample location in plate.
additionalInfo | Object | Object for other miscellaneous fields.

<a name="samplesresourceexample">**Resource Example**</a>

```

    {
        "germplasmDbId": "6",
        "observationUnitDbId": "foo germplasm",
        "sampleType": "d*a",
        "sampleDbId": "7",
        "sampleName": "foo sample",
        "samplePUI": "87a26187-2a89-4dbb-87a",
        "well": "1",
        "row": "a",
        "column": "4",
        "sampleGroupDbId": "3" 
    }

```


