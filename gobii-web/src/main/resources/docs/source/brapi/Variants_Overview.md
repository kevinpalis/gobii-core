
Brapi Variant corresponds to Marker in GDM system.

<a name="variantresource">**Resource Description**</a>

Field | Type | Description
------|------|------------
variantDbId | String | Id of the Variant. Corresponds to Marker.
variantNames | Array(String) | Array of VariantNames.
variantSetDbId | Array(String) | List of dataset ids to which the dnarun belongs to.
variantType | String | Type of variant. Marker or SNP or Indel ....
alternateBases | Array(String) | List of alternate alleles for the variant.
referenceName | String | Name of the reference used for genotyping. example, MSUv7
referenceBases | String | Reference base for the variant.
platformName | String | Genotyping platform used.
additionalInfo | Object | Object for other miscellaneous fields.
created | Date String | Created date string in UTC
updated | Date String | Modified date string in UTC

<a name="variantresourceexample">**Resource Example**</a>

```

    {
        "variantDbId": "7",
        "variantNames": [
            "foo"
        ],
        "variantSetDbId": [
            "2",
            "4"
        ],
        "variantType": "Marker",
        "platformName": "KASP" 
    }

```


