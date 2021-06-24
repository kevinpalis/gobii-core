**Request Body**

Any fields other than the below fields will be ignored by the system.

Field | Type | Required/Optioanl
------|------|------------
samplePUIs | Array(String) | *Optional* Maximum Size allowed = 1000. Corresponds to dnasample UUIDs.
sampleDbIds | Array(String) | *Optional* Maximum Size allowed = 1000. Corresponds to dnasample ids.
sampleNames | Array(String) | *Optional* Maximum Size allowed = 1000. Corresponds to dnasample names. 
germplasmPUIs | Array(String) | *Optional* Maximum Size allowed = 1000. Corresponds to germplasm external codes.
germplasmDbIds | Array(String) | *Optional* Maximum Size allowed = 1000. Corresponds to germplasm ids.
germplasmNames | Array(String) | *Optional* Maximum Size allowed = 1000. Corresponds to germplasm names. 

**Request Body Example : Sample Names**

```json

    {
        "sampleNames" : ["WL18PVSD000016", "181GPUR_ICP_2_1_12"]
    }

```

**Request Body Example : Germplasm external codes and sample names**

```json
    {
        "sampleNames" : ["WL18PVSD000016", "181GPUR_ICP_2_1_12"],
        "germplasmPUIs" : ["300266848", "WL18PVSD000001", "WL18PVSD000002", "IR12A282_IR08A176"]
    } 
```
