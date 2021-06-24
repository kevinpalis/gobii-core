
<a name="genotypecallsresource">**Resource Description**</a>

Field | Type | Description
------|------|------------
callSetDbId | String | Id of the CallSet. 
callSetName | String | Name of the CallSet.
variantSetDbId | String | VariantSet to which genotype belongs to.
variantDbId | String | Id of the Variant,
variantName | String | Name of the variant.
genotype | Object | Object with key name "values" and array of genotypes as value. example, { "values" : ["GG"]}

<a name="genotypecallsresourceexample">**Resource Example**</a>

```

    {
        "variantSetDbId": "9",
        "callSetDbId": "45",
        "callSetName": "181GPUR_ICP_2_1_12",
        "variantDbId": "86",
        "variantName": "snpOS0287",
        "genotype": {
            "values": [
                "C/T"
            ]
        }
    }

```


