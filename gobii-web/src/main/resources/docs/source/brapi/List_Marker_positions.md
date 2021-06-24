
Marker positions maps Genome Maps to the Variants in GDM system and their respective position in linkage group.

<a name="markerpositionsresource">**Resource Description**</a>

Field | Type | Description
------|------|------------
mapDbId | String | Id of genome map in GDM.
mapName | String | Name of the genome maps.
position | Number | Position of marker in linkage group.
variantDbId | String | Id of variant in GDM system.
variantName | String | Name of the Variant.

<a name="markerpositionsresourceexample">**Resource Example**</a>

```

    {
        "linkageGroupName": "1",
        "mapDbId": "2",
        "mapName": "Maize_map",
        "position": 20.000,
        "variantDbId": "8",
        "variantName": "Sr2_ger9 3p"
    }

```

**Response Body** is a [BrAPI response](#brapilistresponsetemplate) with List of [MarkerPositions](#markerpositionsresourceexample) in result.data.


