The GDM web service API provides a comprehensive set of RESTful methods for accomplishing the following core tasks in the GOBii Genomic Data Management (GDM) system.
This is API reference documentation for GDM Implementation of BrAPI Genotyping API specification version 2.0.

Below are links to other GDM API references,

* [GDM BrAPI v1 API reference](https://gdm2.docs.apiary.io/reference/brapi)
* [GDM API v1 reference](https://gdm2.docs.apiary.io/)
* [BrAPI Genotyping v2.0 specification](https://brapigenotyping.docs.apiary.io/)

**BrAPI GDM terminologies Map**

BrAPI Term | GDM/General Term
-----------|------------------
CallSet    | DnaRun
Variant    | Marker/SNP
VariantSet | Dataset
Sample     | Dna Sample
Germplasm  | Germplasm
Study      | Experiment
germpalasmPUI | Germplasm External Code
Calls      | Genotypes
Genome Map | Mapset

Postman Collection URL: https://www.getpostman.com/collections/973110d1a1f89af7eaec

API Response will follow the same structure as BrAPI template below,
Pagination follows zero based index(same as BrAPI).

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

