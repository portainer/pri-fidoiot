# About

The FDO Owner Service is designed to onboard the client device to a Management service. Owner participates during TO0 and T02 FDO protocols and is responsible for ServiceInfo transfer & initializing the device to working state.


***NOTE***: Appropriate security measures with respect to key-store management and credential management should be considered while performing production deployment of any FDO component.

# Getting Started with the FDO Owner Service

The following are the system requirements for the FDO Owner Service.
- Operating System: Ubuntu* 20.04
- Java* Development Kit 11
- Apache Maven* 3.5.4 (Optional) software for building the demo from source
- Java IDE (Optional) for convenience in modifying the source code
- Docker 18.09
- Docker compose 1.21.2
- Haveged

# Configuring JAVA Execution Environment

Appropriate proxy configuration should be updated in **`_JAVA_OPTIONS`** environment variable. (Mandatory, if you are working behind a proxy.)

Update the proxy information in _JAVA_OPTIONS as ```_JAVA_OPTIONS=-Dhttp.proxyHost=http_proxy_host -Dhttp.proxyPort=http_proxy_port -Dhttps.proxyHost=https_proxy_host -Dhttps.proxyPort=https_proxy_port```.

# Getting the Executable

Use the following commands to build FIDO Device Onboard (FDO) Owner source.
For the instructions in this document, `<fdo-pri-src>` refers to the path of the FDO PRI folder 'pri-fidoiot'.
```
$ cd <fdo-pri-src>/
$ mvn clean install
```

This will copy the required executables and libraries into \<fdo-pri-src\>/component-samples/demo/owner/.

# Configuring the FDO Owner Service

All the runtime configurations for the services are specified in four files: `service.env`, `hibernate.cfg.xml`, `service.yml` and `WEB-INF/web.xml`.

`service.env`: consists of all the credentials used by the Owner Service demo service. These credential configurations are to be generated freshly for each deployment.

`hibernate.cfg.xml`: consists of all the database configurations used by the Owner Service. This file can be configured to pick various database tables and properties.

`service.yml` file is structured into multiple sections:

- `hibernate-properties:` - This section contains *Hibernate related runtime properties including the DB URL, dialect and others.


- `system-properties:` - This section contains the runtime environment variables.


- `http-server:` - This section contains the *Tomcat server related properties including ports, schemes, keystore information and api authentication setup.


- `owner:` - This section contains the configuration related to Owner keystore path, type and credentials.


- `workers:` The section contains the configuration to select desired functionality for the services. The deployer can pick and choose the functionality during runtime.


`WEB-INF/web.xml`: consist of the all configurations related to REST endpoints served. The deployer can pick and choose the served endpoints during runtime.

# Configuring Owner Proxy Settings

To configure Owner proxy resolution, add the following properties to `demo/owner/service.env` file:

```
http_host=<value>
https_host=<value>
http_port=<value>
https_port=<value>
no_proxy=<value>
```

# Running FDO Owner Service

FDO Owner Service can be executed as a standalone service as well as a docker service. At the
end of initialization of all services, you will see following statement on the console.

`[INFO] Started Owner Service.`

Follow the below steps to start Owner Service.

##  Run as Standalone service.
Open a terminal, change directory to `<fdo-pri-src>/component-samples/owner/` and execute following command.

```shell
java -jar owner.jar
```

Make sure to export the credential environment variables set in `service.env` file.

##  Run as Docker Service

Open a terminal, change directory to `<fdo-pri-src>/component-samples/owner/` and execute following command.

```
docker-compose up --build
```

In case you need super user access, prefix 'sudo -E' to above command.

***NOTE :*** To support OnDie ECDSA Device attestation, copy the required certificates and crls to `<fdo-pri-src>/component-samples/owner/ondiecache` folder.

***NOTE***: The database file located at \<fdo-pri-src\>/component-samples/demo/owner/app-data/emdb.mv.db is not deleted during 'mvn clean'. As a result, the database schema and tables are persisted across docker invocations. Please delete the file manually, if you encounter any error due to persisted stale data.

# FDO PRI Owner REST APIs

