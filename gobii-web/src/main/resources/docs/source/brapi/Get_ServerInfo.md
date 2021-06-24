
Gets the list of BrAPI endpoints in the GDM system.

Successful operation will return below response with HTTP Status Code 200 Ok.

**Response Body Example**

```json

{
    "metadata": {
        "pagination": {
            "totalCount": 1,
            "pageSize": 1,
            "totalPages": 0,
            "currentPage": 0
        },
        "status": [],
        "datafiles": []
    },
    "result": {
        "calls": [
            {
                "service": "callsets/{callSetDbId}",
                 "dataTypes": [
                    "GET"
                 ],
                 "methods": [
                     "application/json"
                 ],
                 "versions": [
                     "2.0"
                 ] 
            }
        ]
    }
}
        
```






