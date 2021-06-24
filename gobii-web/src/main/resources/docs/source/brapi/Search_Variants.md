**Request Body**

Any fields other than the below fields will be ignored by the system.

Field | Type | Required/Optioanl
------|------|------------
variantDbIds | Array(String) | *Optional* Maximum Size allowed = 1000. Corresponds to marker ids.
variantNames | Array(String) | *Optional* Maximum Size allowed = 1000. Corresponds to marker Names.
variantSetDbIds | Array(String) | *Optional* Maximum Size allowed = 1000. Corresponds to dataset ids.

**Request Body Example : Marker Names**

```json

    {
        "variantNames" : ["Lr34_TCCIND", "Cdex5-6ID", "Sr2_ger9 3p", "snpOS0287"] 
    }

```

**Request Body Example : Marker Names and Dataset Ids**

```json
    {
        "variantNames" : ["Lr34_TCCIND", "Cdex5-6ID", "Sr2_ger9 3p", "snpOS0287", "PMP3-2"],
        "variantSetDbIds" : ["2", "4"]
    } 
```
