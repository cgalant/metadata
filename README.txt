
To create a license:
  $  curl -H "Content-Type: multipart/form-data" -X POST http://<web_url>:<port>/api/metadata/1.0/secure/license?name=<license_name>&feeds=<feed_id>(,<feed_id>)* -F "file=@<local_file_path>"
This returns:
  {"id":"<server_generated_license_id>","name":"<license_name>","originalFileName":<file_name_from_<local_file_path>>,"feedIds":[<feed_id>(,<feed_id>)*]}
  
Get the licenses:
  $ curl -X GET http://<web_url>:<port>/api/metadata/1.0/secure/license
This returns the list of licenses:
  [
    {"id":"<server_generated_license_id>","name":"<license_name>","originalFileName":<file_name_from_<local_file_path>>,"feedIds":[<feed_id>(,<feed_id>)*]}
    (,{"id":"<server_generated_license_id>","name":"<license_name>","originalFileName":<file_name_from_<local_file_path>>,"feedIds":[<feed_id>(,<feed_id>)*]})*
  ]
  
Get a specific license:
  $ curl -X GET http://<web_url>:<port>/api/metadata/1.0/secure/license/<license_id> 
This returns:
  The license file, named <originalFileName>

Delete a license:
  $ curl -X DELETE http://<web_url>:<port>/api/metadata/1.0/secure/license/<license_id>
This deletes the license identified by <license_id> and returns:
  {"id":"<server_generated_license_id>","name":"<license_name>","originalFileName":<file_name_from_<local_file_path>>,"feedIds":[<feed_id>(,<feed_id>)*]}

Update a license:
  $ curl -H "Content-Type: multipart/form-data" -X PUT http://<web_url>:<port>/api/metadata/1.0/secure/license/<license_id>?<params> [-F "file=@<new_local_file_path>]
  <params> is any combination of:
    - name=<new_name>
    - feeds=<comma_separated_feed_ids>
    - action=remove
This will 
  - update the name of the license if a new name is provided 
  - update feeds under that license if a new list of feeds are provided :
    * if action is present in <params> and is equal to "remove" then the provided feeds will be removed from this license (they will have no license)
    * else the provieded feeds will change there license to this one
  - update de license file to <new_local_file> if provided
  - returns:
  {"id":"<server_generated_license_id>","name":"<license_name>","originalFileName":<file_name_from_<local_file_path>>,"feedIds":[<feed_id>(,<feed_id>)*]}

Adding a feedId to a license will automatically remove it from any other license.


Miscdata has the same syntax, just change license to miscdata in the requests.

