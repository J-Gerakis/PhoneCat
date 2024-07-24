## Dev Notes ##

### Tech ###

Developed with Java 21, Maven, Spring Boot 3.0, Hibernate, and Liquibase.

### Database ###

I chose H2, because of it small size and because of the scope of the project (unlikely to have more than a hundred phones) There are 2 tables.

The first is _Phone_ and store phone status

| Field             | Type    |
|-------------------|---------|
| phoneId           | bigint  |
| brand             | varchar |
| model             | varchar |
| is_available      | boolean |
| borrower_username | varchar |
| borrow_date       | date    |
| spec_ref_id       | bigint  |

The second is _SpecRef_ and store technical specification

| Field       | Type    |
|-------------|---------|
| spec_ref_if | bigint  |
| brand       | varchar |
| model       | varchar |
| technology  | varchar |
| _2g_bands   | varchar |
| _3g_bands   | varchar |
| _4g_bands   | varchar |

The purpose of this table is to avoid repeating external API calls. So when a new phone is introduced to the catalog, a call to FonoAPI is made once and the result stored. When a phone's details is requested, the repository makes a Join between the 2 tables on the field _spec_ref_id_. The repetition of the brand and model information is to facilitate search and update operation.

### FonoAPI ###

As mentioned above, I aimed to minimize the number of calls by storing the result. FonoAPI url and token are stored in _application.properties_ and there's also a switch _fonoapi.active_ that allow to deactivate the call and only use user provided values for the technical specifications. This feature proved necessary, as it seems FonoAPI has been down for at least 4 years.

### Controllers and Endpoints ###

#### Booking Controller ####

Handles booking and returning a phone. There are two endpoints:

http://localhost:8080/phonecat/v1/bookings/book

input:
```
{
    "phone_id": 2,
    "username": "gerakis"
}
```

output:
```
{
    "phone_id": 2,
    "borrower_username": "gerakis",
    "borrow_date": "2024-05-23T14:44:49.806848800",
    "confirmed": true,
    "note": "booking confirmed"
}
```
The _confirmed_ field indicate the validity of the booking and the _note_ field can have different message depending on the outcome:
``
"booking confirmed";
"phone unavailable";
"phone returned";
"phone not found";
``

http://localhost:8080/phonecat/v1/bookings/return/{phoneId}

input:
```
    phoneId (path parameter)
```

output:
```
{
  "phone_id": 1,
  "borrower_username": "",
  "borrow_date": null,
  "confirmed": true,
  "note": "phone returned"
}
```

#### Catalog Controller ####

Handle everything related to updating the catalog of phone and their technical specification

http://localhost:8080/phonecat/v1/phones/add

input:
```
{
"brand":"Samsung",
"model":"Z3",
"technology":"some tech",
"_2g_bands":"2G",
"_3g_bands":"3G",
"_4g_bands":"4G"
}
```
Only _brand_ and _model_ are mandatory parameters, the rest are for the user to manually supply technical specifications when not relying on FonoApi.

output:
```
{
  "message": "Phone added to catalog",
  "entity_id": 11
}
```
note: _entity_id_ refer to the new phone's id.

http://localhost:8080/phonecat/v1/phones/{phoneId}

input:
```
    phoneId (path parameter)
```

output (example):
```
{
  "phone_id": 2,
  "brand": "Samsung",
  "model": "Galaxy S8",
  "is_available": true,
  "borrower_username": null,
  "borrow_date": null,
  "spec_ref": {
    "spec_ref_id": 4
    "technology": "ss8T",
    "_2g_bands": "2g",
    "_3g_bands": "2g",
    "_4g_bands": "2g"
  }
}
```
Return a single phone's detail.

http://localhost:8080/phonecat/v1/phones/list

input:
```
   (path parameters)
   brand
   model
   isAvailable
```
output:
```
[
  {
    "phone_id": 3,
    "brand": "Samsung",
    "model": "Galaxy S8",
    "is_available": true,
    "borrower_username": null,
    "borrow_date": null,
    "spec_ref": {
        "spec_ref_id": 4
        "technology": "ss8T",
        "_2g_bands": "2g",
        "_3g_bands": "2g",
        "_4g_bands": "2g"
    }
  },
  {
    "phone_id": 10,
    "brand": "Nokia",
    "model": "3310",
    "is_available": true,
    "borrower_username": null,
    "borrow_date": null,
    "spec_ref": {
        "spec_ref_id": 3
        "technology": "old",
        "_2g_bands": "2g",
        "_3g_bands": "3g",
        "_4g_bands": "4g"
    }
  },
  {
    "phone_id": 11,
    "brand": "Samsung",
    "model": "Z3",
    "is_available": true,
    "borrower_username": "",
    "borrow_date": null,
    "spec_ref": {
        "spec_ref_id": 5
        "technology": "some tech",
        "_2g_bands": "2G",
        "_3g_bands": "2G",
        "_4g_bands": "2G"
    }
  }
]
```
Return a filtered list of the phone catalog

http://localhost:8080/phonecat/v1/phones/newSpecification

input:
```
{
"brand": "Nokia",
"model": "3310",
"technology": "old",
"_2g_bands": "2g",
"_3g_bands": "3g",
"_4g_bands": "4g"
}
```
output:
```
{
"message": "Updated specification",
"entity_id": 3
}
```
Allow to manually input or update a technical specification for a brand/model combination. The _entity_id_ returned is the new _spec_ref_id_ (either created or updated)

http://localhost:8080/phonecat/v1/phones/delete/{phoneId}

input:
```
    phoneId (path parameter)
```
output:
```
{
"message": "Phone deleted",
"entity_id": 3
}
```
Remove a phone entry from the catalog.