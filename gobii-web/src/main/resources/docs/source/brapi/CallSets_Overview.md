
Brapi CallSets corresponds to Dna Run in GDM system.


<a name="callsetresource">**Resource Description**</a>

Field | Type | Description
------|------|------------
callSetDbId | String | Id of the CallSet. Corresponds to Dna Run.
callSetName | String | Name of the Dna Run
studyDbId | String | Genotyping Experiment under dna run belongs to.
sampleDbId | String | Dna Sample for the Genotype calls were generated
sampleName | String | Dna Sample Name  
variantSetIds | Array | List of datasets to which the dnarun belongs to.
germplasmDbId | String | Germplasm to which dna sample of the dna run belongs to.
germplasmName | String | Name of the germplasm to which dna sample of dna run belongs to.
additionalInfo | Object | [Additional Information](#callsetadditionalinfo).
createdBy | Integer | Contact Id of the user who created the VariantSet.
created | Date String | Created date string in UTC
updatedBy | Integer | Contact Id of the user who modified the VariantSet.
updated | Date String | Modified date string in UTC

<a name="callsetresourceexample">**Resource Example**</a>

```

    {
        "callSetDbId": "6",
        "callSetName": "call foo",
        "studyDbId": "2",
        "sampleDbId": "4",
        "sampleName": "bar sample",
        "variantSetIds": [
            2
        ],
        "germplasmDbId": "6",
        "germplasmName": "foo germplasm",
        "additionalInfo": {}
    }

```

<a name="callsetadditionalinfo">**Additional Info Object**</a>

Other than below fields, Realtime user defined fields could also be part the CallSet additional info.
It could vary from one CG Center Implementation to other.

Field | Type | Description
------|------|------------
sample_group | String | Sample Group eg MABCTHC_cycle1
sample_group_cycle | String | Cycle eg backcrossing cycle for a sample group
sample_type | String | TType of tissue sampled eg leaf, seed, bulk seed, bulk plant
sample_parent_prop | String | Type of parent for a population eg donor, recurrent etc
ref_sample | String | Standard sample against which all other germplasm is compared to for this GID/MGID. Gold standard or Reference line.
seed_source_id | String | Seed source Id.
germplasm_subsp | String | Germplasm sub-species eg indica, japonica, for rice; Dent, Flint for maize
germplasm_heterotic_group | String | Heterotic groups within species eg NSS, SSS, A, B for maize
par1 | String | Parent 1 of the germplasm name.
par2 | String | Parent 2 of the germplasm name.
par3 | String | Parent 3 of the germplasm name.
par4 | String | Parent 4 of the germplasm name.
pedigree | String | Pedigree of germplasm.
par1_type | String | Parent 1 type of the germplasm name
par2_type | String | Parent 2 type of the germplasm name
par3_type | String | Parent 3 type of the germplasm name
par4_type | String | Parent 4 type of the germplasm name
trial_name | String | Trial name for field experiment that the sample is coming from, or fieldbook

<a name="callsetadditionalinfoexample">**Resource Example**</a>

```json
    {
        "par1": "IR04A428",
        "par2": "IR16A1957",
        "sample_group": "1"
    }
```

