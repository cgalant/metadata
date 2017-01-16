
To create a license:
  $  curl -H "Content-Type: application/json" -X POST -d '{"name":"anotherLicense", "text":"This license is ...", "feedIds":[]}' http://<web_url>:<port>/api/metadata/1.0/secure/license 
This returns:
  {"id":"af2d8925-3f02-4d5e-bef8-9f03ec2bc3e4","name":"anotherLicense","text":"This license is ...","feedIds":[]}
  
Get the licenses:
  $ curl -X GET http://<web_url>:<port>/api/metadata/1.0/secure/license
This returns:
  [{"id":"af2d8925-3f02-4d5e-bef8-9f03ec2bc3e4","name":"anotherLicense","text":"This license is ...","feedIds":[]},{"id":"cc49be68-e840-4824-814d-7ccb6a21b17b","name":"secondLicense","text":"This license is ...","feedIds":["asx","sder","xxde"]},{"id":"fbaa9c2a-ca31-42f4-81d8-8f862d7d76fa","name":"firstLicense","text":"This license is ...","feedIds":[]}]
  
Get a specific license:
  $ curl -X GET http://<web_url>:<port>/api/metadata/1.0/secure/license/cc49be68-e840-4824-814d-7ccb6a21b17b 
This returns:
  {"id":"cc49be68-e840-4824-814d-7ccb6a21b17b","name":"secondLicense","text":"This license is ...","feedIds":["asx","sder","xxde"]}

Delete a license:
  $ curl -X DELETE http://<web_url>:<port>/api/metadata/1.0/secure/license/af2d8925-3f02-4d5e-bef8-9f03ec2bc3e4
This returns:
  {"id":"af2d8925-3f02-4d5e-bef8-9f03ec2bc3e4","name":"anotherLicense","text":"This license is ...","feedIds":[]}

Update a license:
  $ curl -H "Content-Type: application/json" -X PUT -d '{"name":"new name", "text": "modified text", "action": "remove", "feedIds":["asx"]}' http://<web_url>:<port>/api/metadata/1.0/secure/license/cc49be68-e840-4824-814d-7ccb6a21b17b
This returns:
  {"id":"cc49be68-e840-4824-814d-7ccb6a21b17b","name":"new name","text":"modified text","feedIds":["sder","xxde"]}

If "action" is not "remove" (anything else or even not given) this will add the feedIds to the license.
Adding a feedId to a license will automatically remove it from any other license.