| Operation                      | Description                        | Path/Query Parameters    | Content Type   |Request Body  | Response Body | Sample cURL call |
| ------------------------------:|:----------------------------------:|:------------------------:|:--------------:|-------------:|--------------:|-----------------:|
| Operation                      | Description                        | Path/Query Parameters    | Content Type   |Request Body  | Response Body | Sample cURL call |
| ------------------------------:|:----------------------------------:|:------------------------:|:--------------:|-------------:|--------------:|-----------------:|
| POST /api/v1/owner/redirect    | Updates TO2 RVBlob in `ONBOARDING_CONFIG` table. | | text/plain | RVTO2Addr in diagnostic form | | curl -D - --digest -u ${api_user}: --location --request POST 'http://localhost:8042/api/v1/owner/redirect' --header 'Content-Type: text/plain'  --data-raw '[["localhost","127.0.0.1",8042,3]]' |
| GET /api/v1/to0/{guid} | initiate TO0 from Owner | GUID of the device to initiate TO0 | text/plain |  |  | curl  -D - --digest -u ${api_user}: --location --request GET "http://localhost:8042/api/v1/to0/${device_guid}" --header 'Content-Type: text/plain' |
| POST /api/v1/owner/svi | Uploads SVI instructions to `SYSTEM_PACKAGE` table. |  | text/plain | SVI Instruction |   | curl -D - --digest -u ${api_user}: --location --request POST 'http://localhost:8042/api/v1/owner/svi' --header 'Content-Type: text/plain' --data-raw '[{"filedesc" : "setup.sh","resource" : "URL"}, {"exec" : ["bash","setup.sh"] }]' |
| GET /api/v1/owner/vouchers | Returns a list of all Ownership Voucher GUIDs. | | | | line separated list of GUIDs | curl  -D - --digest -u ${api_user}: --location --request GET "http://localhost:8042/api/v1/owner/vouchers" --header 'Content-Type: text/plain' |
| GET /api/v1/owner/vouchers/<device_guid> | Returns the Ownership Voucher for the specified GUID. | Query - id: Device GUID | | | Ownership Voucher | curl  -D - --digest -u ${api_user}: --location --request GET "http://localhost:8042/api/v1/owner/vouchers/${device_guid}" --header 'Content-Type: text/plain' |
| POST /api/v1/owner/vouchers/ | Insert Ownership Voucher against the specified GUID in `ONBOARDING_VOUCHER` table. | | text/plain | Content of Ownership Voucher in PEM Format | |  curl  -D - --digest -u ${api_user}: --location --request POST "http://localhost:8042/api/v1/owner/vouchers" --header 'Content-Type: text/plain' --data-raw '${voucher}' |
| GET /api/v1/logs | Serves the log from the manufacturer service | | | | Manufacturer logs| curl  -D - --digest -u ${api_user}:  --location --request GET 'http://localhost:8042/api/v1/logs' | 
| DELETE /api/v1/logs | Deletes the log from the manufacturer service | | |  | | curl  -D - --digest -u ${api_user}:  --location --request DELETE 'http://localhost:8042/api/v1/logs' |
| GET /health | Returns the health status |  |  | | Current version |  curl  -D - --digest -u ${api_user}:  --location --request GET 'http://localhost:8042/health'|
| GET /api/v1/ondie | Serves the stored certs & crls files | || Ondie certs & crl files |
| POST /api/v1/ondie | To download onDie certs and crls zip file url. | | text/plain | Ondie certs/crls URL |
| GET /api/v1/certificate?filename=fileName | Returns the certificate file based on filename | Path - filename | | | Certificate file in PKCS12 format | curl  -D - --digest -u ${api_user}: --location --request GET 'http://localhost:8042/api/v1/certificate?filename=ssl.p12' |
| GET /api/v1/certificate?alias={alias} | Returns the owner certificate of the given alias type | Path - alias | | | Certificate PEM format | curl  -D - --digest -u ${api_user}: --location --request GET 'http://localhost:8042/api/v1/certificate?alias=SECP256R1' --header 'Content-Type: text/plain' |
| GET /api/v1/certificate?uuid=uuid | Returns the owner alias type for the given voucher| Path - uuid | | | Certificate PEM format | curl  -D - --digest -u ${api_user}: --location --request GET 'http://localhost:8042/api/v1/certificate?uuid=cc60f0aa-56d0-492e-8c8d-9a1fe55cb60 --header 'Content-Type: text/plain' |
| POST /api/v1/certificate?filename=fileName | Adds the certificate file to DB based on filename | Path - filename | text/plain| PKCS12 Certificate file in Binary format |  | curl -D - --digest -u ${api_user}: --location --request POST 'http://localhost:8042/api/v1/certificate?filename=ssl.p12' --data-binary '@< path to ssl.p12 >' |
| DELETE /api/v1/certificate?filename=fileName | Delete the certificate file to DB based on filename | Path - filename | | |  | curl  -D - --digest -u ${api_user}: --location --request DELETE 'http://localhost:8042/api/v1/certificate?filename=ssl.p12' --header 'Content-Type: text/plain' | 
| POST /api/v1/certificate/validity?days=no_of_days | Updates certificate validity in `CERTIFICATE_VALIDITY` table | | text/plain; charset=us-ascii |  | | curl  -D - --digest -u ${api_user}: --location --request POST 'http://localhost:8039/api/v1/certificate/validity?days=10' |
| GET /api/v1/certificate/validity | Collects certificate validity days from  `CERTIFICATE_VALIDITY` table | |  | | Number of Days| curl  -D - --digest -u ${api_user}: --location --request GET 'http://localhost:8039/api/v1/certificate/validity' |
| GET /api/v1/owner/messagesize | Collects the max message size from `ONBOARDING_CONFIG` table | | |  | MAX_MESSAGE_SIZE | curl -D - --digest -u ${api_user}: --location --request GET 'http://localhost:8042/api/v1/owner/messagesize' --header 'Content-Type: text/plain'|
| POST /api/v1/owner/messagesize | Updates the max message size in `ONBOARDING_CONFIG` table | | | MAX_MESSAGE_SIZE | | curl -D - --digest -u ${api_user}: --location --request POST 'http://localhost:8042/api/v1/owner/messagesize?size=1400' --header 'Content-Type: text/plain'|
| GET /api/v1/owner/svisize | Collects the owner svi size from `ONBOARDING_CONFIG` table | | text/plain |  | MAX_MESSAGE_SIZE | curl -D - --digest -u ${api_user}: --location --request GET 'http://localhost:8042/api/v1/owner/svisize' --header 'Content-Type: text/plain' |
| POST /api/v1/owner/svisize | Updates the owner svi size in `ONBOARDING_CONFIG` table | | text/plain | MAX_MESSAGE_SIZE | | curl -D - --digest -u ${api_user}: --location --request POST 'http://localhost:8042/api/v1/owner/svisize?size=1400' --header 'Content-Type: text/plain' |
| GET /api/v1/owner/resource?filename=fileName | Returns the file based on filename from `SYSTEM_RESOURCE` table | Path - filename | | | file |  curl -D - --digest -u ${api_user}: --location --request GET 'http://localhost:8042/api/v1/owner/resource?filename=fileName' --header 'Content-Type: text/plain'  |
| POST /api/v1/owner/resource?filename=fileName | Adds the file to DB based on filename  from `SYSTEM_RESOURCE` table  | Path - filename | text/plain| file in Binary format |  |  curl -D - --digest -u ${api_user}: --location --request POST 'http://localhost:8042/api/v1/owner/resource?filename=fileName' --header 'Content-Type: text/plain' --data-binary '@< path to file >' |
| DELETE /api/v1/owner/resource?filename=fileName | Delete the  file from DB based on filename from `SYSTEM_RESOURCE` table   | Path - filename | | |  |  curl -D - --digest -u ${api_user}: --location --request DELETE 'http://localhost:8042/api/v1/owner/resource?filename=fileName' --header 'Content-Type: text/plain'|
| POST /api/v1/resell/{guid} | Gets extended resell Ownership Voucher with the guid. | Path - guid of the device to resell | | Owner Certificate | The Ownership voucher in PEM format |   curl -D - --digest -u ${api_user}: --location --request POST "http://localhost:8039/api/v1/resell/${guid}" --header 'Content-Type: text/plain' --data-raw  "$owner_certificate" -o ${serial_no}_voucher.txt |

