FORMAT: 1A
HOST: http://api.gobii.org:8081

# GDM Web Services v3.0


The GDM web service API provides a comprehensive set of RESTful methods for accomplishing the following core tasks in the GOBii Genomic Data Management (GDM) system.

API Response will follow the same structure as BrAPI template below,

Pagination follows zero based index(same as BrAPI).

**GDM hosts multuple crops. List of crops in a GDM instance can be obtianed using [list crops](#/reference/crops/list-crops) endpoint.**

**All other endpoints have a cropType path varible which can be selected from the same [list crops](#/reference/crops/list-crops) endpoint.**

[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/fd6fdc5d068f41bde2b0#?env%5BGDM%20v3%20Variables%5D=W3sia2V5IjoiQXBpSG9zdCIsInZhbHVlIjoiY2JzdWdvYmlpeHZtMjMuYmlvaHBjLmNvcm5lbGwuZWR1IiwiZW5hYmxlZCI6dHJ1ZX0seyJrZXkiOiJVc2VybmFtZSIsInZhbHVlIjoidXNlcl9yZWFkZXIiLCJlbmFibGVkIjp0cnVlfSx7ImtleSI6IlBhc3N3b3JkIiwidmFsdWUiOiJyZWFkZXIiLCJlbmFibGVkIjp0cnVlfSx7ImtleSI6IkFjY2Vzc1Rva2VuIiwidmFsdWUiOiJleUpoYkdjaU9pSlNVekkxTmlJc0luUjVjQ0lnT2lBaVNsZFVJaXdpYTJsa0lpQTZJQ0l4TkVneU5FcEJNR0ZUWjB4dmNsaE1WVmx0WlcxemIyWXRaR3RyT0hGWFNIaHhWR0k0VG1GR04xWkpJbjAuZXlKbGVIQWlPakUyTURJMU9UWTBNalFzSW1saGRDSTZNVFl3TWpVNU5EWXlOQ3dpYW5ScElqb2lPVEUxT0dWa01EQXRaVGczT0MwME5tTTVMV0kwTkRBdFkyVmtaV1UxWVRNeVkyVmpJaXdpYVhOeklqb2lhSFIwY0RvdkwyTmljM1ZuYjJKcGFYaDJiVEl6TG1KcGIyaHdZeTVqYjNKdVpXeHNMbVZrZFRvNE1EZzJMMkYxZEdndmNtVmhiRzF6TDBkdlltbHBMVlJsYzNRaUxDSmhkV1FpT2lKaFkyTnZkVzUwSWl3aWMzVmlJam9pWWpjMk5UUTVaRFV0WW1OaE5DMDBaVEF4TFRrNE1qVXROVGxpWVRjMU5HVTNOMkU0SWl3aWRIbHdJam9pUW1WaGNtVnlJaXdpWVhwd0lqb2laMlJ0TFhkbFlpMXpaWEoyYVdObGN5SXNJbk5sYzNOcGIyNWZjM1JoZEdVaU9pSmhaRGc0TkRsa09TMWxaREZtTFRSbFpqY3RPVFppTlMwNU0yUm1OakF5T1dZeVptUWlMQ0poWTNJaU9pSXhJaXdpWVd4c2IzZGxaQzF2Y21sbmFXNXpJanBiSWlvaVhTd2ljbVZoYkcxZllXTmpaWE56SWpwN0luSnZiR1Z6SWpwYkltOW1abXhwYm1WZllXTmpaWE56SWl3aWRXMWhYMkYxZEdodmNtbDZZWFJwYjI0aVhYMHNJbkpsYzI5MWNtTmxYMkZqWTJWemN5STZleUpuWkcwdGQyVmlMWE5sY25acFkyVnpJanA3SW5KdmJHVnpJanBiSWtOVlVrRlVUMUlpTENKVlUwVlNJbDE5TENKaFkyTnZkVzUwSWpwN0luSnZiR1Z6SWpwYkltMWhibUZuWlMxaFkyTnZkVzUwSWl3aWJXRnVZV2RsTFdGalkyOTFiblF0YkdsdWEzTWlMQ0oyYVdWM0xYQnliMlpwYkdVaVhYMTlMQ0p6WTI5d1pTSTZJbVZ0WVdsc0lIQnliMlpwYkdVaUxDSmxiV0ZwYkY5MlpYSnBabWxsWkNJNmRISjFaU3dpYm1GdFpTSTZJbFpwYzJodWRTQkhiM1pwYm1SaGNtRnFJaXdpWTNKdmNIUjVjR1V1Y205c1pTSTZXeUl2WkdWMkwyTjFjbUYwYjNJaUxDSXZlbTloYmlKZExDSm5jbTkxY0hNaU9sc2lMMlJsZGk5amRYSmhkRzl5SWl3aUwzcHZZVzRpWFN3aWNISmxabVZ5Y21Wa1gzVnpaWEp1WVcxbElqb2lkWE5sY2w5eVpXRmtaWElpTENKbmFYWmxibDl1WVcxbElqb2lWbWx6YUc1MUlpd2labUZ0YVd4NVgyNWhiV1VpT2lKSGIzWnBibVJoY21GcUlpd2laVzFoYVd3aU9pSjJaekkwT1VCamIzSnVaV3hzTG1Wa2RTSjkuTktkQ3hGaGdLbTBQcDRZeEpJQWU2Rm9yY2I4TXFHT0FITVJVSFZNTE10N0tpS1NaZDZJckI0a2NSWUQwTzhRalgybXA2c1FlOTFPX21xS0ZWS2hTbkRVdE1nbDZpYUo1S2lBaHVoT1hLVU8tMnNSanJWUDFQeDJRbnplWTRNclk0U3JlNko2TkN2ZFU5WVp1aTZJX0VLWm4zanp6UWVUWl9FRjZsQWw0aWQxMkVBdGp0TXQybVRyQ1VEVlJVcC1vYlJqOTdLak1WMFNhVmpBdEFHRklaY3VRWjlIcmFDTzVNOF96bnVmYmNOYjhydXByUWFleERMUkxYN3JZSGRsOVp5R1hzVWJpR1pkM1BzNFF0NHpGbnhja05LT2Y3a3ZnVmppWEZFRFhxUW1QdU9Gd3hCNzZfRmFaY0RHcjFMZk5QYlZXR0cxUEZmWEJQVlA2TXQ1dkNRIiwiZW5hYmxlZCI6dHJ1ZX0seyJrZXkiOiJDcmVhdGVkUHJvamVjdCIsInZhbHVlIjoiMTA0IiwiZW5hYmxlZCI6dHJ1ZX0seyJrZXkiOiJDcmVhdGVkQW5hbHlzaXMiLCJ2YWx1ZSI6IjEwIiwiZW5hYmxlZCI6dHJ1ZX0seyJrZXkiOiJDcmVhdGVkQW5hbHlzaXNUeXBlIiwidmFsdWUiOm51bGwsImVuYWJsZWQiOnRydWV9LHsia2V5IjoiQ3JlYXRlZERhdGFzZXQiLCJ2YWx1ZSI6IjM0IiwiZW5hYmxlZCI6dHJ1ZX0seyJrZXkiOiJTZWxlY3RlZEN2R3JvdXAiLCJ2YWx1ZSI6IjE4IiwiZW5hYmxlZCI6dHJ1ZX0seyJrZXkiOiJDcmVhdGVkTWFwc2V0IiwidmFsdWUiOiI1MiIsImVuYWJsZWQiOnRydWV9LHsia2V5IjoiQ3JlYXRlZEV4cGVyaW1lbnQiLCJ2YWx1ZSI6IjQ5IiwiZW5hYmxlZCI6dHJ1ZX0seyJrZXkiOiJBdXRoU2VydmVyIiwidmFsdWUiOiJjYnN1Z29iaWl4dm0yMy5iaW9ocGMuY29ybmVsbC5lZHU6ODA4NiIsImVuYWJsZWQiOnRydWV9LHsia2V5IjoiUmVhbG1OYW1lIiwidmFsdWUiOiJHb2JpaS1UZXN0IiwiZW5hYmxlZCI6dHJ1ZX1d)


<a name="brapiresponsetemplate">**BrAPI Response Template**</a>

```

{
  "metadata" : {
    "pagination" : {},
    "status" : [ ],
    "datafiles" : [ ]
  },
  "result" : {
    "key0": "master",
    "key1": 20,
    "key2": [ "foo", "bar", "baz" ]
  }
}

```

<a name="brapilistresponsetemplate">**BrAPI List Response Template**</a>

```
{
  "metadata" : {
    "pagination" : {
      "totalCount" : 20,
      "pageSize" : 3,
      "totalPages" : 7,
      "currentPage" : 0
    },
    "status" : [ ],
    "datafiles" : [ ]
  },
  "result" : {
    "data" : [ 
      {
        "detailKey0" : "detail0",
        "detailKey1" : [ "foo", "bar" ]
      }, 
      {
        "detailKey0" : "detail1",
        "detailKey1" : [ "bar", "baz" ]
      }, 
      {
        "detailKey0" : "detail2",
        "detailKey1" : [ "baz", "foo" ]
      },
    ]
  }
}

```


## Group Authentication

### Authenticate [/auth/realms/{realmName}/protocol/openid-connect/token]

#### Authenticates the user name and password [POST]

GDM v3 uses keycloak identity management system to authenticate users.
For testing apis, Please use below form-data as request body,

```

grant_type:password
client_id:gdm-web-services
username:user
password:**7

```

Contact [Gobii](http://cbsugobii05.biohpc.cornell.edu/wordpress/index.php/contact/) to get details on auth server test instance and keycloak realm name.  

+ Request (application/x-www-form-urlencoded)


    + Body
        
        grant_type=password&client_id=gdm-web-services&username=user&password=****

+ Response 200

    + Attributes

    + Body
    
    {
        "access_token": "AccessToken",
        "expires_in": 1800,
        "refresh_expires_in": 1800,
        "refresh_token": "ReFAccessToken",
        "token_type": "bearer",
        "not-before-policy": 1600088560,
        "session_state": "sessionState",
        "scope": "email profile"
    }

+ Response 401

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500
    
    + Body
    
        {
            "error": "invalid_grant",
            "error_description": "Invalid user credentials"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }
            

## Group Crops

Crop represents crop database hosted by GDM. Each crop has its own independent authorization levels and maintains seperate databases.

<a name="cropsresourcefields">**Resource Description**</a>

Below table describes the Crops Resource with their paramters name, type and
description.

Field | Type | Description
------|------|------------
cropName | String | Name of the crop.
userAuthorized | Boolean | Tells whether the authenticated user is authroiuzed to access crop or not.

<a name="cropsresourceexample">**Resource Example**</a>

```json
    {
        "cropType": "dev",
        "userAuthorized": true
    }
```

### List Crops [/gdm/crops]

#### Lists Crops in GDM [GET /gdm/crops{?page,pageSize}]

**Response Body**

Field | Type | Description
------|------|------------
metaData | Object | Metadata with pagination.
result | Object | Result object [BrAPI List Resonse](#brapilistresponsetemplate) where data property has List of [Crops](#cropsresourceexample) as value

+ Parameters

    + page (optional) - Page number to be fetched based on page size. Default is 0.
    
    + pageSize (optional) - Size of the page to be fetched. Default is 1000.
    

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes

    + Body
    
        {
            "metadata" : {
                "pagination" : {
                    "pageSize" : 2,
                    "currentPage" : 0
                },
            },
            "result" : {
                "data" : [ 
                    {
                        "cropType": "dev",
                        "userAuthorized": true
                    },
                    {
                        "cropType": "zoan",
                        "userAuthorized": false
                    }
                ]
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


## Group Organizations

Organization is a company or institute. Organization may be associated with a contact in the Define Contacts page. An organization can also describe a vendor and be associated with a protocol to create a vendor-protocol in the Define Protocols page.

<a name="organizationresourcefields">**Resource Description**</a>

Below table describes the Organization Resource with their paramters name, type and
description.

Field | Type | Description
------|------|------------
organizationId | String | Id of the organization to which PI belongs to.
organizationName | String | Name of the organization to which PI belongs to.
organizationWebsite | String | Website of the Organization.
organizationAddress | String | Address of the organization.
status | Enum(String) | One of the following status (new, modified, deleted).
createdBy | String | Contact Id of the user who created the contact.
createdDate | String | Created date string in UTC
modifiedBy | String | Contact Id of the user who modified the contact.
modifiedDate | String | Modified date string in UTC

<a name="organizationresourceexample">**Resource Example**</a>

```json
    {
        "organizationId": "22",
        "organizationName" : "circle",
        "organizationWebsite": "http://circle.org",
        "organizationAddress": "22/7, Hippapus Street, Atlantis - 85321",
        "createdBy": "1",
        "createdDate": "2020-01-25T04:00:00",
        "modifiedBy": "1",
        "modifiedDate": "2020-02-25T04:00:00"
    }
```

### Create Organization [/gdm/crops/{cropType}/gobii/v3/organizations]

#### Creates a new organization [POST]

**Request Body**

Any fields other than the below fields will be ignored by the system.

Field | Type | Required/Optioanl
------|------|------------
organizationName | String | **Required** 
organizationWebsite | String | *Optional*
organizationAddress | String | *Optional*


**Request Body Example**

```json
    
    {
        "organizationName" : "circle",
        "organizationWebsite": "http://circle.org",
        "organizationAddress": "22/7, Hippapus Street, Atlantis - 85321"
    }

```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for Organization
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/organizations"
  
    # Request Body
    data = {
        "organizationName" : "circle",
        "organizationWebsite": "http://circle.org",
        "organizationAddress": "22/7, Hippapus Street, Atlantis - 85321"
    }
    
    # Successful update will have http status code 201
    response = requests.post(url, 
                             json=data,
                             headers= {
                                "Authorization" : "Bearer 123apiKey!"
                            })   

    
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
        {
            "organizationName" : "circle",
            "organizationWebsite": "http://circle.org",
            "organizationAddress": "22/7, Hippapus Street, Atlantis - 85321"
        }

+ Response 201

    + Attributes

    + Body
    
        {
            "result" : {
                "organizationId": "22",
                "organizationName" : "circle",
                "organizationWebsite": "http://circle.org",
                "organizationAddress": "22/7, Hippapus Street, Atlantis - 85321",
                "createdBy": "1",
                "createdDate": "2020-01-25T04:00:00",
                "modifiedBy": "1",
                "modifiedDate": "2020-02-25T04:00:00"
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### List Organizations [/gdm/crops/{cropType}/gobii/v3/organizations]

#### Lists organizations in GDM [GET /gdm/crops/{cropType}/gobii/v3/organizations{?page,pageSize}]

**Response Body**

Field | Type | Description
------|------|------------
metaData | Object | Metadata with pagination.
result | Object | Result object [BrAPI List Resonse](#brapilistresponsetemplate) where data property has List of [Organizations](#organizationresourceexample) as value

+ Parameters

    + page (optional) - Page number to be fetched based on page size. Default is 0.
    
    + pageSize (optional) - Size of the page to be fetched. Default is 1000.

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes

    + Body
    
        {
            "metadata" : {
                "pagination" : {
                    "pageSize" : 2,
                    "currentPage" : 0
                },
            },
            "result" : {
                "data" : [ 
                    {
                        "organizationId": "22",
                        "organizationName" : "circle",
                        "organizationWebsite": "http://circle.org",
                        "organizationAddress": "22/7, Hippapus Street, Atlantis - 85321",
                        "createdBy": "1",
                        "createdDate": "2020-01-25T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate": "2020-02-25T04:00:00"
                    },
                    {
                        "organizationId": "54",
                        "organizationName" : "office",
                        "organizationWebsite": "http://the.office.org",
                        "organizationAddress": "1725 Slough Avenue, Scranton, PA, USA",
                        "createdBy": "1",
                        "createdDate": "2020-01-15T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate": "2020-02-25T04:00:00"
                    }
                ]
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### Update Organization by Id [/gdm/crops/{cropType}/gobii/v3/organizations/{organizationId}]

#### Update existing organization [PATCH]

**Only the fields in the request body will be updated.** <br>

Updating any fields other than the below fields will be ignored by the system.

**Request Body Parameters**

Field | Type | Required/Optioanl
------|------|------------
organizationName | String | **Optional** 
organizationWebsite | String | *Optional*
organizationAddress | String | *Optional*

**Request Body Example**

Assume existing parent resource is as below,

```json

    {
        "organizationId": "22",
        "organizationName" : "circle",
        "organizationWebsite": "http://circle.org",
        "organizationAddress": "22/7, Hippapus Street, Atlantis - 85321",
        "createdBy": "1",
        "createdDate": "2020-01-25T04:00:00",
        "modifiedBy": "1",
        "modifiedDate": "2020-02-25T04:00:00"
    }

```

If the **patch request body** is as below, 

```json

    {
        /* Updates exising organization website */
        "organizationWebsite": "http://sector.circle.org",
    }
    
```

then the updated parent resource will be as below,

```json


    {
        "organizationId": "22",
        "organizationName" : "circle",
        "organizationWebsite": "http://sector.circle.org",
        "organizationAddress": "22/7, Hippapus Street, Atlantis - 85321",
        "createdBy": "1",
        "createdDate": "2020-01-25T04:00:00",
        "modifiedBy": "1",
        "modifiedDate": "2020-02-25T04:00:00"
    }

```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for organization id 22
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/organizations/22"
  
    # organizationWebsite needs to updated
    data = {
        "organizationWebsite" : "http://sector.circle.org"
    }
    
    # Successful creation will have http status code 201
    response = requests.patch(url, 
                                json=data, 
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
    {
        "organizationWebsite": "http://sector.circle.org", /* Updates the exising organization website from http://circle.org to http://sector.circle.org*/
    }

+ Response 200

    + Attributes

    + Body
    
        {
            "result" : {
                "organizationId": "22",
                "organizationName" : "circle",
                /* Website Updated */
                "organizationWebsite": "http://sector.circle.org",
                "organizationAddress": "22/7, Hippapus Street, Atlantis - 85321",
                "createdBy": "1",
                "createdDate": "2020-01-25T04:00:00",
                "modifiedBy": "1",
                "modifiedDate": "2020-02-25T04:00:00"
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### Get Organization by Id [/gdm/crops/{cropType}/gobii/v3/organizations/{organizationId}]

#### Get existing organization [GET]

Retrieves the Organization resource having the specified ID

+ Parameters

    + organizationId (required) - ID of the Organization to be extracted

+ Request

    + Headers

            Authorization :  Bearer  

+ Response 200

    + Attributes
    
    + Body
    
        {
            "result" : {
                "organizationId": "22",
                "organizationName" : "circle",
                "organizationWebsite": "http://sector.circle.org",
                "organizationAddress": "22/7, Hippapus Street, Atlantis - 85321",
                "createdBy": "1",
                "createdDate": "2020-01-25T04:00:00",
                "modifiedBy": "1",
                "modifiedDate": "2020-02-25T04:00:00"
            }
        }
    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### Delete Organization by Id [/gdm/crops/{cropType}/gobii/v3/organizations/{organizationId}]

#### Delete existing organization [DELETE]

Deletes the Organization resource having the specified ID.

**Response**

If Sucessful(HTTP status code: 204), Method will return empty response body.

If called again, Method will return HTTP status code 404(Not Found).

Returns 409(Conflict), If associated resources PI contacts or Vendors Protocols are found.

+ Parameters

    + organizationId (required) - ID of the Organization to be deleted.

+ Request

    + Headers

            Authorization :  Bearer  

+ Response 204

    + Attributes
    
    + Body
        
        
    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Associated resources found. Cannot complete the action unless they are deleted."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }
            
+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


## Group PI Contacts

A Principal Investigator (PI), also called a PI contact.

<a name="picontactresourcefields">**Resource Description**</a>

Below table describes the Contacts Resource with their paramters name, type and
description.

Field | Type | Description
------|------|------------
piContactId | String | Id of PI (Principal investigator) of the project in GDM system. The PI contact can be searched using /contacts API.  *The descirption for the same will be added to this document later*
piContactFirstName | String | First Name of PI Contact.
piContactLastName | String | Last Name of PI Contact.
organizationId | String | Id of the organization to which PI belongs to.
organizationName | String | Name of the organization to which PI belongs to.
createdBy | String | Contact Id of the user who created the contact.
createdDate | String | Created date string in UTC
modifiedBy | String | Contact Id of the user who modified the contact.
modifiedDate | String | Modified date string in UTC

<a name="picontactresourceexample">**Resource Example**</a>

```json
    {
        "piContactId": "7",
        "piContactFirstName" : "im",
        "piContactLastName" : "Pi",
        "organizationId": "22",
        "organizationName" : "circle",
        "createdBy": "1",
        "createdDate": "2019-07-25T04:00:00",
        "modifiedBy": "1",
        "modifiedDate": "2019-07-25T04:00:00"
    }
```

### List Contatcs [/gdm/crops/{cropType}/gobii/v3/contacts]

#### Lists contacts in GDM [GET /gdm/crops/{cropType}/gobii/v3/contacts{?page,pageSize}]

**Response Body**

Field | Type | Description
------|------|------------
metaData | Object | Metadata with pagination.
result | Object | Result object [BrAPI List Resonse](#brapilistresponsetemplate) where data property has List of [Contacts](#picontactresourceexample) as value

+ Parameters

    + page (optional) - Page number to be fetched based on page size. Default is 0.
    
    + pageSize (optional) - Size of the page to be fetched. Default is 1000.
    

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes

    + Body
    
        {
            "metadata" : {
                "pagination" : {
                    "pageSize" : 2,
                    "currentPage" : 0
                },
            },
            "result" : {
                "data" : [ 
                    {
                        "piContactId": "7",
                        "piContactFirstName" : "im",
                        "piContactLastName" : "Pi",
                        "organizationId": "22",
                        "organizationName" : "circle",
                        "createdBy": "1",
                        "createdDate": "2019-07-25T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate": "2019-07-25T04:00:00"
                    },
                    {
                        "piContactId": "4",
                        "piContactFirstName" : "joe",
                        "piContactLastName" : "root",
                        "organizationId": "2",
                        "organizationName" : "square",
                        "createdBy": "1",
                        "createdDate": "2019-06-25T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate": "2019-06-25T04:00:00"
                    }
                ]
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


## Group Projects

A project consists of a group of samples that are, or will be, genotyped. A project belongs to a Principal Investigator (PI), also called a PI contact.

<a name="projectresourcefields">**Resource Description**</a>

Below table describes the Project Resource with their paramters name, type and
description.

Field | Type | Description
------|------|------------
projectId | String | Id of the Project in GDM.
piContactId | String | Id of PI (Principal investigator) of the project in GDM system. The PI contact can be searched using /contacts API.  *The descirption for the same will be added to this document later*
piContactName | String | Name of PI (Principal investigator) of the project
projectName | String | Name of the project. The project name must be unique.
projectDescription | String | Field to describe the project.
properties | Array | Array of properties object with property values for given entity. Set of predefined property id and name can be obtained using [GET /project/properties](#/reference/projects/list-project-properties). New properties for the project can be defined using [POST /project/properties](#/reference/projects/create-project-property).
experimentCount | Integer | Number of experiments in the project
datasetCount | Integer | Number of datasets associated with the project
markerCount | Integer | Number of Markers associated with the project
dnaRunCount | Integer | Number of DnaRuns associated with teh project
createdBy | String | Contact Id of the user who created the project.
createdDate | String | Created date string in UTC
modifiedBy | String | Contact Id of the user who modified the project.
modifiedDate | String | Modified date string in UTC

<a name="projectresourceexample">**Resource Example**</a>

```json
    {
        "projectId": "1",
        "piContactId": 34,
        "piContactName" : "imPi",
        "projectName": "foo_proj_01",
        "projectDescription": "foo 01 project",
        "experimentCount" : 5,
        "datasetCount" : 6,
        "markersCount" : 15000,
        "dnaRunsCount" : 1000,
        "createdBy": "1",
        "createdDate": "2019-07-25T04:00:00",
        "modifiedBy": "1",
        "modifiedDate": "2019-07-25T04:00:00",
        "properties": [
            {
                "propertyId" : "1",
                "propertyName" : "testProp",
                "propertyValue" : "testPropValue",
                "propertyDescription" : "testing...",
                "propertyGroupId" : "4",
                "propertyGroupName" : "project_prop"
            },
        ]
    }
```

### Create Project [/gdm/crops/{cropType}/gobii/v3/projects]

#### Creates a new project [POST]

**Request Body**

Any fields other than the below fields will be ignored by the system.

Field | Type | Required/Optioanl
------|------|------------
piContactId | String | **Required** List of contacts can be obtained using [GET /contacts](#/reference/pi-contacts/list-contatcs) endpoint.
projectName | String | **Required** 
projectDescription | String | *Optional* 
properties | Array | *Optional* Array of properties object with property values for given entity. Set of predefined property id and name can be obtained using [GET /project/properties](#/reference/projects/list-project-properties). New properties for the project can be defined using [POST /project/properties](#/reference/projects/create-project-property). **Any fields other than propertyId and propertyValue will be ignored**. **Property ojects with null values will be ignored**.

**Request Body Example**

```json

    {
        "piContactId": "34",
        "projectName": "foo_proj_01",
        "projectDescription": "foo 01 project",
        "properties": [
            {
                "propertyId" : "1",
                "propertyValue" : "testPropValue"
            },
        ]
    }
    
```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for Projects
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/projects"
  
    # Project to be created
    data = {
        "piContactId": "34",
        "projectName": "foo_proj_01",
        "projectDescription": "foo 01 project",
        "properties": [
            {
                "propertyId" : "1",
                "propertyValue" : "testPropValue"
            },
        ]
    }
    
    # Successful creation will have http status code 201
    response = requests.post(url, 
                                json=data, 
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
        {
            "piContactId": "34",
            "projectName": "foo_proj_01",
            "projectDescription": "foo 01 project",
            "properties": [
                {
                    "propertyId" : "1",
                    "propertyValue" : "testPropValue"
                },
            ]
        }

+ Response 201

    + Attributes

    + Body
    
        {
            "result" : {
                "projectId": "1",
                "piContactId": 34,
                "piContactName" : "imPi",
                "projectName": "foo_proj_01",
                "projectDescription": "foo 01 project",
                "experimentCount" : 0,
                "datasetCount" : 0,
                "markersCount" : 0,
                "dnaRunsCount" : 0,
                "createdBy": "1",
                "createdDate": "2019-07-25T04:00:00",
                "modifiedBy": "1",
                "modifiedDate": "2019-07-25T04:00:00",
                "properties": [
                    {
                        "propertyId" : "1",
                        "propertyName" : "testProp",
                        "propertyValue" : "testPropValue",
                        "propertyDescription" : "testing...",
                        "propertyGroupId" : "4",
                        "propertyGroupName" : "project_prop",
                        "propertyType" : "system"
                    },
                ]
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### List Projects [/gdm/crops/{cropType}/gobii/v3/projects]

#### Lists projects in GDM [GET /gdm/crops/{cropType}/gobii/v3/projects{?page,pageSize,piContactId}]

**Response Body**

Field | Type | Description
------|------|------------
metaData | Object | Metadata with pagination.
result | Object | Result object [BrAPI List Resonse](#brapilistresponsetemplate) where data property has List of [Projects](#projectresourceexample) as value

+ Parameters

    + page (optional) - Page number to be fetched based on page size. Default is 0.
    
    + pageSize (optional) - Size of the page to be fetched. Default is 1000.
    
    + piContactId (optional) - Filter Projects by PI Contact Id

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes

    + Body
    
        {
            "metadata" : {
                "pagination" : {
                    "pageSize" : 2,
                    "currentPage" : 0
                },
            },
            "result" : {
                "data" : [ 
                    {
                        "projectId": "1",
                        "piContactId": 34,
                        "piContactName" : "imPi",
                        "projectName": "foo_proj_01",
                        "projectDescription": "foo 01 project",
                        "experimentCount" : 5,
                        "datasetCount" : 6,
                        "markersCount" : 15000,
                        "dnaRunsCount" : 1000,
                        "createdBy": "1",
                        "createdDate": "2019-07-25T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate": "2019-07-25T04:00:00",
                        "properties": [
                            {
                                "propertyId" : "1",
                                "propertyName" : "testProp",
                                "propertyValue" : "testPropValue",
                                "propertyDescription" : "test description",
                                "propertyGroupId" : "4",
                                "propertyGroupName" : "project_prop",
                                "propertyType" : "system"
                            },
                        ]
                    },
                    {
                        "projectId": "2",
                        "piContactId": 34,
                        "piContactName" : "imPi",
                        "projectName": "foo_proj_02",
                        "projectDescription": "foo 01 project",
                        "experimentCount" : 3,
                        "datasetCount" : 4,
                        "markersCount" : 5000,
                        "dnaRunsCount" : 100,
                        "createdBy": "1",
                        "createdDate": "2019-07-27T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate": "2019-07-27T04:00:00",
                        "properties": [
                            {
                                "propertyId" : "1",
                                "propertyName" : "testProp",
                                "propertyValue" : "testPropValue",
                                "propertyDescription" : "test description",
                                "propertyGroupId" : "4",
                                "propertyGroupName" : "project_prop",
                                "propertyType" : "system"
                            },
                        ]
                    }

                ]
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### Update Project by Id [/gdm/crops/{cropType}/gobii/v3/projects/{projectId}]

#### Update existing project [PATCH]

**Only the fields in the request body will be updated.** <br>

**To add new property** - *Add the property object with propertyId and propertyValue* <br>
**To delete a property** - *Make the propertyValue as null* <br>
**To update existing property** - *Add the property object with new value*

Updating any fields other than the below fields will be ignored by the system.

**Request Body Parameters**

Field | Type | Required/Optioanl
------|------|------------
piContactId | String | *Optional*
projectName | String | *Optional* 
projectDescription | String | *Optional* 
properties | Array | *Optional* Array of properties object with property values for given entity. Set of predefined property id and name can be obtained using [GET /project/properties](#/reference/projects/list-project-properties). New properties for the project can be defined using [POST /project/properties](#/reference/projects/create-project-property).

**Request Body Example**

Assume existing parent resource is as below,

```json

    {
        "projectId": "1",
        "piContactId": "34",
        "piContactName" : "imPi",
        "projectName": "foo_proj_01",
        "projectDescription": "foo 01 project",
        "experimentCount" : 0,
        "datasetCount" : 0,
        "markersCount" : 0,
        "dnaRunsCount" : 0,
        "createdBy": "1",
        "createdDate": "2019-07-25T04:00:00",
        "modifiedBy": "1",
        "modifiedDate": "2019-08-25T04:00:00",
        "properties": [
            {
                "propertyId" : "1",
                "propertyName" : "testProp",
                "propertyValue" : "testPropValue",
                "propertyDescription" : "test description",
                "propertyGroupId" : "4",
                "propertyGroupName" : "project_prop",
                "propertyType" : "system"
            }
        ]
    }

```

If the **patch request body** is as below, 

```json

    {
        /* Replaces the exising contact 34 with 43 */
        "piContactId": "43",
        "properties" : [
            /* If present, property with Id 1 will be deleted in parent resource as the value is null */
            {                       
                "propertyId" : 1,
                "propertyValue" : null 
            },
            {                       
            /* If present, value will be updated to testProp5, else, the property will be added */
                "propertyId" : 5,
                "propertyValue" : "testProp5"
            }
        ]
    }
    
```

then the updated parent resource will be as below,

```json

    {
        "projectId": "1",
        /* the pi contact id is updated */
        "piContactId": "43",          
        "piContactName" : "imPi22by7",
        "projectName": "foo_proj_01",
        "projectDescription": "foo 01 project",
        "experimentCount" : 0,
        "datasetCount" : 0,
        "markersCount" : 0,
        "dnaRunsCount" : 0,
        "createdBy": "1",
        "createdDate": "2019-07-25T04:00:00",
        "modifiedBy": "1",
        "modifiedDate": "2019-08-25T04:00:00",
        "properties": [
            /* the property with Id 5 is added and property with id 1 is deleted as the value was null*/
            {   
                "propertyId" : "5", 
                "propertyName" : "testProp5",
                "propertyValue" : "test",
                "propertyDescription" : "test description",
                "propertyGroupId" : "4",
                "propertyGroupName" : "project_prop",
                "propertyType" : "system"
            },
        ]
    }

```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for Project id 1
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/projects/1"
  
    # piContactId needs to be updated
    data = {
        "piContactId" : "43",
        "properties" : [
            {
                "propertyId" : "1",
                "propertyValue" : null
            },
            {
                "propertyId" : "5",
                "propertyValue" : "testProp5"
            }
        ]
    }
    
    # Successful creation will have http status code 201
    response = requests.patch(url, 
                                json=data, 
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
    {
        "piContactId": "43", /* Replaces the exising contact 34 with 43 */
        "properties" : [
            {                       /* If present, property with Id 1 will be deleted in parent resource as value is null */
                "propertyId" : 1,
                "propertyValue" : null 
            },
            {                       /* If already present, value will be updated, else, the property will be added */
                "propertyId" : 5,
                "propertyValue" : "testProp5"
            }
        ]
    }

+ Response 200

    + Attributes

    + Body
    
        {
            "result" : {
                "projectId": "1",
                /* the pi contact is updated */
                "piContactId": "43",
                "piContactName" : "imPi22by43",
                "projectName": "foo_proj_01",
                "projectDescription": "foo 01 project",
                "experimentCount" : 0,
                "datasetCount" : 0,
                "markersCount" : 0,
                "dnaRunsCount" : 0,
                "createdBy": "1",
                "createdDate": "2019-07-25T04:00:00",
                "modifiedBy": "1",
                "modifiedDate": "2019-08-25T04:00:00",
                "properties": [
                    /* the property with Id 5 is added and property with id 1 is deleted as the value was null*/
                    {
                        "propertyId" : "5",
                        "propertyName" : "testProp5",
                        "propertyValue" : "test",
                        "propertyDescription" : "test description",
                        "propertyGroupId" : "4",
                        "propertyGroupName" : "project_prop",
                        "propertyType" : "system"
                    },
                ]
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### Get Project by Id [/gdm/crops/{cropType}/gobii/v3/projects/{projectId}]

#### Get existing project [GET]

Retrieves the Project resource having the specified ID

+ Parameters

    + projectId (required) - ID of the Project to be extracted

+ Request

    + Headers

            Authorization :  Bearer  

+ Response 200

    + Attributes
    
    + Body
    
        {
            "result" : {
                "projectId": "1",
                "piContactId": "43",
                "piContactName" : "imPi22by43",
                "projectName": "foo_proj_01",
                "projectDescription": "foo 01 project",
                "experimentCount" : 0,
                "datasetCount" : 0,
                "markersCount" : 0,
                "dnaRunsCount" : 0,
                "createdBy": "1",
                "createdDate": "2019-07-25T04:00:00",
                "modifiedBy": "1",
                "modifiedDate": "2019-08-25T04:00:00",
                "properties": [
                    {
                        "propertyId" : "5",
                        "propertyName" : "testProp5",
                        "propertyValue" : "test",
                        "propertyDescription" : "test description",
                        "propertyGroupId" : "4",
                        "propertyGroupName" : "project_prop",
                        "propertyType" : "system"
                    },
                ]
            }
        }


    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### Delete Project by Id [/gdm/crops/{cropType}/gobii/v3/projects/{projectId}]

#### Delete existing project [DELETE]

Deletes the Project resource having the specified ID.

**Response**

If Sucessful(HTTP status code: 204), Method will return empty response body.

If called again, Method will return HTTP status code 404.

Returns 409(Conflict), If associated resources like Experiment/Samples found.

+ Parameters

    + projectId (required) - ID of the Project to be deleted.

+ Request

    + Headers

            Authorization :  Bearer  

+ Response 204

    + Attributes
    
    + Body
        
        
    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Associated resources found. Cannot complete the action unless they are deleted."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }
            
+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### Create Project Property [/gdm/crops/{cropType}/gobii/v3/projects/properties]

#### Creates new Project Property [POST]

**Request Body**

Any fields other than the below fields will be ignored by the system.

Field | Type | Required/Optioanl
------|------|------------
propertyName | String | **Required** Unique property name.
propertyDescription | String | *Optional* 


**Request Body Example**

```json

    {
        "propertyName": "division",
        "propertyDescription": "division to which project belongs to",
    }
    
```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for Project Property
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/projects/properties"
  
    # Project Property to be created
    data = {
        "propertyName": "division",
        "propertyDescription": "division to which project belongs to",
    }
    
    # Successful update will have http status code 201
    response = requests.post(url,
                                json=data,
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body

        {
            "propertyName": "division",
            "propertyDescription": "division to which project belongs to",
        }

+ Response 201

    + Attributes

    + Body
        
        {
            "result" : {
                "propertyId" : "7",
                "propertyName": "division",
                "propertyDescription": "division to which project belongs to",
                "propertyGroupId" : "4",
                "propertyGroupName" : "project_prop",
                "propertyType" : "user defined"
            }
        }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### List Project Properties [/gdm/crops/{cropType}/gobii/v3/projects/properties]

#### Lists project properties in GDM [GET /gdm/crops/{cropType}/gobii/v3/projects/properties{?page,pageSize}]

Property Fields are additional information related to project. There are system defined properties whcih are predefined. Additional properties can be defined by the users. 

**Response Body**

Field | Type | Description
------|------|------------
metaData | Object | Metadata with pagination.
result | Object | Result object [BrAPI List Resonse](#brapilistresponsetemplate) where data property has List of [Project Property](#propertyresourceexample) as value

+ Parameters

    + page (optional) - Size of the page to be fetched. Default is 1000.
    
    + pageSize (optional) - Page number to be fetched based on page size. Default is 0.

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes

    + Body
    
        {
            "metadata" : {
                "pagination" : {
                    "pageSize" : 2,
                    "currentPage" : 0
                },
            },
            "result" : {
                "data" : [ 
                    {
                        "propertyId" : "1",
                        "propertyName": "genotyping_purp",
                        "propertyDescription": "genotyping purpose project belongs to",
                        "propertyGroupId" : "4",
                        "propertyGroupName" : "project_prop",
                        "propertyType" : "system"
                    },
                    {
                        "propertyId" : "7",
                        "propertyName": "division",
                        "propertyDescription": "division to which project belongs to",
                        "propertyGroupId" : "4",
                        "propertyGroupName" : "project_prop",
                        "propertyType" : "user defined"
                    }
                ]
            }
        }
    
+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


## Group Experiment

An experiment defines the protocol and vendor used to generate the genotyping data. More than one experiment can belong to a project.

<a name="experimentresourcefields">**Resource Description**</a>

Below table describes the Experiment Resource with their paramters name, type and
description.

Field | Type | Description
------|------|------------
experimentId | String | Id of the experiment
experimentName | String | Name of the experiment
projectId | String | Project Id unde which experiment is created.
projectName | String | Name of the Project
vendorId | String | Id of the vendor for the experiment.
vendorName | String | Name of te vendor.
protocolId | String | Id of the protocol used for experiment.
protocolName | String | Name of the protocol.
datasetCount | Integer | Number of datasets associated with the experiment.
markerCount | Integer | Number of Markers associated with the experiment.
dnaRunCount | Integer | Number of DnaRuns associated with teh experiment.
createdBy | String | Contact Id of the user who created the experiment.
createdDate | String | Created date string in UTC
modifiedBy | String | Contact Id of the user who modified the experiment.
modifiedDate | String | Modified date string in UTC

<a name="experimentresourceexample">**Resource Example**</a>

```
        {
            "experimentId" : "4",
            "experimentName": "foo experiment",
            "projectId": "7",
            "projectName" : "bar project",
            "vendorId": "4",
            "vendorName" : "vendor test",
            "protocolId": "16",
            "protocolName" "foo protocol",
            "platformId": "5",
            "platformName" : "test platform",
            "createdBy": "1",
            "createdDate": "2019-07-26T04:00:00",
            "modifiedBy": "1",
            "modifiedDate" : "2019-07-29T04:00:00",

        }
```

### Create Experiment [/gdm/crops/{cropType}/gobii/v3/experiments]

#### Creates a new experiment [POST]

**Request Body**

Any fields other than the below fields will be ignored by the system.

Field | Type | Required/Optioanl
------|------|------------
experimentName | String | **Required** 
projectId | String | **Required** List of projects can be obtained using [GET /projects](#/reference/projects/list-projects) endpoint.
vendorId | String | **Required** List of vendors can be obtained using [GET /organizations](#/reference/organizations/list-organizations) endpoint.
protocolId | String | **Required** List of protocols can be obtained using [GET /protocols](#/reference/protocols/list-protocols) endpoint.

**Request Body Example**

```json
    
    {
        "projectId" : "7", 
        "experimentName" : "fooExperiment",
        "vendorId": "4",
        "protocolId": "16",
    }

```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for Experiments
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/experiments"
  
    # Request Body
    data = {
        "projectId" : "7", 
        "experimentName" : "fooExperiment",
        "vendorId": "4",
        "protocolId": "16"
    }
    
    # Successful update will have http status code 201
    response = requests.post(url, 
                             json=data,
                             headers= {
                                "Authorization" : "Bearer 123apiKey!"
                            })   

    
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
        {
            "projectId" : "7", 
            "experimentName" : "fooExperiment",
            "vendorId": "4",
            "protocolId": "16"

        }


+ Response 201

    + Attributes

    + Body
    
        {
            "result" : {
                "experimentId" : "4",
                "experimentName": "foo experiment",
                "projectId": "7",
                "projectName" : "bar project",
                "vendorId": "4",
                "vendorName" : "vendor test",
                "protocolId": "16",
                "protocolName" "foo protocol",
                "platformId": "5",
                "platformName" : "test platform",
                "createdBy": "1",
                "createdDate": "2019-07-26T04:00:00",
                "modifiedBy": "1",
                "modifiedDate" : "2019-07-29T04:00:00"
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### List Experiments [/gdm/crops/{cropType}/gobii/v3/experiments]

#### Lists experiment in GDM [GET /gdm/crops/{cropType}/gobii/v3/experiments{?page,pageSize,projectId}]

**Response Body**

Field | Type | Description
------|------|------------
metaData | Object | Metadata with pagination.
result | Object | Result object [BrAPI List Resonse](#brapilistresponsetemplate) where data property has List of [Experiments](#experimentresourceexample) as value

+ Parameters

    + page (optional) - Page number to be fetched based on page size. Default is 0.
    
    + pageSize (optional) - Size of the page to be fetched. Default is 1000.

    + projectId (optional) - To filter experiments by projectId.

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes

    + Body
    
        {
            "metadata" : {
                "pagination" : {
                    "pageSize" : 2,
                    "currentPage" : 0
                },
            },
            "result" : {
                "data" : [ 
                    {
                        "experimentId" : "4",
                        "experimentName": "foo experiment",
                        "projectId": "7",
                        "projectName" : "bar project",
                        "vendorId": "4",
                        "vendorName" : "vendor test",
                        "protocolId": "16",
                        "protocolName" "foo protocol",
                        "platformId": "5",
                        "platformName" : "test platform",
                        "createdBy": "1",
                        "createdDate": "2019-07-26T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate" : "2019-07-29T04:00:00"
                    },
                    {
                        "experimentId" : "5",
                        "experimentName": "bar experiment",
                        "projectId": "4",
                        "projectName" : "foo project",
                        "vendorId": "4",
                        "vendorName" : "vendor test",
                        "protocolId": "16",
                        "protocolName" "foo protocol",
                        "platformId": "5",
                        "platformName" : "test platform",
                        "createdBy": "1",
                        "createdDate": "2019-09-26T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate" : "2019-09-29T04:00:00"
                    }
                ]
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### Update Experiment by Id [/gdm/crops/{cropType}/gobii/v3/experiments/{experimentId}]

#### Update existing experiment [PATCH]

**Only the fields in the request body will be updated.** <br>

Updating any fields other than the below fields will be ignored by the system.

**Request Body Parameters**

Field | Type | Required/Optioanl
------|------|------------
experimentName | String | *Optional*
projectId | String | *Optional* List of projects can be obtained using [GET /projects](#/reference/projects/list-projects) endpoint.
vendorId | String | *Optional* List of vendors can be obtained using [GET /organizations](#/reference/organizations/list-organizations) endpoint.
protocolId | String | *Optional* List of protocols can be obtained using [GET /protocols](#/reference/protocols/list-protocols) endpoint.

**Request Body Example**

Assume existing parent resource is as below,

```json

    {
        "experimentId" : "4",
        "experimentName": "foo experiment",
        "projectId": "7",
        "projectName" : "bar project",
        "vendorId": "4",
        "vendorName" : "vendor test",
        "protocolId": "16",
        "protocolName" "foo protocol",
        "platformId": "5",
        "platformName" : "test platform",
        "createdBy": "1",
        "createdDate": "2019-07-26T04:00:00",
        "modifiedBy": "1",
        "modifiedDate" : "2019-07-29T04:00:00"
    }

```

If the **patch request body** is as below, 

```json

    {
        /* Updates exising vendorId from 4 to 5 */
        "vendorId": "5"
    }
    
```

then the updated parent resource will be as below,

```json


    {
        "experimentId" : "4",
        "experimentName": "foo experiment",
        "projectId": "7",
        "projectName" : "bar project",
        /* Vendor Id updated from 4 to 5 */
        "vendorId": "5",
        "vendorName" : "new vendor",
        "protocolId": "16",
        "protocolName" "foo protocol",
        "platformId": "7",
        "platformName" : "test platform 7",
        "createdBy": "1",
        "createdDate": "2019-07-26T04:00:00",
        "modifiedBy": "1",
        "modifiedDate" : "2019-10-29T04:00:00"
    }

```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for experiment id 4
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/experiments/4"
  
    # vendorProtocolId needs to be updated
    data = {
        "vendorId": "5"
    }
    
    # Successful creation will have http status code 201
    response = requests.patch(url, 
                                json=data, 
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
    {
        "vendorId": "5", /* Updates the exising vendor Id 4 with 5 */
    }

+ Response 200

    + Attributes

    + Body
    
        {
            "result" : {
                "experimentId" : "4",
                "experimentName": "foo experiment",
                "projectId": "7",
                "projectName" : "bar project",
                /* Vendor Id updated from 4 to 5 */
                "vendorId": "5",
                "vendorName" : "new vendor",
                "protocolId": "16",
                "protocolName" "foo protocol",
                "platformId": "7",
                "platformName" : "test platform 7",
                "createdBy": "1",
                "createdDate": "2019-07-26T04:00:00",
                "modifiedBy": "1",
                "modifiedDate" : "2019-10-29T04:00:00"
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### Get Experiment by Id [/gdm/crops/{cropType}/gobii/v3/experiments/{experimentId}]

#### Get existing experiment [GET]

Retrieves the Experiment resource having the specified ID

+ Parameters

    + experimentId (required) - ID of the Experiment to be extracted

+ Request

    + Headers

            Authorization : 

+ Response 200

    + Attributes
    
    + Body
    
        {
            "result" : {
                "experimentId" : "4",
                "experimentName": "foo experiment",
                "projectId": "7",
                "projectName" : "bar project",
                "vendorId": "5",
                "vendorName" : "new vendor",
                "protocolId": "16",
                "protocolName" "foo protocol",
                "platformId": "7",
                "platformName" : "test platform 7",
                "createdBy": "1",
                "createdDate": "2019-07-26T04:00:00",
                "modifiedBy": "1",
                "modifiedDate" : "2019-10-29T04:00:00"
            }
        }
    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### Delete Experiment by Id [/gdm/crops/{cropType}/gobii/v3/experiments/{experimentId}]

#### Delete existing experiment [DELETE]

Deletes the Experiment resource having the specified ID.

**Response**

If Sucessful(HTTP status code: 204), Method will return empty response body.

If called again, Method will return HTTP status code 404(Not Found).

Returns 409(Conflict), If associated resources Dataset/DnaRun are found.

+ Parameters

    + experimentId (required) - ID of the Experiment to be deleted.

+ Request

    + Headers

            Authorization :  Bearer  

+ Response 204

    + Attributes
    
    + Body
        
        
    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Associated resources found. Cannot complete the action unless they are deleted."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }
            
+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


## Group Analyses

Analyses describe the different algorithms that were applied to the genotyping or sequence data to produce the final dataset being loaded. Each analysis is grouped into an analysis type. The analysis types are: calling (variant calling), cleaning, and imputation.

<a name="analysisresourcefields">**Resource Description**</a>

Below table describes the Analyses Resource with their paramters name, type and
description.

Field | Type | Description
------|------|------------
analysisId | String | Id of the Analysis in GDM.
analysisName | String | Name of the Analysis.
analysisypeId | String | Id of the Analysis type.
analysisTypeName | String | Name of the Analysis type.
program | String | Name of the program.
programVersion | String | Version of the program.
algorithm | String | Algorithm used for analysis.
sourceName | String | Source name of the program.
sourceVersion | String | Version of the source.
sourceUrl | String | Url for the source of program.
refernceId | String | Id of the reference.
referenceName | String | Name of the reference.
parameters | Object | Additional analysis parameters and their respective values. For example, Prior probability : 0.454.
createdBy | String | Contact Id of the user who created the project.
createdDate | String | Created date string in UTC
modifiedBy | String | Contact Id of the user who modified the project.
modifiedDate | String | Modified date string in UTC

<a name="analysesresourceexample">**Resource Example**</a>

```json

    {
        "analysisId": "7",
        "analysisName": "Test_Calling",
        "analysisTypeId": "5",
        "analysisTypeName": "calling",
        "program": "foo program",
        "programVersion": "bar version",
        "algorithm": "algogol",
        "sourceName": "foo source",
        "sourceVersion": "bar version",
        "sourceUrl": "http://yyy.xz",
        "referenceId" : "4",
        "referenceName": "foo ref",
        "parameters": {
            "Prior Probability" : "0.454"
        },
        "createdBy": "1",
        "createdDate": "2019-07-26T04:00:00",
        "modifiedBy": "1",
        "modifiedDate" : "2019-10-29T04:00:00"
    }

```

### Create Analysis [/gdm/crops/{cropType}/gobii/v3/analyses]

#### Creates a new analysis [POST]

**Request Body**

Any fields other than the below fields will be ignored by the system.

Field | Type | Required/Optioanl
------|------|------------
analysisName | String | **Required**
analysisTypeId | String | **Required**. List of predfined analysis type can be obtained by [GET /analyses/types](#/reference/analyses/list-analysis-types). New types can be added using [POST /analyses/types](#/reference/analyses/add-analysis-type)
program | String | *Optional*
programVersion | String | *Optional*
algorithm | String | *Optional*
sourceName | String | *Optional*
sourceVersion | String | *Optional*
sourceUrl | String | *Optional*
refernceId | String | *Optional*. List of reference can be obtained by [GET /references](#referenceresourceexample).
parameters | Object | *Optional* (String:String) Key Value pair.

**Request Body Example**

```json

    {
        "analysisName": "Test_calling",
        "analysisTypeId": "5",
        "program": "foo program",
        "programVersion": "bar version",
        "algorithm": "algogol",
        "sourceName": "foo source",
        "sourceVersion": "bar version",
        "sourceUrl": "http://yyy.xz",
        "referenceId": "4",
        "parameters": {
            "Prior Probability" : "0.454"
        }
    }

```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for Analyses
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/analyses"
  
    # Analysis to be created
    data = {
        "analysisName": "Test_calling",
        "analysisTypeId": "5",
        "program": "foo program",
        "programVersion": "bar version",
        "algorithm": "algogol",
        "sourceName": "foo source",
        "sourceVersion": "bar version",
        "sourceUrl": "http://yyy.xz",
        "referenceId": "4",
        "parameters": {
            "Prior Probability" : "0.454"
        }
    }
    
    # Successful creation will have http status code 201
    response = requests.post(url, 
                                json=data, 
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
        {
            "analysisName": "Test_calling",
            "analysisTypeId": "5",
            "program": "foo program",
            "programVersion": "bar version",
            "algorithm": "algogol",
            "sourceName": "foo source",
            "sourceVersion": "bar version",
            "sourceUrl": "http://yyy.xz",
            "referenceId": "4",
            "parameters": {
                "Prior Probability" : "0.454"
            }
        }

+ Response 201

    + Attributes

    + Body
    
        {
            "result" : {
                "analysisId": "7",
                "analysisName": "Test_Calling",
                "analysisTypeId": "5",
                "analysisTypeName": "calling",
                "program": "foo program",
                "programVersion": "bar version",
                "algorithm": "algogol",
                "sourceName": "foo source",
                "sourceVersion": "bar version",
                "sourceUrl": "http://yyy.xz",
                "referenceId" : "4",
                "referenceName": "foo ref",
                "parameters": {
                    "Prior Probability" : "0.454"
                },
                "createdBy": "1",
                "createdDate": "2019-07-26T04:00:00",
                "modifiedBy": "1",
                "modifiedDate" : "2019-10-29T04:00:00"
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### List Analyses [/gdm/crops/{cropType}/gobii/v3/analyses]

#### Lists Analyses in GDM [GET /gdm/crops/{cropType}/gobii/v3/analyses{?page,pageSize,typeId}]

**Response Body**

Field | Type | Description
------|------|------------
metaData | Object | Metadata with pagination.
result | Object | Result object [BrAPI List Resonse](#brapilistresponsetemplate) where data property has List of [Analyses](#analysesresourceexample) as value

+ Parameters

    + page (optional) - Page number to be fetched based on page size. Default is 0.
    
    + pageSize (optional) - Size of the page to be fetched. Default is 1000.
    
    + typeId (optional) - Filter by Analysis type.

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes

    + Body
    
        {
            "metadata" : {
                "pagination" : {
                    "pageSize" : 2,
                    "currentPage" : 0
                },
            },
            "result" : {
                "data" : [ 
                    {
                        "analysisId": "7",
                        "analysisName": "Test_Calling",
                        "analysisTypeId": "5",
                        "analysisTypeName": "calling",
                        "program": "foo program",
                        "programVersion": "bar version",
                        "algorithm": "algogol",
                        "sourceName": "foo source",
                        "sourceVersion": "bar version",
                        "sourceUrl": "http://yyy.xz",
                        "referenceId" : "4",
                        "referenceName": "foo ref",
                        "parameters": {
                            "Prior Probability" : "0.454"
                        },
                        "createdBy": "1",
                        "createdDate": "2019-07-26T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate" : "2019-10-29T04:00:00"
                    },
                    {
                        "analysisId": "9",
                        "analysisName": "Test_Calling_Adele",
                        "analysisTypeId": "5",
                        "analysisTypeName": "calling",
                        "program": "foo program",
                        "programVersion": "bar version",
                        "algorithm": "algogol",
                        "sourceName": "foo source",
                        "sourceVersion": "bar version",
                        "sourceUrl": "http://yyy.xz",
                        "referenceId" : "4",
                        "referenceName": "foo ref",
                        "parameters": {
                            "Prior Probability" : "0.454"
                        },
                        "createdBy": "1",
                        "createdDate": "2018-07-26T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate" : "2018-10-29T04:00:00"
                    }
                ]
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### Update Analysis by Id [/gdm/crops/{cropType}/gobii/v3/analyses/{analysisId}]

#### Update existing analysis [PATCH]

**Only the fields in the request body will be updated.** <br>

Updating any fields other than the below fields will be ignored by the system.

**Request Body Parameters**

Field | Type | Required/Optioanl
------|------|------------
analysisName | String | *Optional*
analysisTypeId | String | *Optional*. List of predfined analysis type can be obtained by [GET /analyses/types](#/reference/analyses/list-analysis-types). New types can be added using [POST /analyses/types](#/reference/analyses/add-analysis-type)
program | String | *Optional*
programVersion | String | *Optional*
algorithm | String | *Optional*
sourceName | String | *Optional*
sourceVersion | String | *Optional*
sourceUrl | String | *Optional*
refernceId | String | *Optional*. List of reference can be obtained by [GET /references](#referenceresourceexample).
parameters | Object | *Optional* (String:String) Key Value pair.


**Request Body Example**

Assume existing parent resource is as below,

```json

    {
        "analysisId": "7",
        "analysisName": "Test_Calling",
        "analysisTypeId": "5",
        "analysisTypeName": "calling",
        "program": "foo program",
        "programVersion": "bar version",
        "algorithm": "algogol",
        "sourceName": "foo source",
        "sourceVersion": "bar version",
        "sourceUrl": "http://yyy.xz",
        "referenceId" : "4",
        "referenceName": "foo ref",
        "parameters": {
            "Prior Probability" : "0.454"
        },
        "createdBy": "1",
        "createdDate": "2019-07-26T04:00:00",
        "modifiedBy": "1",
        "modifiedDate" : "2019-10-29T04:00:00"
    }

```

If the **patch request body** is as below, 

```json

    {
        /* Updates exising analysisType from calling(with id 5) to haplotypecalling(with id 16) */
        "typeId": "16",
    }
    
```

then the updated parent resource will be as below,

```json

    {
        "analysisId": "7",
        "analysisName": "Test_Calling",
        /* Updated the analysistype*/
        "analysisTypeId": "16",
        "analysisTypeName": "haplotypecalling",
        "program": "foo program",
        "programVersion": "bar version",
        "algorithm": "algogol",
        "sourceName": "foo source",
        "sourceVersion": "bar version",
        "sourceUrl": "http://yyy.xz",
        "referenceId" : "4",
        "referenceName": "foo ref",
        "parameters": {
            "Prior Probability" : "0.454"
        },
        "createdBy": "1",
        "createdDate": "2019-07-26T04:00:00",
        "modifiedBy": "1",
        "modifiedDate" : "2019-10-29T04:00:00"
    }

```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for analyses id 7
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/analyses/7"
  
    # analysis type needs to be updated
    data = {
        "analysisTypeId" : "16"
    }
    
    # Successful creation will have http status code 201
    response = requests.patch(url, 
                                json=data, 
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
    {
        "analysisTypeId": "16", /* Updates the exising analysis type with id 5 with 16 */
    }

+ Response 200

    + Attributes

    + Body

        {
            "analysisId": "7",
            "analysisName": "Test_Calling",
            /* Updated the analysistype*/
            "analysisTypeId": "16",
            "analysisTypeName": "haplotypecalling",
            "program": "foo program",
            "programVersion": "bar version",
            "algorithm": "algogol",
            "sourceName": "foo source",
            "sourceVersion": "bar version",
            "sourceUrl": "http://yyy.xz",
            "referenceId" : "4",
            "referenceName": "foo ref",
            "parameters": {
                "Prior Probability" : "0.454"
            },
            "createdBy": "1",
            "createdDate": "2019-07-26T04:00:00",
            "modifiedBy": "1",
            "modifiedDate" : "2019-10-29T04:00:00"
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### Get Analysis by Id [/gdm/crops/{cropType}/gobii/v3/analyses/{analysisId}]

#### Get existing analysis [GET]

Retrieves the Analysis resource having the specified ID

+ Parameters

    + analysisId (required) - ID of the Analysis to be extracted

+ Request

    + Headers

            Authorization : Bearer 

+ Response 200

    + Attributes
    
    + Body
    
        {
            "result" : {
                "analysisId": "7",
                "analysisName": "Test_Calling",
                "analysisTypeId": "16",
                "analysisTypeName": "haplotypecalling",
                "program": "foo program",
                "programVersion": "bar version",
                "algorithm": "algogol",
                "sourceName": "foo source",
                "sourceVersion": "bar version",
                "sourceUrl": "http://yyy.xz",
                "referenceId" : "4",
                "referenceName": "foo ref",
                "parameters": {
                    "Prior Probability" : "0.454"
                },
                "createdBy": "1",
                "createdDate": "2019-07-26T04:00:00",
                "modifiedBy": "1",
                "modifiedDate" : "2019-10-29T04:00:00"
        }
    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### Delete Analyses by Id [/gdm/crops/{cropType}/gobii/v3/analyses/{analysisId}]

#### Delete existing analysis [DELETE]

Deletes the Analysis resource having the specified ID.

**Response**

If Sucessful(HTTP status code: 204), Method will return empty response body.

If called again, Method will return HTTP status code 404(Not Found).

Returns 409(Conflict), If associated resources Dataset are found.

+ Parameters

    + analysisId (required) - ID of the Analysis to be deleted

+ Request

    + Headers

            Authorization : Bearer 

+ Response 204

    + Attributes
    
    + Body
        
        
    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Associated resources found. Cannot complete the action unless they are deleted."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }
            
+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }



### Add Analysis Type [/gdm/crops/{cropType}/gobii/v3/analyses/types]

#### Creates a new Analysis type [POST]

**Request Body**

Any fields other than the below fields will be ignored by the system.

Field | Type | Required/Optioanl
------|------|------------
typeName | String | **Required** Unique Analysis Type.
typeDescription | String | *Optional* 


**Request Body Example**

```json

    {
        "typeName": "foo_lts_snp_calling",
        "typeDescription": "a new way of doing calling",
    }
    
```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for Analysis type
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/analyses/types"
  
    # Analyses type to be created
    data = {
        "typeName": "foo_lts_snp_calling",
        "typeDescription": "a new way of doing calling",
    }
    
    # Successful update will have http status code 201
    response = requests.post(url,
                                json=data,
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body

        {
            "typeName": "foo_lts_snp_calling",
            "typeDescription": "a new way of doing calling",
        
        }

+ Response 201

    + Attributes

    + Body
        
        {
            "result" : {
                "typeId" : "7",
                "typeName": "foo_lts_snp_calling",
                "typeDescription": "a new way of doing calling",
                "userDefined" : true
            }
        }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### List Analysis Types [/gdm/crops/{cropType}/gobii/v3/analyses/types]

#### Lists analysis types in GDM [GET /gdm/crops/{cropType}/gobii/v3/analyses/types{?page,pageSize}]

**Response Body**

Field | Type | Description
------|------|------------
metaData | Object | Metadata with pagination.
result | Object | Result object [BrAPI List Resonse](#brapilistresponsetemplate) where data property has List of [Analysis Types](#typesresourceexample) as value

+ Parameters

    + page (optional) - Size of the page to be fetched. Default is 1000.
    
    + pageSize (optional) - Page number to be fetched based on page size. Default is 0.

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes

    + Body
    
        {
            "metadata" : {
                "pagination" : {
                    "pageSize" : 2,
                    "currentPage" : 0
                },
            },
            "result" : {
                "data" : [ 
                    {
                        "typeId" : "4",
                        "typeName": "calling",
                        "typeDescription": "snp calling",
                        "userDefined" : false
                    }
                    {
                        "typeId" : "7",
                        "typeName": "foo_lts_snp_calling",
                        "typeDescription": "a new way of doing calling",
                        "userDefined" : true
                    }
                ]
            }
        }
    
+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


## Group Datasets

<a name="datasetresourcefields">**Resource Description**</a>

Below table describes the Dataset Resource with their paramters name, type and
description.

Field | Type | Description
------|------|------------
datasetId | String | Id of the Dataset in GDM.
datasetName | String | Name of the Dataset.
experimentId | String | Id of the experiment in GDM.
experimentName | String | Name of the experiment.
projectId | String | Name of the project in GDM.
projectName | String | Name of the project.
piContactId | String | Id of the PI contact in GDM.
piContactName | String | Name of the PI Contact.
datasetTypeId | String | Id of the Analysis type.
datasetTypeName | String | Name of the Analysis type.
analyses | Array | List of [Analysis](#analysisresourcefields) in dataset.
callingAnalysisId | String | Calling Analysis Id of dataset in GDM>
callingAnalysisName | String| Name of the Calling Analysis 

<a name="datasetresourceexample">**Resource Example**</a>

```json

    {
        "datasetId": "16",
        "datasetName": "foo dataset",
        "experimentId": "7",
        "experimentName": "bar experiment",
        "projectId": "2",
        "projectName": "test project",
        "piContactId": "3",
        "piContactName": "Pecan Pi",
        "datasetTypeId": "4",
        "datasetTypeName": "iupac",
        "analyses": [
            {
                "analysisId": "7",
                "analysisName": "Test_Haplotyper",
                "analysisTypeId": "5",
                "analysisTypeName": "haplotyping",
                "program": "foo program",
                "programVersion": "bar version",
                "algorithm": "algogol",
                "sourceName": "foo source",
                "sourceVersion": "bar version",
                "sourceUrl": "http://yyy.xz",
                "referenceId" : "4",
                "referenceName": "foo ref",
                "parameters": {
                    "Prior Probability" : "0.454"
                },
                "createdBy": "1",
                "createdDate": "2019-07-26T04:00:00",
                "modifiedBy": "1",
                "modifiedDate" : "2019-10-29T04:00:00"
            }
        ],
        "callingAnalysisId": "6",
        "callingAnalysisName": "foo calling"
    }

```

### Create Dataset [/gdm/crops/{cropType}/gobii/v3/datasets]

#### Creates a new dataset [POST]

**Request Body**

Any fields other than the below fields will be ignored by the system.

Field | Type | Required/Optioanl
------|------|------------
datasetName | String | **Required**
experimentId | String | **Required**
callingAnalysisId | String | **Required**. Analysis can be selected from [GET analyses](#/reference/datasets/list-analyses)
datasetTypeId | String | *Optional*. List of dataset types can be selected from [GET /datasets/types](#/reference/datasets/list-dataset-types)
analysisIds | Array(String) | *Optional* Analyses can be selected from [GET analyses](#/reference/datasets/list-analyses)

**Request Body Example**

```json

    {
        "datasetName": "foo dataset",
        "experimentId": "7",
        "datasetTypeId": "4",
        "analysisIds": [
            "17"
        ],
        "callingAnalysisId": "6"
    }

```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for Datasets
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/datasets"
  
    # Dataset to be created
    data = {
        "datasetName": "foo dataset",
        "experimentId": "7",
        "datasetTypeId": "4",
        "analysisIds": [
            "17"
        ],
        "callingAnalysisId": "6"
    }
    
    # Successful creation will have http status code 201
    response = requests.post(url, 
                                json=data, 
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
        {
            "datasetName": "foo dataset",
            "experimentId": "7",
            "datasetTypeId": "4",
            "analysisIds": [
                "17"
            ],
            "callingAnalysisId": "6"
        }

+ Response 201

    + Attributes

    + Body
    
        {
            "result" : {
                "datasetId": "16",
                "datasetName": "foo dataset",
                "experimentId": "7",
                "experimentName": "bar experiment",
                "projectId": "2",
                "projectName": "test project",
                "piContactId": "3",
                "piContactName": "Pecan Pi",
                "datasetTypeId": "4",
                "datasetTypeName": "iupac",
                "analyses": [
                    {
                        "analysisId": "7",
                        "analysisName": "Test_Haplotyper",
                        "analysisTypeId": "5",
                        "analysisTypeName": "haplotyping",
                        "program": "foo program",
                        "programVersion": "bar version",
                        "algorithm": "algogol",
                        "sourceName": "foo source",
                        "sourceVersion": "bar version",
                        "sourceUrl": "http://yyy.xz",
                        "referenceId" : "4",
                        "referenceName": "foo ref",
                        "parameters": {
                            "Prior Probability" : "0.454"
                        },
                        "createdBy": "1",
                        "createdDate": "2019-07-26T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate" : "2019-10-29T04:00:00"
                    }
                ],
                "callingAnalysisId": "6",
                "callingAnalysisName": "foo calling"
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### List Datasets [/gdm/crops/{cropType}/gobii/v3/datasets]

#### Lists Datasets in GDM [GET /gdm/crops/{cropType}/gobii/v3/datasets{?page,pageSize,experimentId,datasetTypeId}]

**Response Body**

Field | Type | Description
------|------|------------
metaData | Object | Metadata with pagination.
result | Object | Result object [BrAPI List Resonse](#brapilistresponsetemplate) where data property has List of [Datasets](#datasetsresourceexample) as value

+ Parameters

    + page (optional) - Page number to be fetched based on page size. Default is 0.
    
    + pageSize (optional) - Size of the page to be fetched. Default is 1000.
    
    + experimentId (optional) - Filter by experiment.
    
    + datasetTypeId (optional) - Filter by Dataset type.

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes

    + Body
    
        {
            "metadata" : {
                "pagination" : {
                    "pageSize" : 2,
                    "currentPage" : 0
                },
            },
            "result" : {
                "data" : [
                    {
                        "datasetId": "16",
                        "datasetName": "foo dataset",
                        "experimentId": "7",
                        "experimentName": "bar experiment",
                        "projectId": "2",
                        "projectName": "test project",
                        "piContactId": "3",
                        "piContactName": "Pecan Pi",
                        "datasetTypeId": "4",
                        "datasetTypeName": "iupac",
                        "analyses": [
                            {
                                "analysisId": "7",
                                "analysisName": "Test_Haplotyper",
                                "analysisTypeId": "5",
                                "analysisTypeName": "haplotyping",
                                "program": "foo program",
                                "programVersion": "bar version",
                                "algorithm": "algogol",
                                "sourceName": "foo source",
                                "sourceVersion": "bar version",
                                "sourceUrl": "http://yyy.xz",
                                "referenceId" : "4",
                                "referenceName": "foo ref",
                                "parameters": {
                                    "Prior Probability" : "0.454"
                                },
                                "createdBy": "1",
                                "createdDate": "2019-07-26T04:00:00",
                                "modifiedBy": "1",
                                "modifiedDate" : "2019-10-29T04:00:00"
                            }
                        ],
                        "callingAnalysisId": "6",
                        "callingAnalysisName": "foo calling"
                    },
                    {
                        "datasetId": "25",
                        "datasetName": "spinach dataset",
                        "experimentId": "34",
                        "experimentName": "olive oyl experiment",
                        "projectId": "61",
                        "projectName": "popeye project",
                        "piContactId": "3",
                        "piContactName": "Pecan Pi",
                        "datasetTypeId": "4",
                        "datasetTypeName": "iupac",
                        "analyses": [
                            {
                                "analysisId": "7",
                                "analysisName": "Test_Haplotyper",
                                "analysisTypeId": "5",
                                "analysisTypeName": "haplotyping",
                                "program": "foo program",
                                "programVersion": "bar version",
                                "algorithm": "algogol",
                                "sourceName": "foo source",
                                "sourceVersion": "bar version",
                                "sourceUrl": "http://yyy.xz",
                                "referenceId" : "4",
                                "referenceName": "foo ref",
                                "parameters": {
                                    "Prior Probability" : "0.454"
                                },
                                "createdBy": "1",
                                "createdDate": "2019-07-26T04:00:00",
                                "modifiedBy": "1",
                                "modifiedDate" : "2019-10-29T04:00:00"
                            }
                        ],
                        "callingAnalysisId": "6",
                        "callingAnalysisName": "foo calling"
                    }

                ]
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### Update Dataset by Id [/gdm/crops/{cropType}/gobii/v3/analyses/{datasetId}]

#### Update existing Dataset [PATCH]

**Only the fields in the request body will be updated.** <br>

Updating any fields other than the below fields will be ignored by the system.

**Request Body Parameters**

Field | Type | Required/Optioanl
------|------|------------
datasetName | String | *Optional*
experimentId | String | *Optional*
callingAnalysisId | String | *Optional*. Analysis can be selected from [GET analyses](#/reference/datasets/list-analyses)
datasetTypeId | String | *Optional*. List of dataset types can be selected from [GET /datasets/types](#/reference/datasets/list-dataset-types)
analysisIds | Array(String) | *Optional*. Updated as one value. Analyses can be selected from [GET analyses](#/reference/datasets/list-analyses)


**Request Body Example**

Assume existing parent resource is as below,

```json

    {
        "datasetId": "16",
        "datasetName": "foo dataset",
        "experimentId": "7",
        "experimentName": "bar experiment",
        "projectId": "2",
        "projectName": "test project",
        "piContactId": "3",
        "piContactName": "Pecan Pi",
        "datasetTypeId": "4",
        "datasetTypeName": "iupac",
        "analyses": [
            {
                "analysisId": "7",
                "analysisName": "Test_Haplotyper",
                "analysisTypeId": "5",
                "analysisTypeName": "haplotyping",
                "program": "foo program",
                "programVersion": "bar version",
                "algorithm": "algogol",
                "sourceName": "foo source",
                "sourceVersion": "bar version",
                "sourceUrl": "http://yyy.xz",
                "referenceId" : "4",
                "referenceName": "foo ref",
                "parameters": {
                    "Prior Probability" : "0.454"
                },
                "createdBy": "1",
                "createdDate": "2019-07-26T04:00:00",
                "modifiedBy": "1",
                "modifiedDate" : "2019-10-29T04:00:00"
            }
        ],
        "callingAnalysisId": "6",
        "callingAnalysisName": "foo calling"
    }

```

If the **patch request body** is as below, 

```json

    {
        /* Updates exising datasetType from iupac(with id 4) to 2_letter_nucleotide(with id 79) */
        "datasetTypeId": "79",
    }
    
```

then the updated parent resource will be as below,

```json

    {
        "datasetId": "16",
        "datasetName": "foo dataset",
        "experimentId": "7",
        "experimentName": "bar experiment",
        "projectId": "2",
        "projectName": "test project",
        "piContactId": "3",
        "piContactName": "Pecan Pi",
        /* Updated the dataset type*/
        "datasetTypeId": "79",
        "datasetTypeName": "2_letter_nucleotide",
        "analyses": [
            {
                "analysisId": "7",
                "analysisName": "Test_Haplotyper",
                "analysisTypeId": "5",
                "analysisTypeName": "haplotyping",
                "program": "foo program",
                "programVersion": "bar version",
                "algorithm": "algogol",
                "sourceName": "foo source",
                "sourceVersion": "bar version",
                "sourceUrl": "http://yyy.xz",
                "referenceId" : "4",
                "referenceName": "foo ref",
                "parameters": {
                    "Prior Probability" : "0.454"
                },
                "createdBy": "1",
                "createdDate": "2019-07-26T04:00:00",
                "modifiedBy": "1",
                "modifiedDate" : "2019-10-29T04:00:00"
            }
        ],
        "callingAnalysisId": "6",
        "callingAnalysisName": "foo calling"
    }

```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for dataset id 7
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/datasets/16"
  
    # dataset type needs to be updated
    data = {
        "datsetTypeId" : "79"
    }
    
    # Successful creation will have http status code 201
    response = requests.patch(url, 
                                json=data, 
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
    {
        "datasetTypeId": "79", /* Updates the exising dataset type with Id 4 with 79 */
    }

+ Response 200

    + Attributes

    + Body

    {
        "datasetId": "16",
        "datasetName": "foo dataset",
        "experimentId": "7",
        "experimentName": "bar experiment",
        "projectId": "2",
        "projectName": "test project",
        "piContactId": "3",
        "piContactName": "Pecan Pi",
        /* Updated the dataset type*/
        "datasetTypeId": "79",
        "datasetTypeName": "2_letter_nucleotide",
        "analyses": [
            {
                "analysisId": "7",
                "analysisName": "Test_Haplotyper",
                "analysisTypeId": "5",
                "analysisTypeName": "haplotyping",
                "program": "foo program",
                "programVersion": "bar version",
                "algorithm": "algogol",
                "sourceName": "foo source",
                "sourceVersion": "bar version",
                "sourceUrl": "http://yyy.xz",
                "referenceId" : "4",
                "referenceName": "foo ref",
                "parameters": {
                    "Prior Probability" : "0.454"
                },
                "createdBy": "1",
                "createdDate": "2019-07-26T04:00:00",
                "modifiedBy": "1",
                "modifiedDate" : "2019-10-29T04:00:00"
            }
        ],
        "callingAnalysisId": "6",
        "callingAnalysisName": "foo calling"
    }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### Get Dataset by Id [/gdm/crops/{cropType}/gobii/v3/datasets/{datasetId}]

#### Get existing dataset [GET]

Retrieves the Dataset resource having the specified ID

+ Parameters

    + datasetId (required) - ID of the Dataset to be extracted

+ Request

    + Headers

            Authorization :  Bearer  

+ Response 200

    + Attributes
    
    + Body
    
        {
            "result" : {
                "datasetId": "16",
                "datasetName": "foo dataset",
                "experimentId": "7",
                "experimentName": "bar experiment",
                "projectId": "2",
                "projectName": "test project",
                "piContactId": "3",
                "piContactName": "Pecan Pi",
                "datasetTypeId": "4",
                "datasetTypeName": "iupac",
                "analyses": [
                    {
                        "analysisId": "7",
                        "analysisName": "Test_Haplotyper",
                        "analysisTypeId": "5",
                        "analysisTypeName": "haplotyping",
                        "program": "foo program",
                        "programVersion": "bar version",
                        "algorithm": "algogol",
                        "sourceName": "foo source",
                        "sourceVersion": "bar version",
                        "sourceUrl": "http://yyy.xz",
                        "referenceId" : "4",
                        "referenceName": "foo ref",
                        "parameters": {
                            "Prior Probability" : "0.454"
                        },
                        "createdBy": "1",
                        "createdDate": "2019-07-26T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate" : "2019-10-29T04:00:00"
                    }
                ],
                "callingAnalysisId": "6",
                "callingAnalysisName": "foo calling"
        }
    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### Delete Datasets by Id [/gdm/crops/{cropType}/gobii/v3/datasets/{datasetId}]

#### Delete existing dataset [DELETE]

Deletes the Dataset resource having the specified ID.

**Response**

If Sucessful(HTTP status code: 204), Method will return empty response body.

If called again, Method will return HTTP status code 404(Not Found).

Returns 409(Conflict), If associated resources Markers / Dnaruns are found.

+ Parameters

    + datasetId (required) - ID of the Dataset to be deleted

+ Request

    + Headers

            Authorization :  Bearer  

+ Response 204

    + Attributes
    
    + Body
        
        
    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Associated resources found. Cannot complete the action unless they are deleted."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }
            
+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }



### Add Dataset Type [/gdm/crops/{cropType}/gobii/v3/datasets/types]

#### Creates a new Datasets type [POST]

**Request Body**

Any fields other than the below fields will be ignored by the system.

Field | Type | Required/Optioanl
------|------|------------
typeName | String | **Required** Unique Dataset Type Name.
typeDescription | String | *Optional* 


**Request Body Example**

```json

    {
        "typeName": "2_letter_nucelotide",
        "typeDescription": "2 letters nucleotide",
    }
    
```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for dataset type
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/datasets/types"
  
    # Dataset type to be created
    data = {
        "typeName": "2_letter_nucelotide",
        "typeDescription": "2 letters nucleotide",
    }
    
    # Successful update will have http status code 201
    response = requests.post(url,
                                json=data,
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body

        {
            "typeName": "2_letter_nucelotide",
            "typeDescription": "2 letters nucleotide"
        }

+ Response 201

    + Attributes

    + Body
        
        {
            "result" : {
                "typeId" : "43",
                "typeName": "2_letter_nucelotide",
                "typeDescription": "2 letters nucleotide",
                "userDefined" : true
            }
        }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### List Dataset Types [/gdm/crops/{cropType}/gobii/v3/datasets/types]

#### Lists dataset types in GDM [GET /gdm/crops/{cropType}/gobii/v3/datasets/types{?page,pageSize}]

**Response Body**

Field | Type | Description
------|------|------------
metaData | Object | Metadata with pagination.
result | Object | Result object [BrAPI List Resonse](#brapilistresponsetemplate) where data property has List of [Dataset Types](#typesresourceexample) as value

+ Parameters

    + page (optional) - Size of the page to be fetched. Default is 1000.
    
    + pageSize (optional) - Page number to be fetched based on page size. Default is 0.

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes

    + Body
    
        {
            "metadata" : {
                "pagination" : {
                    "pageSize" : 2,
                    "currentPage" : 0
                },
            },
            "result" : {
                "data" : [ 
                    {
                        "typeId" : "43",
                        "typeName": "2_letter_nucelotide",
                        "typeDescription": "2 letters nucleotide",
                        "userDefined" : true
                    },
                    {

                        "typeId" : "7",
                        "typeName": "iupac",
                        "typeDescription": "IUPAC",
                        "userDefined" : false
                    }
                ]
            }
        }
    
+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


## Group Mapsets

<a name="mapsetsresourcefields">**Resource Description**</a>

Below table describes the Mapset Resource with their paramters name, type and
description.

Field | Type | Description
------|------|------------
mpsetId | String | Id of the Mapset in GDM.
mapsetName | String | Name of the Mpaset.
mapsetDescription | String | Description of the mapset.
mapsetTypeId | String | Id of the Mapset type.
mapsetTypeName | String | Name of the Mapset type.
referenceId | String | Id of the genome reference for mapset.
referenceName | String | Name fo the genome reference.
createdBy | String | Contact Id of the user who created the contact.
createdDate | String | Created date string in UTC
modifiedBy | String | Contact Id of the user who modified the contact.
modifiedDate | String | Modified date string in UTC

<a name="mapsetsresourceexample">**Resource Example**</a>

```json

    {
        "mapsetId": "16",
        "mapsetName": "foo mapset",
        "mapsetDescription": "mapset desc...",
        "mapsetTypeId": "4",
        "mapsetTypeName": "physical",
        "referenceId" : "6",
        "referenceName": "barrefv7",
        "createdBy": "1",
        "createdDate": "2019-07-26T04:00:00",
        "modifiedBy": "1",
        "modifiedDate" : "2019-10-29T04:00:00",
    }

```

### Create Mapset [/gdm/crops/{cropType}/gobii/v3/mapsets]

#### Creates a new mapset [POST]

**Request Body**

Any fields other than the below fields will be ignored by the system.

Field | Type | Required/Optioanl
------|------|------------
mapsetName | String | **Required**
mapsetDescription | String | *Optional*
mapsetTypeId | String | **Required**. List of mapset types can be selected from [GET /mapsets/types](#/reference/mapsets/list-mapset-types)
referenceId | String | *Optional*.

**Request Body Example**

```json

    {
        "mapsetName": "foo mapset",
        "mapsetDescription": "mapset desc...",
        "mapsetTypeId": "4",
        "referenceId" : "6"
    }

```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for Mapsets
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/mapsets"
  
    # Mapset to be created
    data = {
        "mapsetName": "foo mapset",
        "mapsetDescription": "mapset desc...",
        "mapsetTypeId": "4",
        "referenceId" : "6"
    }
    
    # Successful creation will have http status code 201
    response = requests.post(url, 
                                json=data, 
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
        {
            "mapsetName": "foo mapset",
            "mapsetDescription": "mapset desc...",
            "mapsetTypeId": "4",
            "referenceId" : "6"
        }

+ Response 201

    + Attributes

    + Body
    
        {
            "result" : {
                "mapsetId": "16",
                "mapsetName": "foo mapset",
                "mapsetDescription": "mapset desc...",
                "mapsetTypeId": "4",
                "mapsetTypeName": "physical",
                "referenceId" : "6",
                "referenceName": "barrefv7",
                "createdBy": "1",
                "createdDate": "2019-07-26T04:00:00",
                "modifiedBy": "1",
                "modifiedDate" : "2019-10-29T04:00:00"
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### List Mapsets [/gdm/crops/{cropType}/gobii/v3/mapsets]

#### Lists Mapsets in GDM [GET /gdm/crops/{cropType}/gobii/v3/mapsets{?page,pageSize,mapsetTypeId}]

**Response Body**

Field | Type | Description
------|------|------------
metaData | Object | Metadata with pagination.
result | Object | Result object [BrAPI List Resonse](#brapilistresponsetemplate) where data property has List of [Mapsets](#mapsetsresourceexample) as value

+ Parameters

    + page (optional) - Page number to be fetched based on page size. Default is 0.
    
    + pageSize (optional) - Size of the page to be fetched. Default is 1000.
    
    + mapsetTypeId (optional) - Filter by Mapset type.

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes

    + Body
    
        {
            "metadata" : {
                "pagination" : {
                    "pageSize" : 2,
                    "currentPage" : 0
                },
            },
            "result" : {
                "data" : [
                    {
                        "mapsetId": "16",
                        "mapsetName": "foo mapset",
                        "mapsetDescription": "mapset desc...",
                        "mapsetTypeId": "4",
                        "mapsetTypeName": "physical",
                        "referenceId" : "6",
                        "referenceName": "barrefv7",
                        "createdBy": "1",
                        "createdDate": "2019-07-26T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate" : "2019-10-29T04:00:00"
                    },
                    {
                        "mapsetId": "25",
                        "mapsetName": "bar mapset",
                        "mapsetDescription": "mapset desc 2...",
                        "mapsetTypeId": "4",
                        "mapsetTypeName": "physical",
                        "referenceId" : "6",
                        "referenceName": "barrefv7",
                        "createdBy": "1",
                        "createdDate": "2019-09-26T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate" : "2019-11-29T04:00:00"
                ]
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### Update Mapset by Id [/gdm/crops/{cropType}/gobii/v3/analyses/{mapsetId}]

#### Update existing Mapset [PATCH]

**Only the fields in the request body will be updated.** <br>

Updating any fields other than the below fields will be ignored by the system.

**Request Body Parameters**

Field | Type | Required/Optioanl
------|------|------------
mapsetName | String | **Required**
mapsetDescription | String | *Optional*
mapsetTypeId | String | **Required**. List of mapset types can be selected from [GET /mapsets/types](#/reference/mapsets/list-mapset-types)
referenceId | String | *Optional*.

**Request Body Example**

Assume existing parent resource is as below,

```json

    {
        "mapsetId": "16",
        "mapsetName": "foo mapset",
        "mapsetDescription": "mapset desc...",
        "mapsetTypeId": "4",
        "mapsetTypeName": "physical",
        "referenceId" : "6",
        "referenceName": "barrefv7",
        "createdBy": "1",
        "createdDate": "2019-07-26T04:00:00",
        "modifiedBy": "1",
        "modifiedDate" : "2019-10-29T04:00:00"
    }

```

If the **patch request body** is as below, 

```json

    {
        /* Updates exising mapsetType from physical(with id 4) to genetic(with id 49) */
        "mapsetTypeId": "49",
    }
    
```

then the updated parent resource will be as below,

```json

    {
        "mapsetId": "16",
        "mapsetName": "foo mapset",
        "mapsetDescription": "mapset desc...",
        /* Update Mapset Type */
        "mapsetTypeId": "49",
        "mapsetTypeName": "genetic",
        "referenceId" : "6",
        "referenceName": "barrefv7",
        "createdBy": "1",
        "createdDate": "2019-07-26T04:00:00",
        "modifiedBy": "1",
        "modifiedDate" : "2019-11-29T04:00:00"
    }

```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for mapset id 16
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/mapsets/16"
  
    # mapset type needs to be updated
    data = {
        "datsetTypeId" : "49",
    }
    
    # Successful creation will have http status code 201
    response = requests.patch(url, 
                                json=data, 
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
    {
        "mapsetTypeId": "49", /* Updates the exising mapset type with Id 4 with 49 */
    }

+ Response 200

    + Attributes

    + Body

    {
        "mapsetId": "16",
        "mapsetName": "foo mapset",
        "mapsetDescription": "mapset desc...",
        /* Update Mapset Type */
        "mapsetTypeId": "49",
        "mapsetTypeName": "genetic",
        "referenceId" : "6",
        "referenceName": "barrefv7",
        "createdBy": "1",
        "createdDate": "2019-07-26T04:00:00",
        "modifiedBy": "1",
        "modifiedDate" : "2019-11-29T04:00:00"
    }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### Get Mapset by Id [/gdm/crops/{cropType}/gobii/v3/mapsets/{mapsetId}]

#### Get existing mapset [GET]

Retrieves the Mapset resource having the specified ID

+ Parameters

    + mapsettId (required) - ID of the Mapset to be extracted

+ Request

    + Headers

            Authorization :  Bearer  

+ Response 200

    + Attributes
    
    + Body
    
        {
            "result" : {
                "mapsetId": "16",
                "mapsetName": "foo mapset",
                "mapsetDescription": "mapset desc...",
                "mapsetTypeId": "4",
                "mapsetTypeName": "physical",
                "referenceId" : "6",
                "referenceName": "barrefv7",
                "createdBy": "1",
                "createdDate": "2019-07-26T04:00:00",
                "modifiedBy": "1",
                "modifiedDate" : "2019-10-29T04:00:00"
            }
        }
    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### Delete Mapset by Id [/gdm/crops/{cropType}/gobii/v3/mapsets/{mapsetId}]

#### Delete existing mapset [DELETE]

Deletes the Mapset resource having the specified ID.

**Response**

If Sucessful(HTTP status code: 204), Method will return empty response body.

If called again, Method will return HTTP status code 404(Not Found).

Returns 409(Conflict), If associated resources Markers / Dnaruns are found.

+ Parameters

    + mapsetId (required) - ID of the Mapset to be deleted

+ Request

    + Headers

            Authorization :  Bearer  

+ Response 204

    + Attributes
    
    + Body
        
        
    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Associated resources found. Cannot complete the action unless they are deleted."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }
            
+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### Add Mapset Type [/gdm/crops/{cropType}/gobii/v3/mapsets/types]

#### Creates a new Mapsets type [POST]

**Request Body**

Any fields other than the below fields will be ignored by the system.

Field | Type | Required/Optioanl
------|------|------------
typeName | String | **Required** Unique Mapset Type Name.
typeDescription | String | *Optional* 


**Request Body Example**

```json

    {
        "typeName": "user defined",
        "typeDescription": "new mapset type",
    }
    
```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for mapset type
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/mapsets/types"
  
    # Mapset type to be created
    data = {
        "typeName": "user defined",
        "typeDescription": "new mapset type",
    }
    
    # Successful update will have http status code 201
    response = requests.post(url,
                                json=data,
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body

        {
            "typeName": "user defined",
            "typeDescription": "new mapset type"
        }

+ Response 201

    + Attributes

    + Body
        
        {
            "result" : {
                "typeId" : "74",
                "typeName": "user defined",
                "typeDescription": "new mapset type",
                "userDefined" : true
            }
        }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### List Mapset Types [/gdm/crops/{cropType}/gobii/v3/mapsets/types]

#### Lists mapset types in GDM [GET /gdm/crops/{cropType}/gobii/v3/mapsets/types{?page,pageSize}]

**Response Body**

Field | Type | Description
------|------|------------
metaData | Object | Metadata with pagination.
result | Object | Result object [BrAPI List Resonse](#brapilistresponsetemplate) where data property has List of [Mapset Types](#typesresourceexample) as value

+ Parameters

    + page (optional) - Size of the page to be fetched. Default is 1000.
    
    + pageSize (optional) - Page number to be fetched based on page size. Default is 0.

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes

    + Body
    
        {
            "metadata" : {
                "pagination" : {
                    "pageSize" : 2,
                    "currentPage" : 0
                },
            },
            "result" : {
                "data" : [ 
                    {
                        "typeId" : "4",
                        "typeName": "physical",
                        "typeDescription": "Physical Map",
                        "userDefined" : false
                    },
                    {
                        "typeId" : "74",
                        "typeName": "user defined",
                        "typeDescription": "new mapset type",
                        "userDefined" : true
                    }
                ]
            }
        }
    
+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

## Group Platforms

The platform describes the general chemistry used to generate marker genotyping data. Examples of platforms are KASP, Illumina, etc. All markers must be associated with a platform.

There are several considerations for defining platforms.

- Consistency of data generated: the expectation is that within each platform, the genotyping data generated by a marker name should give generally consistent results to allow for future composite scores by marker. A marker that generates data using a different chemistry can give different results and should be placed in a separate platform.
- Avoiding unnecessary duplication of marker names:  if five different sequencing methods each produce the same or overlapping marker names that give equivalent genotyping scores, they should all be placed under a single platform. For example, "Sequencing," and the five methods described using protocols. 
- Extract requirements: one of the primary extract use-cases is by platform, and so consider how the user will want to extract data; they will likely want to separate gel-based codominant data from sequence generated data.

<a name="platformresourcefields">**Resource Description**</a>

Below table describes the Experiment Resource with their paramters name, type and
description.

Field | Type | Description
------|------|------------
platformId | String | Id of the platform.
platformName | String | Name of the platform.
platformTypeId | String | Id of platform type.
platformTypeName | String | Name of the platform type.
protocolCount | Integer | Number of protocols associated with the platform.
vendorProtocolCount | Integer | Number of vendor protocols associated with the platforms.
experimentCount | Integer | Number of experiments associated with the platform.
markerCount | Integer | Number of Markers associated with the platform.
dnaRunCount | Integer | Number of DnaRuns associated with teh platform.
createdBy | String | Contact Id of the user who created the experiment.
createdDate | String | Created date string in UTC
modifiedBy | String | Contact Id of the user who modified the experiment.
modifiedDate | String | Modified date string in UTC

<a name="platformresourceexample">**Resource Example**</a>

```
        {
            "platformId" : "4",
            "platformName": "foo platform",
            "platformTypeId": "7",
            "platformTypeName" : "bar type",
            "protocolCount": 10,
            "vendorProtocolCount": 100,
            "experimentCount" : 100,
            "markerCount": 10000,
            "dnaRunCount": 4556,
            "createdBy": "1",
            "createdDate": "2018-07-26T04:00:00",
            "modifiedBy": "1",
            "modifiedDate" : "2018-07-29T04:00:00",
        }
        
```

### Create Platform [/gdm/crops/{cropType}/gobii/v3/platforms]

#### Creates a new platform [POST]

**Request Body**

Any fields other than the below fields will be ignored by the system.

Field | Type | Required/Optioanl
------|------|------------
platformName | String | **Required**
platformTypeId | String | **Required** List of mapset types can be selected from [GET /platforms/types](#/reference/platforms/list-platform-types)

**Request Body Example**

```json
    
    {
        "platformName" : "foo platform",
        "platformTypeId" : "7"
    }

```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for Platforms
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/platforms"
  
    # Request Body
    data = {
        "platformName" : "foo platform",
        "platformTypeId" : "7"
    }
    
    # Successful update will have http status code 201
    response = requests.post(url, 
                             json=data,
                             headers= {
                                "Authorization" : "Bearer 123apiKey!"
                            })   

    
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
        {
            "platformName" : "foo platform",
            "platformTypeId" : "7"
        }


+ Response 201

    + Attributes

    + Body
    
        {
            "result" : {
                "platformId" : "4",
                "platformName": "foo platform",
                "platformTypeId": "7",
                "platformTypeName" : "bar type",
                "protocolCount": 0,
                "vendorProtocolCount": 0,
                "experimentCount" : 0,
                "markerCount": 0,
                "dnaRunCount": 0,
                "createdBy": "1",
                "createdDate": "2018-07-26T04:00:00",
                "modifiedBy": "1",
                "modifiedDate" : "2018-07-29T04:00:00",
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### List Platforms [/gdm/crops/{cropType}/gobii/v3/platforms]

#### Lists platform in GDM [GET /gdm/crops/{cropType}/gobii/v3/platforms{?page,pageSize,platformTypeId}]

**Response Body**

Field | Type | Description
------|------|------------
metaData | Object | Metadata with pagination.
result | Object | Result object [BrAPI List Resonse](#brapilistresponsetemplate) where data property has List of [Platforms](#platformresourceexample) as value

+ Parameters

    + page (optional) - Page number to be fetched based on page size. Default is 0.
    
    + pageSize (optional) - Size of the page to be fetched. Default is 1000.

    + platformTypeId (optional) - To filter experiments by Platform Type.

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes

    + Body
    
        {
            "metadata" : {
                "pagination" : {
                    "pageSize" : 2,
                    "currentPage" : 0
                },
            },
            "result" : {
                "data" : [ 
                    {
                        "platformId" : "4",
                        "platformName": "foo platform",
                        "platformTypeId": "7",
                        "platformTypeName" : "bar type",
                        "protocolCount": 0,
                        "vendorProtocolCount": 0,
                        "experimentCount" : 0,
                        "markerCount": 0,
                        "dnaRunCount": 0,
                        "createdBy": "1",
                        "createdDate": "2018-07-26T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate" : "2018-07-29T04:00:00"
                    },
                    {
                        "platformId" : "2",
                        "platformName": "test platform",
                        "platformTypeId": "7",
                        "platformTypeName" : "bar type",
                        "protocolCount": 10,
                        "vendorProtocolCount": 20,
                        "experimentCount" : 30,
                        "markerCount": 50000,
                        "dnaRunCount": 600,
                        "createdBy": "1",
                        "createdDate": "2018-07-26T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate" : "2018-07-29T04:00:00"
                    }
                ]
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### Update Platform by Id [/gdm/crops/{cropType}/gobii/v3/platforms/{platformId}]

#### Update existing platform [PATCH]

**Only the fields in the request body will be updated.** <br>

Updating any fields other than the below fields will be ignored by the system.

**Request Body Parameters**

Field | Type | Required/Optioanl
------|------|------------
platformName | String | *Optional*
platformTypeId | String | *Options* List of platform types can be selected from [GET /platforms/types](#/reference/platforms/list-platform-types)

**Request Body Example**

Assume existing parent resource is as below,

```json

    {
        "platformId" : "4",
        "platformName": "foo platform",
        "platformTypeId": "7",
        "platformTypeName" : "bar type",
        "protocolCount": 10,
        "vendorProtocolCount": 100,
        "experimentCount" : 100,
        "markerCount": 10000,
        "dnaRunCount": 4556,
        "createdBy": "1",
        "createdDate": "2018-07-26T04:00:00",
        "modifiedBy": "1",
        "modifiedDate" : "2018-07-29T04:00:00",
    }

```

If the **patch request body** is as below, 

```json

    {
        /* Updates exising platform type from 7 to 16 */
        "platformTypeId": "16",
    }
    
```

then the updated parent resource will be as below,

```json


    {
        "platformId" : "4",
        "platformName": "foo platform",
        /* Platform type updated from 7 to 16 */
        "platformTypeId": "7",
        "platformTypeName" : "test type",
        "protocolCount": 10,
        "vendorProtocolCount": 100,
        "experimentCount" : 100,
        "markerCount": 10000,
        "dnaRunCount": 4556,
        "createdBy": "1",
        "createdDate": "2018-07-26T04:00:00",
        "modifiedBy": "1",
        "modifiedDate" : "2018-09-29T04:00:00",
    }


```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for platform id 4
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/platforms/4"
  
    # platform type needs to be updated
    data = {
        "platformTypeId" : "16"
    }
    
    # Successful creation will have http status code 201
    response = requests.patch(url, 
                                json=data, 
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
    {
        /* Updates exising platform type from 7 to 16 */
        "platformTypeId": "16",
    }

+ Response 200

    + Attributes

    + Body
    
        {
            "result" : {
                "platformId" : "4",
                "platformName": "foo platform",
                /* Platform type updated from 7 to 16 */
                "platformTypeId": "7",
                "platformTypeName" : "test type",
                "protocolCount": 10,
                "vendorProtocolCount": 100,
                "experimentCount" : 100,
                "markerCount": 10000,
                "dnaRunCount": 4556,
                "createdBy": "1",
                "createdDate": "2018-07-26T04:00:00",
                "modifiedBy": "1",
                "modifiedDate" : "2018-09-29T04:00:00",
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### Get Platform by Id [/gdm/crops/{cropType}/gobii/v3/platforms/{platformId}]

#### Get existing platform [GET]

Retrieves the Platform resource having the specified ID

+ Parameters

    + platformId (required) - ID of the Platform to be extracted

+ Request

    + Headers

            Authorization : Bearer 

+ Response 200

    + Attributes
    
    + Body
    
        {
            "result" : {
                "platformId" : "4",
                "platformName": "foo platform",
                "platformTypeId": "7",
                "platformTypeName" : "test type",
                "protocolCount": 10,
                "vendorProtocolCount": 100,
                "experimentCount" : 100,
                "markerCount": 10000,
                "dnaRunCount": 4556,
                "createdBy": "1",
                "createdDate": "2018-07-26T04:00:00",
                "modifiedBy": "1",
                "modifiedDate" : "2018-09-29T04:00:00",
            }
        }
    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### Delete Platform by Id [/gdm/crops/{cropType}/gobii/v3/platforms/{platformId}]

#### Delete existing platform [DELETE]

Deletes the Platform resource having the specified ID.

**Response**

If Sucessful(HTTP status code: 204), Method will return empty response body.

If called again, Method will return HTTP status code 404(Not Found).

Returns 409(Conflict), If associated resources Marker/Protocol are found.

+ Parameters

    + platformId (required) - ID of the Platform to be deleted.

+ Request

    + Headers

            Authorization :  Bearer  

+ Response 204

    + Attributes
    
    + Body
        
        
    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Associated resources found. Cannot complete the action unless they are deleted."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }
            
+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### Add Platform Type [/gdm/crops/{cropType}/gobii/v3/platforms/types]

#### Creates a new Platforms type [POST]

**Request Body**

Any fields other than the below fields will be ignored by the system.

Field | Type | Required/Optioanl
------|------|------------
typeName | String | **Required** Unique Platform Type Name.
typeDescription | String | *Optional* 


**Request Body Example**

```json

    {
        "typeName": "user defined",
        "typeDescription": "new platform type",
    }
    
```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for platforms type
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/platforms/types"
  
    # Mapset type to be created
    data = {
        "typeName": "user defined",
        "typeDescription": "new platform type",
    }
    
    # Successful update will have http status code 201
    response = requests.post(url,
                                json=data,
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body

        {
            "typeName": "user defined",
            "typeDescription": "new mapset type"
        }

+ Response 201

    + Attributes

    + Body
        
        {
            "result" : {
                "typeId" : "79",
                "typeName": "user defined",
                "typeDescription": "new platform type",
                "userDefined" : true
            }
        }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### List Platform Types [/gdm/crops/{cropType}/gobii/v3/platforms/types]

#### Lists platform types in GDM [GET /gdm/crops/{cropType}/gobii/v3/platforms/types{?page,pageSize}]

**Response Body**

Field | Type | Description
------|------|------------
metaData | Object | Metadata with pagination.
result | Object | Result object [BrAPI List Resonse](#brapilistresponsetemplate) where data property has List of [Platform Types](#typesresourceexample) as value

+ Parameters

    + page (optional) - Size of the page to be fetched. Default is 1000.
    
    + pageSize (optional) - Page number to be fetched based on page size. Default is 0.

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes

    + Body
    
        {
            "metadata" : {
                "pagination" : {
                    "pageSize" : 2,
                    "currentPage" : 0
                },
            },
            "result" : {
                "data" : [ 
                    {
                        "typeId" : "75",
                        "typeName": "platform name",
                        "typeDescription": "platform name for platform",
                        "userDefined" : false
                    },
                    {
                        "typeId" : "79",
                        "typeName": "user defined",
                        "typeDescription": "new platform type",
                        "userDefined" : true
                    }
                ]
            }
        }
    
+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


## Group Protocols

Protocols are grouped within platforms. Protocol defines the specific method that is used to generate the genotyping data for a set of markers within a platform. There could be minor differences in the genotyping data generated by different protocols within the same platform, but the data should not be substantially different for the same marker name. Examples of protocols are different enzymes used to generate GbS data within a sequencing platform.

<a name="protocolsresourcefields">**Resource Description**</a>

Below table describes the Protocol Resource with their paramters name, type and
description.

Field | Type | Description
------|------|------------
protocolId | String | Id of the protocol.
protocolName | String | Name of the protocol.
protocolDescription| String | Description of the protocol.
platformId | String | Id of the platform protocol belongs to.
status | Enum(String) | One of the following status (new, modified, deleted).
createdBy | String | Contact Id of the user who created the contact.
createdDate | String | Created date string in UTC
modifiedBy | String | Contact Id of the user who modified the contact.
modifiedDate | String | Modified date string in UTC

<a name="protocolsresourceexample">**Resource Example**</a>

```json
    {
        "protocolId": "4",
        "protocolName" : "foo",
        "protocolDescription": "foo description",
        "platformId": "7",
        "status": "new",
        "createdBy": "1",
        "createdDate": "2020-04-25T04:00:00",
        "modifiedBy": "1",
        "modifiedDate": "2020-04-25T04:00:00"
    }
```

### Create Protocol [/gdm/crops/{cropType}/gobii/v3/protocols]

#### Creates a new protocol [POST]

**Request Body**

Any fields other than the below fields will be ignored by the system.

Field | Type | Required/Optioanl
------|------|------------
protocolName | String | **Required** 
protocolDescription | String | *Optional*
platformId | String | **Required** List of platforms can be selected from [GET /platforms](#/reference/platforms/list-platforms)


**Request Body Example**

```json
    
    {
        "protocolName" : "foo",
        "protocolDescription": "foo description",
        "platformId": "7"
    }

```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for Protocols
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/protocols"
  
    # Request Body
    data = {
        "protocolName" : "foo",
        "protocolDescription": "foo description",
        "platformId": "7"
    }
    
    # Successful update will have http status code 201
    response = requests.post(url, 
                             json=data,
                             headers= {
                                "Authorization" : "Bearer 123apiKey!"
                            })   

    
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
        {
            "protocolName" : "foo",
            "protocolDescription": "foo description",
            "platformId": "7"
        }

+ Response 201

    + Attributes

    + Body
    
        {
            "result" : {
                "protocolId": "4",
                "protocolName" : "foo",
                "protocolDescription": "foo description",
                "platformId": "7",
                "status": "new",
                "createdBy": "1",
                "createdDate": "2020-04-25T04:00:00",
                "modifiedBy": "1",
                "modifiedDate": "2020-04-25T04:00:00"
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### List Protocols [/gdm/crops/{cropType}/gobii/v3/protocols]

#### Lists protocols in GDM [GET /gdm/crops/{cropType}/gobii/v3/protocols{?page,pageSize,platformId}]

**Response Body**

Field | Type | Description
------|------|------------
metaData | Object | Metadata with pagination.
result | Object | Result object [BrAPI List Resonse](#brapilistresponsetemplate) where data property has List of [Protocols](#protocolsresourceexample) as value

+ Parameters

    + page (optional) - Page number to be fetched based on page size. Default is 0.
    
    + pageSize (optional) - Size of the page to be fetched. Default is 1000.
    
    + platformId (optional) - Filter by Platform.

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes

    + Body
    
        {
            "metadata" : {
                "pagination" : {
                    "pageSize" : 2,
                    "currentPage" : 0
                },
            },
            "result" : {
                "data" : [ 
                    {
                        "protocolId": "4",
                        "protocolName" : "foo",
                        "protocolDescription": "foo description",
                        "platformId": "7",
                        "status": "new",
                        "createdBy": "1",
                        "createdDate": "2020-04-25T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate": "2020-04-25T04:00:00"
                    },
                    {
                        "protocolId": "7",
                        "protocolName" : "bar",
                        "protocolDescription": "bar description",
                        "platformId": "4",
                        "status": "new",
                        "createdBy": "1",
                        "createdDate": "2020-04-16T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate": "2020-04-16T04:00:00"
                    }
                ]
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### Update Protocol by Id [/gdm/crops/{cropType}/gobii/v3/protocols/{protocolId}]

#### Update existing protocol [PATCH]

**Only the fields in the request body will be updated.** <br>

Updating any fields other than the below fields will be ignored by the system.

**Request Body Parameters**

Field | Type | Required/Optioanl
------|------|------------
protocolName | String | *Optional*
protocolDescription | String | *Optional*
platformId | String | *Optional* List of platforms can be selected from [GET /platforms](#/reference/platforms/list-platforms)

**Request Body Example**

Assume existing parent resource is as below,

```json

    {
        "protocolId": "4",
        "protocolName" : "foo",
        "protocolDescription": "foo description",
        "platformId": "7",
        "status": "new",
        "createdBy": "1",
        "createdDate": "2020-04-25T04:00:00",
        "modifiedBy": "1",
        "modifiedDate": "2020-04-25T04:00:00"
    }

```

If the **patch request body** is as below, 

```json

    {
        /* Updates exising protocol description */
        "protocolDescription": "foo updated description",
    }
    
```

then the updated parent resource will be as below,

```json


    {
        "protocolId": "4",
        "protocolName" : "foo",
        "protocolDescription": "foo updated description",
        "platformId": "7",
        "status": "modified",
        "createdBy": "1",
        "createdDate": "2020-04-25T04:00:00",
        "modifiedBy": "1",
        "modifiedDate": "2020-05-25T04:00:00"
    }

```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for protocol id 4
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/protocols/4"
  
    # protocolDescription needs to updated
    data = {
        "protocolDescription" : "foo updated description"
    }
    
    # Successful creation will have http status code 201
    response = requests.patch(url, 
                                json=data, 
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
    {
        "protocolDescription" : "foo updated description" /* Updates the protocol description */
    }

+ Response 200

    + Attributes

    + Body
    
        {
            "result" : {
                "protocolId": "4",
                "protocolName" : "foo",
                /* updated description */
                "protocolDescription": "foo updated description",
                "platformId": "7",
                "status": "modified",
                "createdBy": "1",
                "createdDate": "2020-04-25T04:00:00",
                "modifiedBy": "1",
                "modifiedDate": "2020-05-25T04:00:00"
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### Get Protocol by Id [/gdm/crops/{cropType}/gobii/v3/protocols/{protocolId}]

#### Get existing protocol [GET]

Retrieves the Protocol resource having the specified ID

+ Parameters

    + protocolId (required) - ID of the Protocol to be extracted

+ Request

    + Headers

            Authorization :  Bearer  

+ Response 200

    + Attributes
    
    + Body
    
        {
            "result" : {
                "protocolId": "4",
                "protocolName" : "foo",
                "protocolDescription": "foo updated description",
                "platformId": "7",
                "status": "modified",
                "createdBy": "1",
                "createdDate": "2020-04-25T04:00:00",
                "modifiedBy": "1",
                "modifiedDate": "2020-05-25T04:00:00"
            }
        }
    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### Delete Protocol by Id [/gdm/crops/{cropType}/gobii/v3/protocols/{protocolId}]

#### Delete existing protocol [DELETE]

Deletes the Protocol resource having the specified ID.

**Response**

If Sucessful(HTTP status code: 204), Method will return empty response body.

If called again, Method will return HTTP status code 404(Not Found).

Returns 409(Conflict), If any associated resources like Vendors Protocols are found.

+ Parameters

    + protocolId (required) - ID of the Protocol to be deleted.

+ Request

    + Headers

            Authorization :  Bearer  

+ Response 204

    + Attributes
    
    + Body
        
        
    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Associated resources found. Cannot complete the action unless they are deleted."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }
            
+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }



## Group Genome References

The authoritative genome reference and associated linked files for a physical mapset.

<a name="referenceresourcefields">**Resource Description**</a>

Below table describes the Contacts Resource with their paramters name, type and
description.

Field | Type | Description
------|------|------------
referenceId | String | Id of the references.
referenceName | String | Name of the reference.
version | String | Version of the genome reference.
createdBy | String | Contact Id of the user who created the contact.
createdDate | String | Created date string in UTC
modifiedBy | String | Contact Id of the user who modified the contact.
modifiedDate | String | Modified date string in UTC

<a name="referenceresourceexample">**Resource Example**</a>

```json
    {
        "referenceId": "2",
        "referenceName": "IR64",
        "version": "v8",
        "createdBy": "1",
        "createdDate": "2020-04-25T04:00:00",
        "modifiedBy": "1",
        "modifiedDate": "2020-04-25T04:00:00"
    }
```

### Create Reference [/gdm/crops/{cropType}/gobii/v3/references]

#### Creates a new reference [POST]

**Request Body**

Any fields other than the below fields will be ignored by the system.

Field | Type | Required/Optioanl
------|------|------------
referenceName | String | **Required**.
version | String | **Required**.

**Request Body Example**

```json
    
    {
        "referenceName" : "IR64",
        "version": "v8"
    }

```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for Reference
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/references"
  
    # Request Body
    data = {
        "referenceName" : "IR64",
        "version": "v8"
    }
    
    # Successful update will have http status code 201
    response = requests.post(url, 
                             json=data,
                             headers= {
                                "Authorization" : "Bearer 123apiKey!"
                            })   

    
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
        {
            "referenceName" : "IR64",
            "version": "v8"
        }

+ Response 201

    + Attributes

    + Body
    
        {
            "result" : {
                "referenceId": "2",
                "referenceName": "IR64",
                "version": "v8",
                "createdBy": "1",
                "createdDate": "2020-04-25T04:00:00",
                "modifiedBy": "1",
                "modifiedDate": "2020-04-25T04:00:00"
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### List Reference [/gdm/crops/{cropType}/gobii/v3/references]

#### Lists references in GDM [GET /gdm/crops/{cropType}/gobii/v3/references{?page,pageSize}]

**Response Body**

Field | Type | Description
------|------|------------
metaData | Object | Metadata with pagination.
result | Object | Result object [BrAPI List Resonse](#brapilistresponsetemplate) where data property has List of [References](#referenceresourceexample) as value

+ Parameters

    + page (optional) - Page number to be fetched based on page size. Default is 0.
    
    + pageSize (optional) - Size of the page to be fetched. Default is 1000.

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes

    + Body
    
        {
            "metadata" : {
                "pagination" : {
                    "pageSize" : 2,
                    "currentPage" : 0
                },
            },
            "result" : {
                "data" : [ 
                    {
                        "referenceId": "2",
                        "referenceName": "IR64",
                        "version": "v8",
                        "createdBy": "1",
                        "createdDate": "2020-04-25T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate": "2020-04-25T04:00:00"
                    },
                    {
                        "referenceId": "3",
                        "referenceName": "fooref",
                        "version": "v1",
                        "createdBy": "1",
                        "createdDate": "2020-05-25T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate": "2020-05-25T04:00:00"
                    }
                ]
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### Update Reference by Id [/gdm/crops/{cropType}/gobii/v3/references/{referenceId}]

#### Update existing reference [PATCH]

**Only the fields in the request body will be updated.** <br>

Updating any fields other than the below fields will be ignored by the system.

**Request Body Parameters**

Field | Type | Required/Optioanl
------|------|------------
referenceName | String | **Optional**.
version | String | **Optional**.

**Request Body Example**

Assume existing parent resource is as below,

```json

    {
        "referenceId": "2",
        "referenceName": "IR64",
        "version": "v8",
        "createdBy": "1",
        "createdDate": "2020-04-25T04:00:00",
        "modifiedBy": "1",
        "modifiedDate": "2020-04-25T04:00:00"
    }

```

If the **patch request body** is as below, 

```json

    {
        /* Updates exising reference version */
        "version": "v11",
    }
    
```

then the updated parent resource will be as below,

```json


    {
        "referenceId": "2",
        "referenceName": "IR64",
        /* Updates exising reference version */
        "version": "v11",
        "createdBy": "1",
        "createdDate": "2020-04-25T04:00:00",
        "modifiedBy": "1",
        "modifiedDate": "2020-05-25T04:00:00"
    }

```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for reference with id 2
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/references/2"
  
    # version needs to updated
    data = {
        "version" : "v11"
    }
    
    # Successful creation will have http status code 201
    response = requests.patch(url, 
                                json=data, 
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
    {
        "version": "v11", /* Updates version from v8 to v11 */
    }

+ Response 200

    + Attributes

    + Body
    
        {
            "result" : {
                "referenceId": "2",
                "referenceName": "IR64",
                /* Updates exising reference version */
                "version": "v11",
                "createdBy": "1",
                "createdDate": "2020-04-25T04:00:00",
                "modifiedBy": "1",
                "modifiedDate": "2020-05-25T04:00:00"
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### Get Reference by Id [/gdm/crops/{cropType}/gobii/v3/references/{referenceId}]

#### Get existing reference [GET]

Retrieves the Reference resource having the specified ID

+ Parameters

    + referenceId (required) - ID of the Reference to be extracted

+ Request

    + Headers

            Authorization :  Bearer  

+ Response 200

    + Attributes
    
    + Body
    
        {
            "result" : {
                "referenceId": "2",
                "referenceName": "IR64",
                "version": "v11",
                "createdBy": "1",
                "createdDate": "2020-04-25T04:00:00",
                "modifiedBy": "1",
                "modifiedDate": "2020-05-25T04:00:00"
            }
        }
    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### Delete Reference by Id [/gdm/crops/{cropType}/gobii/v3/references/{referenceId}]

#### Delete existing reference [DELETE]

Deletes the Reference resource having the specified ID.

**Response**

If Sucessful(HTTP status code: 204), Method will return empty response body.

If called again, Method will return HTTP status code 404(Not Found).

Returns 409(Conflict), If associated resources Maps/Markers are found.

+ Parameters

    + referenceId (required) - ID of the Reference to be deleted.

+ Request

    + Headers

            Authorization :  Bearer  

+ Response 204

    + Attributes
    
    + Body
        
        
    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Associated resources found. Cannot complete the action unless they are deleted."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }
            
+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

## Group Markers

### Marker Upload [/gdm/crops/dev/gobii/v3/markers/file-upload]

#### Upload marker file to GDM [POST]

The Request Content-Type is *multipart/form-data* with two feilds, 

* **<h4>fileProperties</h4>** **Required** json object with following fields . Any fields other than below fields will be ignored by the system.

Field | Type | Description
------|------|------------
templateId | String | **Required** Id of the file template to map file columns to gdm system fields. New file templates can be created using POST [/loader-templates/marker](#/reference/templates/add-marker-template/creates-a-marker-template)
platformId | String | **Required** for loading markers, *Optional* if platformName is mapped in input file.
mapId | string | **Required** for loading linkage group and marker positions. *Optional* if mapName is mapped in input file.

* **<h4>markerFile</h4>** **Required** Marker file. Content Type: text/plain. File should be tab(\\t) delimited.

**Python Example**

```python

  import requests
  
  import json
  
  input_file_path = "/home/user/markers.txt"
  
  # URL for Marker Upload
  url = "http://test.gdm.org/gdm/crops/dev/gobii/v3/markers/file-upload"
  
  # File Properties
  file_properties = {
    "mapId": "1",
    "markerTemplateId": "1",
    "platformId": "9"
  }

  #Marker File is added in files object with file properties
  files = { 
  
     "markerFile" : ('', open(input_file_path, 'rb'), 'text/plain'),
     
     "fileProperties" : (None, json.dumps(file_properties), 'application/json')
  
  }
  
  response = requests.post(url,
                           files=files, 
                           headers= {
                                "Authorization" : "Bearer 123apitoken!"
                           })

```

**Form Data Example**

```

POST /gdm/crops/dev/gobii/v3/markers/file-upload HTTP/1.1 
Host: http://test.gdm.org/
Content-Type: multipart/form-data;boundary="boundary" 
Authorization : Bearer 123apitoken!

--boundary 
Content-Disposition: form-data; name="fileProperties"
Content-Type: application/json

        {
            "mapId": "1",
            "markerTemplateId": "1",
            "platformId": "9"
        }
 
--boundary 
Content-Disposition: form-data; Content-Type: text/plain; name="markerFile"; filename="foo.txt" 

Platform\tmarker_name\tchrom\tpos\n
Dart_clone\tDart_clone_1_575\tchr_1\t210517\n
....
Dart_clone\tDart_clone_1_794\tchr_1\t288483\n

--boundary-


```

**Successful Operation** will return a json response with HTTP Status code 202 accepted. The json response will have jobId. The status of the Job can obtained by GET [/jobs/jobId](#/reference/jobs/get-job-by-id?mc=reference%2Fjobs%2Fget-job-by-id%2Fget-existing-job%2F200). Below is the example response,

```json
    
    {
        "result": {
            "jobId": "10",
            "jobName": "e5da41daec854ef78b54ea885fedfa9f",
            "jobMessage": "Submitted marker upload job",
            "payload": "markers",
            "jobType": "load",
            "submittedDate": "2020-10-27T04:17:24",
            "submittedBy": "bond"
        }
    }

```

+ Request (multipart/form-data; boundary=--boundary)

    + Headers

            Authorization :  Bearer

    + Attributes

    + Body
    
        --boundary 
        Content-Disposition: form-data; name="fileProperties"
        Content-Type: application/json

            {
                "mapId": "1",
                "markerTemplateId": "1",
                "platformId": "9"
            }
 
        --boundary 
        Content-Disposition: form-data; Content-Type: text/plain; name="markerFile"; filename="foo.txt" 

        Platform\tmarker_name\tchrom\tpos\n
        Dart_clone\tDart_clone_1_575\tchr_1\t210517\n
        ....
        Dart_clone\tDart_clone_1_794\tchr_1\t288483\n

        --boundary-

+ Response 202

    + Attributes

    + Body
        
        {
            "result": {
                "jobId": "10",
                "jobName": "e5da41daec854ef78b54ea885fedfa9f",
                "jobMessage": "Submitted marker upload job",
                "payload": "markers",
                "jobType": "load",
                "submittedDate": "2020-10-27T04:17:24",
                "submittedBy": "bond"
            }
        }
        
+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }
            

## Group Marker Group

A Marker Group defines a group of markers, the marker platform, and any optional favorable alleles for each marker.

<a name="markergroupresourcefields">**Resource Description**</a>

Below table describes the Marker Group Resource with their paramters name, type and
description.

Field | Type | Description
------|------|------------
markerGroupId | String | Id of the marker group.
markerGroupName | String | Name of the marker group.
germplasmGroup | String | Germplasm group.
createdBy | String | Contact Id of the user who created the marker group.
createdDate | String | Created date string in UTC
modifiedBy | String | Contact Id of the user who modified the marker group.
modifiedDate | String | Modified date string in UTC

<a name="markergroupresourceexample">**Resource Example**</a>

```json

    {
        "markerGroupId": "1",
        "markerGroupName": "foo marker group",
        "germplasmGroup": "bar germplasm group",
        "createdBy": "1",
        "createdDate": "2019-02-25T04:00:00",
        "modifiedBy": "1",
        "modifiedDate": "2019-02-25T04:00:00"
    }
    
```

<a name="markergroupmarkerresourcefields">**Marker Resource Description**</a>

Below table describes the Marker Group Marker Resource with their paramters name, type and
description.

Field | Type | Description
------|------|------------
markerId | String | Id of the marker.
markerName | String | Name of the marker.
platformId | String | Id of the platform
platformName | String | Name of the platform.
favorableAlleles | Array(String) | Allowed characters must be in the following set: {A,C,G,T,+,-,0,1,2,Any 4 digit number (for SSR alleles) (0000 <= x <= 1000)}

<a name="markergroupmarkerresourceexample">**Marker Resource Example**</a>

```json

    {
        "markerId": "4",
        "markerName": "foo marker name",
        "platformId": "34",
        "platformName": "KASP",
        "favorableAlleles": ['G', 'T']
    }
    
```

### Create Marker Groups [/gdm/crops/{cropType}/gobii/v3/markergroups]

#### Creates a new marker group [POST]

**Request Body**

Any fields other than the below fields will be ignored by the system.

Field | Type | Required/Optioanl
------|------|------------
markerGroupName | String | **Required**
germplasmGroup | String | **Optional**.

**Request Body Example**

```json

    {
        "markerGroupName": "foo marker group",
        "germplasmGroup": "bar germplasm group"
    }
    
```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for marker group
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/markergroups"
  
    # Marker Group to be created
    data = {
        "markerGroupName": "foo marker group",
        "germplasmGroup": "bar germplasm group"
    }
    
    # Successful creation will have http status code 201
    response = requests.post(url, 
                                json=data, 
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
        {
            "markerGroupName": "foo marker group",
            "germplasmGroup": "bar germplasm group"
        }

+ Response 201

    + Attributes

    + Body
    
        {
            "result" : {
                "markerGroupId": "1",
                "markerGroupName": "foo marker group",
                "germplasmGroup": "bar germplasm group",
                "createdBy": "1",
                "createdDate": "2019-02-25T04:00:00",
                "modifiedBy": "1",
                "modifiedDate": "2019-02-25T04:00:00"
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### List Marker Groups [/gdm/crops/{cropType}/gobii/v3/markergroups]

#### Lists marker groups in GDM [GET /gdm/crops/{cropType}/gobii/v3/markergroups{?page,pageSize}]

**Response Body**

Field | Type | Description
------|------|------------
metaData | Object | Metadata with pagination.
result | Object | Result object [BrAPI List Resonse](#brapilistresponsetemplate) where data property has List of [Marker Groups](#markergroupsresourceexample) as value

+ Parameters

    + page (optional) - Page number to be fetched based on page size. Default is 0.
    
    + pageSize (optional) - Size of the page to be fetched. Default is 1000.
    
+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes

    + Body
    
        {
            "metadata" : {
                "pagination" : {
                    "pageSize" : 2,
                    "currentPage" : 0
                },
            },
            "result" : {
                "data" : [ 
                    {
                        "markerGroupId": "1",
                        "markerGroupName": "foo marker group",
                        "germplasmGroup": "foo germplasm group",
                        "createdBy": "1",
                        "createdDate": "2019-02-25T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate": "2019-02-25T04:00:00"
                    },
                    {
                        "markerGroupId": "4",
                        "markerGroupName": "bar marker group",
                        "germplasmGroup": "bar germplasm group",
                        "createdBy": "1",
                        "createdDate": "2019-03-25T04:00:00",
                        "modifiedBy": "1",
                        "modifiedDate": "2019-03-25T04:00:00"
                    }

                ]
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### Update Marker Groups by Id [/gdm/crops/{cropType}/gobii/v3/markergroups/{markerGroupId}]

#### Update existing marker group [PATCH]

Updating any fields other than the below fields will be ignored by the system.

**Request Body Parameters**

Field | Type | Required/Optioanl
------|------|------------
markerGroupName | String | **Optional**
germplasmGroup | String | **Optional**.

**Request Body Example**

Assume existing parent resource is as below,

```json

    {
        "markerGroupId": "1",
        "markerGroupName": "foo marker group",
        "germplasmGroup": "bar germplasm group",
        "createdBy": "1",
        "createdDate": "2019-02-25T04:00:00",
        "modifiedBy": "1",
        "modifiedDate": "2019-02-25T04:00:00"
    }

```

If the **patch request body** is as below, 

```json

    {
        /* Updates the marker group name */
        "markerGroupName": "foobar marker group"
    }
    
```

then the updated parent resource will be as below,

```json

    {
        "markerGroupId": "1",    
        /* Updated marker group name */
        "markerGroupName": "foobar marker group",
        "germplasmGroup": "bar germplasm group",
        "createdBy": "1",
        "createdDate": "2019-02-25T04:00:00",
        "modifiedBy": "1",
        "modifiedDate": "2020-02-25T04:00:00"
    }

```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for Marker Group id 1
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/markergroups/1"
  
    # marker group name needs to be updated
    data = {
        "markerGroupName": "foobar marker group"
    }
    
    # Successful creation will have http status code 201
    response = requests.patch(url, 
                                json=data, 
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
    {
        /* Updates the marker group name */
        "markerGroupName": "foobar marker group"
    }

+ Response 200

    + Attributes

    + Body
    
        {
            "result" : {
                "markerGroupId": "1",    
                /* Updated marker group name */
                "markerGroupName": "foobar marker group",
                "germplasmGroup": "bar germplasm group",
                "createdBy": "1",
                "createdDate": "2019-02-25T04:00:00",
                "modifiedBy": "1",
                "modifiedDate": "2020-02-25T04:00:00"
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### Get Marker Group by Id [/gdm/crops/{cropType}/gobii/v3/markergroups/{markerGroupId}]

#### Get existing marker group [GET]

Retrieves the marker group resource having the specified ID

+ Parameters

    + markerGroupId (required) - ID of the Marker Group to be retreived.

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes
    
    + Body
    
        {
            "result" : {
                "markerGroupId": "1",
                "markerGroupName": "foobar marker group",
                "germplasmGroup": "bar germplasm group",
                "createdBy": "1",
                "createdDate": "2019-02-25T04:00:00",
                "modifiedBy": "1",
                "modifiedDate": "2020-02-25T04:00:00"
            }
        }


    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### Delete Marker Group by Id [/gdm/crops/{cropType}/gobii/v3/markergroups/{markerGroupId}]

#### Delete existing marker group [DELETE]

Deletes the Marker Group resource having the specified ID.

**Response**

If Sucessful(HTTP status code: 204), Method will return empty response body.

If called again, Method will return HTTP status code 404.

Returns 409(Conflict), If associated resources are found.

+ Parameters

    + markerGroupId (required) - ID of the Marker Group to be deleted.

+ Request

    + Headers

            Authorization :  Bearer  

+ Response 204

    + Attributes
    
    + Body
        
        
    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Associated resources found. Cannot complete the action unless they are deleted."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }
            
+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### Map Markers to Marker Group [/gdm/crops/{cropType}/gobii/v3/markergroups/{markerGroupId}/markerscollection]

#### Maps list of markers to the marker group [POST]

**Request Body**

List of [Marker Resource](#markergroupmarkerresourcefields). 
**Maximum number of allowed markers in list for a single request is 1000**.

Inserts the whole collecytion or fails for all. No partial inserts will be done.

Any fields other than the below fields in marker resource will be ignored by the system.

Field | Type | Required/Optioanl
------|------|------------
markerName | String | **Required**. Marker name should have been already defined in the system.
platformName | String | **Required**. Platform name should have been already defined in the system.
favorableAlleles | Array(String) | *Optional* Allowed characters must be in the following set: {A,C,G,T,+,-,0,1,2,Any 4 digit number (for SSR alleles) (0000 <= x <= 1000)}

**Request Body Example**

```json
    
    [
        {
            "markerName": "foo marker",
            "platformName": "foo platform",
            "favorableAlleles": ["A"]
        },
        {
            "markerName": "bar marker",
            "platformName": "bar platform",
            "favorableAlleles": ["G"]
        }
    ]
    
```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL to map list of markers to marker group with id 1
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/markergroups/1/markerscollection"
  
    # Marker Group to be created
    data = [
        {
            "markerName": "foo marker",
            "platformName": "foo platform",
            "favorableAlleles": ["A"]
        },
        {
            "markerName": "bar marker",
            "platformName": "bar platform",
            "favorableAlleles": ["G"]
        },
    ]

    # Successful creation will have http status code 201
    response = requests.post(url, 
                                json=data, 
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

**If successful, Returns mapped markers collection in Brapi list response template**

**If failed for bad request with invalid marker object, returns a list of error object with error message.**

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
        
        [
            {
                "markerName": "foo marker",
                "platformName": "foo platform",
                "favorableAlleles": ["A"]
            },
            {
                "markerName": "bar marker",
                "platformName": "bar platform",
                "favorableAlleles": ["G"]
            },
        ]

+ Response 200

    + Attributes

    + Body
    
        {
            "result" : {
                "data" : [
                    {
                        "markerId": "4",
                        "markerName": "foo marker",
                        "platformId": "5",
                        "platformName": "foo platform",
                        "favorableAlleles": ["A"]
                    },
                    {
                        "markerId": "6",
                        "markerName": "bar marker",
                        "platformId": "7",
                        "platformName": "bar platform",
                        "favorableAlleles": ["G"]
                    }
                ]
            }
        }

+ Response 400

    + Attributes

    + Body
    
        [
            {
                "isValid": false,
                "error" : "Bad Request. Marker Name: foo marker does not exist."
            },
            {
                "isValid": true
            }
        ]

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### List Markers in Marker Group [/gdm/crops/{cropType}/gobii/v3/markergroups/1/markers]

#### List markers in marker group in GDM [GET /gdm/crops/{cropType}/gobii/v3/markergroups/1/markers{?page,pageSize}]

**Response Body**

Field | Type | Description
------|------|------------
metaData | Object | Metadata with pagination.
result | Object | Result object [BrAPI List Resonse](#brapilistresponsetemplate) where data property has List of [Markers](#markergroupmarkerresourcefields) as value

+ Parameters

    + page (optional) - Page number to be fetched based on page size. Default is 0.
    
    + pageSize (optional) - Size of the page to be fetched. Default is 1000.
    
+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes

    + Body
    
        {
            "metadata" : {
                "pagination" : {
                    "pageSize" : 2,
                    "currentPage" : 0
                },
            },
            "result" : {
                "data" : [ 
                    {
                        "markerId": "4",
                        "markerName": "foo marker",
                        "platformId": "5",
                        "platformName": "foo platform",
                        "favorableAlleles": ["A"]
                    },
                    {
                        "markerId": "6",
                        "markerName": "bar marker",
                        "platformId": "7",
                        "platformName": "bar platform",
                        "favorableAlleles": ["G"]
                    }
                ]
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

## Group Vendor Protocols

Genotyping protocol used by a vendor.

<a name="vendorprotocolfields">**Resource Description**</a>

Below table describes the Vendor Protocol Resource with their paramters name, type and
description.

Field | Type | Description
------|------|------------
vendorProtocolId | String | Id of vendor protocol.
vendorProtocolName | String | Name of the vendor protocol.
vendorId | String | Id of the vendor.
vendorName | String | Name of the vendor.
protocolId | String | Id of the protocol.
protocolName | String | Name of the protocol.

<a name="vendorprotocolresourceexample">**Resource Example**</a>

```json
    {
        "vendorProtocolId": "7",
        "vendorProtocolName" : "foo vendor protocol",
        "vendorId" : "4",
        "vendorName": "bar vendor",
        "protocolId" : "4",
        "protocolName": "bar protocol"
    }
```

### List Vendor Protocols [/gdm/crops/{cropType}/gobii/v3/vendorprotocols]

#### Lists vendor protocols in GDM [GET /gdm/crops/{cropType}/gobii/v3/vendorprotocols{?page,pageSize}]

**Response Body**

Field | Type | Description
------|------|------------
metaData | Object | Metadata with pagination.
result | Object | Result object [BrAPI List Resonse](#brapilistresponsetemplate) where data property has List of [Vendor Protocols](#vendorprotocolresourceexample) as value

+ Parameters

    + page (optional) - Page number to be fetched based on page size. Default is 0.
    
    + pageSize (optional) - Size of the page to be fetched. Default is 1000.
    

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes

    + Body
    
        {
            "metadata" : {
                "pagination" : {
                    "pageSize" : 2,
                    "currentPage" : 0
                },
            },
            "result" : {
                "data" : [ 
                    {
                        "vendorProtocolId": "7",
                        "vendorProtocolName" : "foo vendor protocol",
                        "vendorId" : "4",
                        "vendorName": "bar vendor",
                        "protocolId" : "4",
                        "protocolName": "bar protocol"
                    },
                    {
                        "vendorProtocolId": "16",
                        "vendorProtocolName" : "bar vendor protocol",
                        "vendorId" : "5",
                        "vendorName": "foo vendor",
                        "protocolId" : "4",
                        "protocolName": "bar protocol"

                    }
                ]
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

## Group Templates

Loader templates are json map objects with fields mapping GDM API fields to the columns in input files.


### Add Marker Template [/gdm/crops/{cropType}/gobii/v3/loader-templates/marker]

#### Creates a marker template [POST]

Below table describes the API fields for marker template. The values will be the respective column names in input file.

Field | Type | Comments
------|------|-------------
markerName | Array | **Required** example: ["marker_name"]
markerRef | Array  | *Optional*
markerAlt | Array | *Optional*
markerSequence | Array | *Optional*
markerStrandName | Array | *Optional*
markerStart | Array | *Optional* **Fails if map and linkagegroup fields are not provided**
markerEnd | Array | *Optional* **Fails if map and linkagegroup fields are not provided**
linkageGroupName | Array | *Optional* **Fails if map is not provided**
linkageGroupStart | Array | *Optional* **Fails if map is not provided**
linkageGroupEnd | Array | *Optional* **Fails if map is not provided**
markerProperties | Object | Object key {propertyName} is a variable which can be obtained using [GET /cvs](#/reference/controlled-vocabulary/list-c-vs/lists-cv-in-gdm) by querying using **cvGroupType** with **"marker_prop"** as value.

**Template**

```json
    {
        "markerName": [],
        "markerRef": [],
        "markerAlt": [],
        "markerStart": [],
        "markerEnd": [],
        "markerSequence": [],
        "markerStrandName": [],
        "linkageGroupName": [],
        "linkageGroupStart": [],
        "linkageGroupEnd": [],
        "markerProperties" : {
           "{propertyName}" : []
        }
    }
```

An exmaple of a template would look like below,

**For an input file: markers.txt, like below **

```
Platform    marker_name chrom   pos
Dart_clone  Dart_clone_1_575    chr_1   210517
Dart_clone  Dart_clone_1_794    chr_1   288483
Dart_clone  Dart_clone_1_438    chr_1   649969
Dart_clone  Dart_clone_1_1004   chr_1   818878
Dart_clone  Dart_clone_1_856    chr_1   1372476
Dart_clone  Dart_clone_1_153    chr_1   2347693
Dart_clone  Dart_clone_1_604    chr_1   3155524
Dart_clone  Dart_clone_1_111    chr_1   3449357
```

**Template file would look like below,**

```json

    {
        "markerName": ["marker_name"],
        "linkageGroupName": ["chrom"],
        "markerStart": ["pos"],
        "markerStop": ["pos"]
    }

```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
    {
        "templateName": "marker_template1",
        "template": {
            "markerName": ["marker_name"],
            "linkageGroupName": ["chrom"],
            "markerStart": ["pos"],
            "markerStop": ["pos"]
        }
    }

+ Response 201

    + Attributes

    + Body
    
        {
            "result" : {
                "templateId": "1",
                "templateName": "marker_test_template_1",
                "templateType": "markers",
                "template": {
                    "markerName": [
                        "marker_name"
                    ],
                    "markerStart": [
                        "marker_linkage_group_start"
                    ],
                    "linkageGroupName": [
                        "linkage_group_name"
                    ]
                },
                "createdBy": "5",
                "createdDate": "2020-10-26T03:25:31",
                "modifiedBy": "5",
                "modifiedDate": "2020-10-26T03:25:31"
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### Get Marker Template [/gdm/crops/{cropType}/gobii/v3/loader-templates/marker]

#### Returns empty marker template to be filled [GET]

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes
    
    + Body
    
        {
            "markerName": [],
            "markerRef": [],
            "markerAlt": [],
            "markerStart": [],
            "markerEnd": [],
            "markerSequence": [],
            "markerStrandName": [],
            "linkageGroupName": [],
            "linkageGroupStart": [],
            "linkageGroupEnd": [],
            "markerProperties" : {
                "{propertyName}" : []
            }
        }
    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


## Group Jobs

Jobs represents the jobs submitted to the system by loader or an extractor.

<a name="jobssresourcefields">**Resource Description**</a>

Below table describes the Jobs Resource with their paramters name, type and
description.

Field | Type | Description
------|------|------------
jobId | String | Id of the job.
jobName | String | Name of the job
jobMessage | String | Status of the Job
payload | String | type of data submitted to load or extractor. example: marker/samples
jobType | String | Type of the job, load or extract or qc
submittedDate | TimeStamp | Date time at which the job was submitted
submittedBy | String | User name of the person who submitted the job

<a name="cropsresourceexample">**Resource Example**</a>

```json
   {
        "jobId": "10",
        "jobName": "e5da41daec854ef78b54ea885fedfa9f",
        "jobMessage": "Submitted marker upload job",
        "payload": "markers",
        "jobType": "load",
        "submittedDate": "2020-10-27T04:17:24",
        "submittedBy": "jobber"
    }
```

### Get Job by Id [/gdm/crops/{cropType}/gobii/v3/jobs/{jobId}]

#### Get existing job [GET]

Retrieves the job resourc given ID

+ Parameters

    + jobId (required) - ID of the job to be retreived.

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes
    
    + Body
    
        {
            "result" : {
                "jobId": "10",
                "jobName": "e5da41daec854ef78b54ea885fedfa9f",
                "jobMessage": "Submitted marker upload job",
                "payload": "markers",
                "jobType": "load",
                "submittedDate": "2020-10-27T04:17:24",
                "submittedBy": "jobber"
            }
        }


    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


## Group Controlled Vocabulary

Edit property fields and type fields in the Controlled Vocabulary tables. Property fields are identified by the suffix "_prop." These are additional, user-defined fields associated with database tables. Type fields are identified by the suffix "_type." These control the entries being loaded to a field and require an exact match. 

<a name="cvsresourcefields">**Resource Description**</a>

Below table describes the CV Resource with their paramters name, type and
description.

Field | Type | Description
------|------|------------
cvId | String | Id of the CV in GDM.
cvName | String | Name of the CV in GDM.
cvDescription | String | Description of CV.
cvGroupId | String | Id of the CV group CV belongs to.
cvGroupName | String | Name of the CV group CV belongs to.
cvGroupType | Enum(String) | Enum of (SYSTEM_DEFINED | USER_DEFINED).
cvStatus | Enum(String) | Status of CV. (new | modified | deleted)
properties | Array | Array of properties object with property values for given entity. Set of predefined property id and name can be obtained using [GET /cv/properties](#/reference/controlled-vocabulary/list-cv-properties). New properties for the project can be defined using [POST /cv/properties](#/reference/controlled-vocabulary/create-cv-property).

<a name="cvsresourceexample">**Resource Example**</a>

```json
    {
        "cvId": "4",
        "cvName" : "fooTerm",
        "cvDesciption": "purpose of foo",
        "cvGroupId": "7",
        "cvGroupName": "bar group",
        "cvStatus": "new",
        "cvGroupType": "SYSTEM_DEFINED",
        "properties": [
            {
                "propertyId" : "16",
                "propertyName" : "aprop",
                "propertyValue" : "apropValue",
                "propertyDescription" : "testing...",
                "propertyGroupId" : "4",
                "propertyGroupName" : "cv_prop"
            },
        ]
    }
```

### Create CV [/gdm/crops/{cropType}/gobii/v3/cvs]

#### Creates a new cv [POST]

**Request Body**

Any fields other than the below fields will be ignored by the system.

Field | Type | Required/Optioanl
------|------|------------
cvName | String | **Required**
cvDescription | String | **Required**
cvGroupId | String | **Required**
properties | Array | *Optional*. Array of properties object with property values for given entity. Set of predefined property id and name can be obtained using [GET /cv/properties](#/reference/controlled-vocabulary/list-cv-properties). New properties for the project can be defined using [POST /cv/properties](#/reference/controlled-vocabulary/create-cv-property).

**Request Body Example**

```json

    {
        "cvName" : "fooTerm",
        "cvDesciption": "purpose of foo",
        "cvGroupId": "7",
        "properties": [
            {
                "propertyId" : "16",
                "propertyValue" : "apropValue"
            },
        ]
    }
    
```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for CVs
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/cvs"
  
    # CV to be created
    data = {
        "cvName" : "fooTerm",
        "cvDesciption": "purpose of foo",
        "cvGroupId": "7",
        "properties": [
            {
                "propertyId" : "16",
                "propertyValue" : "apropValue"
            },
        ]
    }
    
    # Successful creation will have http status code 201
    response = requests.post(url, 
                                json=data, 
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
        {
            "cvName" : "fooTerm",
            "cvDesciption": "purpose of foo",
            "cvGroupId": "7",
            "properties": [
                {
                    "propertyId" : "16",
                    "propertyValue" : "apropValue"
                },
            ]
        }

+ Response 201

    + Attributes

    + Body
    
        {
            "result" : {
                "cvId": "4",
                "cvName" : "fooTerm",
                "cvDesciption": "purpose of foo",
                "cvGroupId": "7",
                "cvGroupName": "bar group",
                "cvStatus": "new",
                "cvGroupType": "USER_DEFINED",
                "properties": [
                    {
                        "propertyId" : "16",
                        "propertyName" : "aprop",
                        "propertyValue" : "apropValue",
                        "propertyDescription" : "testing...",
                        "propertyGroupId" : "4",
                        "propertyGroupName" : "cv_prop"
                    },
                ]
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### List CVs [/gdm/crops/{cropType}/gobii/v3/cvs]

#### Lists cv in GDM [GET /gdm/crops/{cropType}/gobii/v3/cvs{?page,pageSize,cvGroupName,cvGroupType}]

**Response Body**

Field | Type | Description
------|------|------------
metaData | Object | Metadata with pagination.
result | Object | Result object [BrAPI List Resonse](#brapilistresponsetemplate) where data property has List of [CVs](#cvsresourceexample) as value

+ Parameters

    + page (optional) - Page number to be fetched based on page size. Default is 0.
    
    + pageSize (optional) - Size of the page to be fetched. Default is 1000.
    
    + cvGroupName (optional) - Filter CVs by cv group name.
    
    + cvGroupType (optional) - Filter CVs by cv group type.

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes

    + Body
    
        {
            "metadata" : {
                "pagination" : {
                    "pageSize" : 2,
                    "currentPage" : 0
                },
            },
            "result" : {
                "data" : [ 
                    {
                        "cvId": "4",
                        "cvName" : "fooTerm",
                        "cvDesciption": "purpose of foo",
                        "cvGroupId": "7",
                        "cvGroupName": "bar group",
                        "cvStatus": "new",
                        "cvGroupType": "SYSTEM_DEFINED",
                        "properties": [
                            {
                                "propertyId" : "16",
                                "propertyName" : "aprop",
                                "propertyValue" : "apropValue",
                                "propertyDescription" : "testing...",
                                "propertyGroupId" : "4",
                                "propertyGroupName" : "cv_prop"
                            },
                        ]
                    },
                    {
                        "cvId": "5",
                        "cvName" : "foobarTerm",
                        "cvDesciption": "purpose of foobar",
                        "cvGroupId": "12",
                        "cvGroupName": "barfoo group",
                        "cvStatus": "new",
                        "cvGroupType": "USER_DEFINED",
                        "properties": [
                            {
                                "propertyId" : "16",
                                "propertyName" : "aprop",
                                "propertyValue" : "apropValue2",
                                "propertyDescription" : "testing...",
                                "propertyGroupId" : "4",
                                "propertyGroupName" : "cv_prop"
                            },
                        ]
                    }
                ]
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

### Update CV by Id [/gdm/crops/{cropType}/gobii/v3/cvs/{cvId}]

#### Update existing cv [PATCH]

**Only the fields in the request body will be updated.** <br>

**To add new property** - *Add the property object with propertyId and propertyValue* <br>
**To delete a property** - *Make the propertyValue as null* <br>
**To update existing property** - *Add the property object with new value*

Updating any fields other than the below fields will be ignored by the system.

**Request Body Parameters**

Field | Type | Required/Optioanl
------|------|------------
cvName | String | **Optional**
cvDescription | String | **Optional**
cvGroupId | String | **Optional**
properties | Array | *Optional* Array of properties object with property values for given entity. Set of predefined property id and name can be obtained using [GET /cv/properties](#/reference/controlled-vocabulary/list-cv-properties). New properties for the project can be defined using [POST /cv/properties](#/reference/controlled-vocabulary/create-cv-property).

**Request Body Example**

Assume existing parent resource is as below,

```json

    {
        "cvId": "4",
        "cvName" : "fooTerm",
        "cvDesciption": "purpose of foo",
        "cvGroupId": "7",
        "cvGroupName": "bar group",
        "cvStatus": "new",
        "cvGroupType": "SYSTEM_DEFINED",
        "properties": [
            {
                "propertyId" : "16",
                "propertyName" : "aprop",
                "propertyValue" : "apropValue",
                "propertyDescription" : "testing...",
                "propertyGroupId" : "4",
                "propertyGroupName" : "cv_prop"
            },
        ]
    }

```

If the **patch request body** is as below, 

```json

    {
        /* Replaces the existing cvgroup 7 with 9 */
        "cvGroupId": "9",
    }
    
```

then the updated parent resource will be as below,

```json

    {
        "cvId": "4",
        "cvName" : "fooTerm",
        "cvDesciption": "purpose of foo",
        "cvGroupId": "9",
        /* Updated the CV group */
        "cvGroupName": "barsoap group",
        "cvStatus": "new",
        "cvGroupType": "SYSTEM_DEFINED",
        "properties": [
            {
                "propertyId" : "16",
                "propertyName" : "aprop",
                "propertyValue" : "apropValue",
                "propertyDescription" : "testing...",
                "propertyGroupId" : "4",
                "propertyGroupName" : "cv_prop"
            },
        ]
    }

```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for CV with id 4
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/cvs/4"
  
    # CV group needs to be updated
    data = {
        "cvGroupId" : "9",
    }
    
    # Successful creation will have http status code 201
    response = requests.patch(url, 
                                json=data, 
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body
    
    {
        "cvGroupId" : "9" /* Replaces the existing cv group 7 with 9 */
    }

+ Response 200

    + Attributes

    + Body
    
        {
            "result" : {
                "cvId": "4",
                "cvName" : "fooTerm",
                "cvDesciption": "purpose of foo",
                "cvGroupId": "9",
                /* Updated the CV group */
                "cvGroupName": "barsoap group",
                "cvStatus": "new",
                "cvGroupType": "SYSTEM_DEFINED",
                "properties": [
                    {
                        "propertyId" : "16",
                        "propertyName" : "aprop",
                        "propertyValue" : "apropValue",
                        "propertyDescription" : "testing...",
                        "propertyGroupId" : "4",
                        "propertyGroupName" : "cv_prop"
                    },
                ]
            }
        }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### Get CV by Id [/gdm/crops/{cropType}/gobii/v3/cvs/{cvId}]

#### Get existing cv [GET]

Retrieves the CV resource having the specified ID

+ Parameters

    + cvId (required) - ID of the controlled vocabulary to be extracted

+ Request

    + Headers

            Authorization : Bearer  

+ Response 200

    + Attributes
    
    + Body
    
        {
            "result" : {
                "cvId": "4",
                "cvName" : "fooTerm",
                "cvDesciption": "purpose of foo",
                "cvGroupId": "9",
                "cvGroupName": "barsoap group",
                "cvStatus": "new",
                "cvGroupType": "SYSTEM_DEFINED",
                "properties": [
                    {
                        "propertyId" : "16",
                        "propertyName" : "aprop",
                        "propertyValue" : "apropValue",
                        "propertyDescription" : "testing...",
                        "propertyGroupId" : "4",
                        "propertyGroupName" : "cv_prop"
                    },
                ]
            }
        }


    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### Delete CV by Id [/gdm/crops/{cropType}/gobii/v3/cvs/{cvId}]

#### Delete existing cv [DELETE]

Deletes the CV resource having the specified ID.

**Response**

If Sucessful(HTTP status code: 204), Method will return empty response body.

If called again, Method will return HTTP status code 404.

Returns 409(Conflict), If associated resources like Experiment/Samples found.

+ Parameters

    + cvId (required) - ID of the CV to be deleted.

+ Request

    + Headers

            Authorization :  Bearer  

+ Response 204

    + Attributes
    
    + Body
        
        
    
+ Response 404

    + Attributes
    
    + Body
    
        {
            "error" : "Not Found"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Associated resources found. Cannot complete the action unless they are deleted."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }
            
+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### Create CV Property [/gdm/crops/{cropType}/gobii/v3/cvs/properties]

#### Creates new cv property [POST]

**Request Body**

Any fields other than the below fields will be ignored by the system.

Field | Type | Required/Optioanl
------|------|------------
propertyName | String | **Required** Unique property name.
propertyDescription | String | *Optional* 


**Request Body Example**

```json

    {
        "propertyName": "cvProp",
        "propertyDescription": "cvPropDesc",
    }
    
```

**Python Example**

```python
   
    import requests
  
    import json
  
    # URL for CV Property
    url = "http://test.gdm.org/gdm/crops/{cropType}/gobii/v3/cvs/properties"
  
    # CV Property to be created
    data = {
        "propertyName": "cvProp",
        "propertyDescription": "cvPropDesc",
    }
    
    # Successful update will have http status code 201
    response = requests.post(url,
                                json=data,
                                headers= {
                                    "Authorization" : "Bearer 123apiKey!"
                                }
                            )   
   
```

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 
    
    + Body

        {
            "propertyName": "cvProp",
            "propertyDescription": "cvPropDesc",
        }

+ Response 201

    + Attributes

    + Body
        
        {
            "result" : {
                "propertyId" : "79",
                "propertyName": "cvProp",
                "propertyDescription": "cvPropDesc",
                "propertyGroupId" : "4",
                "propertyGroupName" : "cv_prop",
                "propertyType" : "USER_DEFINED"
            }
        }

+ Response 409

    + Attributes
    
    + Body
    
        {
            "error" : "Entity already exists."
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }


### List CV Properties [/gdm/crops/{cropType}/gobii/v3/cvs/properties]

#### Lists cv properties in GDM [GET /gdm/crops/{cropType}/gobii/v3/cvs/properties{?page,pageSize}]

Property Fields are additional information related to CVs. There are system defined properties whcih are predefined. Additional properties can be defined by the users. 

**Response Body**

Field | Type | Description
------|------|------------
metaData | Object | Metadata with pagination.
result | Object | Result object [BrAPI List Resonse](#brapilistresponsetemplate) where data property has List of [CV Property](#propertyresourceexample) as value

+ Parameters

    + page (optional) - Size of the page to be fetched. Default is 1000.
    
    + pageSize (optional) - Page number to be fetched based on page size. Default is 0.

+ Request

    + Headers

            Authorization: Bearer {{AccessToken}} 

+ Response 200

    + Attributes

    + Body
    
        {
            "metadata" : {
                "pagination" : {
                    "pageSize" : 2,
                    "currentPage" : 0
                },
            },
            "result" : {
                "data" : [ 
                    {
                        "propertyId" : "16",
                        "propertyName" : "aprop",
                        "propertyDescription" : "testing...",
                        "propertyGroupId" : "4",
                        "propertyGroupName" : "cv_prop",
                        "propertyType" : "SYSTEM_DEFINED"
                    },
                    {
                        "propertyId" : "79",
                        "propertyName": "cvProp",
                        "propertyDescription": "cvPropDesc",
                        "propertyGroupId" : "4",
                        "propertyGroupName" : "cv_prop",
                        "propertyType" : "USER_DEFINED"
                    }
                ]
            }
        }
    
+ Response 400

    + Attributes

    + Body
    
        {
            "error" : "Bad Request. Missing required field or Invalid Type"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 401

    + Attributes

    + Body
    
        {
            "error" : "Unauthorized credentials. Please use valid credential or api token"
        }

    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

+ Response 500

    + Attributes
    
    + Body
    
        {
            "error" : "Internal server error"
        }


    + Schema

            {
              "type": "object",
              "properties": {
                "error": {
                  "type": "string"
                }
              }
            }

## Group GDM Properties

<a name="propertyresourcefields">**Resource Description**</a>

Below table describes the Property Resource with their paramters name, type and
description.

Field | Type | Description
------|------|------------
propertyId | String | Id of the Property in GDM.
propertyName | String | Name of the Property. Uniquely identifies the property
propertyDescription | String | Description of .
propertyGroupId | String | Id of the property group peroperty belongs to.
propertyGroupName | String | Name of the property group property belongs to.
propertyType | String | System defined property or user defined.

<a name="propertyresourceexample">**Resource Example**</a>

```json

    {
        "propertyId" : "7",
        "propertyName": "division",
        "propertyDescription": "division to which project belongs to",
        "propertyGroupId" : "4",
        "propertyGroupName" : "project_prop",
        "propertyType" : "user defined"
    }
    
```

## Group GDM Types

<a name="typesresourcefields">**Type Resource Description**</a>

Below table describes the types resource with their paramters name, type and
description.

Field | Type | Description
------|------|------------
typeId | String | Id of Analysis type in GDM.
typeName | String | Name of the analysis type name.
typeDescription | String | Description of Analysis type.
userDefined | Boolean | True, If added by the user. False, If system defined.

<a name="typesresourceexample">**Type Resource Example**</a>

```json

    {
        "typeId" : "7",
        "typeName": "foo_lts_snp_calling",
        "typeDescription": "a new way of doing calling",
        "userDefined" : true

    }

```


