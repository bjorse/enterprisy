# Client data service
Handles all data associated with clients

## API
###: GET /clients
Returns all clients currently stored in the database

##### Data structure
**id** : int
**added** : datetime
**firstname** : string
**lastname** : string
**email** : string
**birthdate** : datetime
**gender** : string : *Either 'Male' or 'Female'*
**active** : boolean

###: GET /clients/:id
Returns the client associated with the provided id (:id)

##### Data structure
**id** : int
**added** : datetime
**firstname** : string
**lastname** : string
**email** : string
**birthdate** : datetime
**gender** : string : *Either 'Male' or 'Female'*
**active** : boolean

###: POST /client
Adds a new client with the provided data in the database

##### Data structure
**firstname** : string
**lastname** : string
**email** : string
**birthdate** : datetime
**gender** : string : *Either 'Male' or 'Female'*

##### Validations
**firstname** : Required
**lastname** : Required
**email** : Required, valid e-mail address
**birthdate** : Required, not earlier than 100 year, not later than today
**gender** : Required, either 'Male' or 'Female'
