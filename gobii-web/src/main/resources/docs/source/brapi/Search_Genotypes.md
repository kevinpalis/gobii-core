**Request Body**

Any fields other than the below fields will be ignored by the system.

*Search by only giving variantDbIds/variantNames is yet to be implemented.*

Field | Type | Required/Optioanl
------|------|------------
variantDbIds | Array(String) | *Optional* Maximum Size allowed = 1000. Corresponds to marker ids.
variantNames | Array(String) | *Optional* Maximum Size allowed = 1000. Corresponds to marker Names.
callSetDbIds | Array(String) | *Optional* Maximum Size allowed = 1000.
callSetNames | Array(String) | *Optional* Maximum Size allowed = 1000.
variantSetDbIds | Array(String) | *Optional* Maximum Size allowed = 1000.
germplasmPUIs | Array(String) | *Optional* Maximum Size allowed = 1000. Corresponds to germplasm external codes.

**Request Body Example : DnaRun names and Marker Names**

```json

    {
        "callSetNames" : ["WL18PVSD000016", "181GPUR_ICP_2_1_12"],
        "variantNames" : ["Lr34_TCCIND", "Cdex5-6ID", "Sr2_ger9 3p", "snpOS0287"] 
    }

```

**Request Body Example : Germplasm External Codes and Marker Names**

```json
    {
        "variantNames" : ["Lr34_TCCIND", "Cdex5-6ID", "Sr2_ger9 3p", "snpOS0287", "PMP3-2"],
        "germplasmPUIs" : ["300266848", "WL18PVSD000001", "WL18PVSD000002", "IR12A282_IR08A176"]
    } 
```