***NOTE***: These REST APIs use Digest authentication. `api_user` and `api_password` properties specify the credentials to be used while making the REST calls. The value for `api_user` is present in `service.yml` file and value for `api_password` is present in `service.env` file.

Following is the list of REST response error codes and it's possible causes :

|     Error Code     |             Possible Causes               |
| -------------------:|:----------------------------------------:|
| `401 Unauthorized`  | When an invalid Authentication header is present with the REST Request. Make sure to use the correct REST credentials. |
| `404 Not Found`     | When an invalid REST request is sent to AIO. Make sure to use the correct REST API endpoint. |
| `405 Method Not Allowed` | When an unsupported REST method is requested. Currently, AIO supports GET, PUT and DELETE only. |
| `406 Not Acceptable` | When an invalid filename is passed through the REST endpoints. |
| `500 Internal Server Error` | Due to internal error, AIO unable to fetch/copy/delete the requested file. |


# Troubleshooting

As the H2 DB grows, larger heap space will be required by the application to run the service. The default configured heap size is `256 MB`. Increase the heap size appropriately in `demo/owner/owner-entrypoint.sh` to avoid heap size issue

# Configuring FDO Owner Service for HTTPS/TLS Communication

By default, the Owner Service uses HTTP for all communications on port 8042. In addition to that, the Owner Service can be configured to handle HTTPS requests from the device.

