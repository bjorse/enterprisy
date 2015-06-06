# Service name
Short description of the purpose of this service.

## API
###: GET /entity
Short description of the purpose of this API call

##### Data structure
**id** : int
**name** : string : *This is a description if there's something noticable about this value*
**added** : datetime

###: POST /entity
Short description of the purpose of this API call

##### Data structure
**name** : string

##### Validations
**name** : max length: 100

## Event publishing
###: entity.added
Short description of the purpose of this event publish

#### Data structure
**id** : int
**name** : string
**added** : datetime

## Event listening
###: something.happened
**id** : int
**type** : string