Owner Service can generate its own certificate and if you want to override the default certificate, follow these steps:

- Generate the Keystore/Certificate for the Owner Service. [REFER](https://docs.oracle.com/cd/E19509-01/820-3503/6nf1il6er/index.html)

    * Ensure that the web certificate is issued to the resolvable domain of Owner Service.

- Copy the generated Keystore/Certificate to `.app-data` folder and update credentials in `service.yml` file.

- Update the SSL keystore password & subject_names in `service.yml` file.

# Configuring AIO workers

Workers are java classes the implement various behavioral aspects of an FDO service.  Workers are defined in the workers section of the service.yml.


|     worker                        |             Description         |
| ---------------------------------:|:-------------------------------------:|
| org.fidoalliance.fdo.protocol.HttpOwnerSchemeSupplier | Tells the owner to use HTTP instead of HTTPS for TO0 protocol|
| org.fidoalliance.fdo.protocol.RemoteDatabaseServer | Provides access to an external database  |
| org.fidoalliance.fdo.protocol.SelfSignedHttpClientSupplier | Tells HTTPS Clients to trust self-signed certificates  |
| org.fidoalliance.fdo.protocol.StandardCryptoService | Provides cryptographic services  (e.g. sign, verify, random number generation etc) |
| org.fidoalliance.fdo.protocol.StandardDatabaseServer | Provides an embedded H2 Database server |
| org.fidoalliance.fdo.protocol.StandardHttpServer | Provides an embedded Tomcat HTTP server for handling messages |
| org.fidoalliance.fdo.protocol.StandardLogProvider | Providers logging services for other workers (must be first defined worker)
| org.fidoalliance.fdo.protocol.StandardMessageDispatcher | Provides message processing for all FDO protocols          | 
| org.fidoalliance.fdo.protocol.StandardOwnerKeySupplier | Provides owner signing keys  |
| org.fidoalliance.fdo.protocol.StandardOwnerSchemeSupplier | Tells the owner to use HTTPS for TO0 protocol |
| org.fidoalliance.fdo.protocol.StandardReplacementKeySupplier | Provides Owner2 keys for for credential replacement during TO2
| org.fidoalliance.fdo.protocol.db.ConformanceOwnerModule | Provides the implementation of Fido Conformance Module for Interop|
| org.fidoalliance.fdo.protocol.db.FdoSysOwnerModule | Provides the implementation of the FdoSys Module |
| org.fidoalliance.fdo.protocol.db.OnDieCertificateManager | Provides Root certificate chains for Intel OnDie hardware ECDSA keys|
| org.fidoalliance.fdo.protocol.db.ReuseVoucherReplacementFunction | Tells the owner to reuse the device credentials instead of replacing them during TO2 |
| org.fidoalliance.fdo.protocol.db.StandardAcceptOwnerFunction | Accepts TO0 wait seconds and updates the owner database with the expiry time |
| org.fidoalliance.fdo.protocol.db.StandardExtraInfoSupplier | Provides null as owner extra data in ownership voucher entries |
| org.fidoalliance.fdo.protocol.db.StandardKeyStoreInputStream | Provides Keystore loading from a database table |
| org.fidoalliance.fdo.protocol.db.StandardKeyStoreOutputStream | Provides Keystore storage to a database table |
| org.fidoalliance.fdo.protocol.db.StandardOwnerInfoSizeSupplier | Provides the size of the service info buffer owner is willing to accept |
| org.fidoalliance.fdo.protocol.db.StandardRendezvousWaitSecondsSupplier | Gets the Wait Seconds amount the rv server is willing to accept  |
| org.fidoalliance.fdo.protocol.db.StandardReplacementVoucherStorageFunction | Stores the replacement ownership voucher at the end to TO2 |
| org.fidoalliance.fdo.protocol.db.StandardServerSessionManager | Provides session storage and management for service side protocols |
| org.fidoalliance.fdo.protocol.db.StandardSessionCleaner | Removes incomplete session from the database after a couple of hours |
| org.fidoalliance.fdo.protocol.db.StandardValidityDaysSupplier | Provides the validity days for certificate generation |
| org.fidoalliance.fdo.protocol.db.StandardVoucherQueryFunction | Provides ownership vouchers from the owner database |
| org.fidoalliance.fdo.protocol.db.StandardVoucherReplacementFunction | Tells the owner to replace the device credentials during TO2 |
| org.fidoalliance.fdo.protocol.db.StandardVoucherStorageFunction | Stores voucher in the database without performing To0 protocol |
| org.fidoalliance.fdo.protocol.db.To0Scheduler | Performs To0 for vouchers that have expired wait seconds at regular intervals |
