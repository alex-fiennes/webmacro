Management Service

Abstract
---------
The contribution to WM is to define a standard way
to add services to WM allowing for administrators
to manage a WM installation remotely. It was added in release
0.97a. Contributions are welcome.

Original Author: Lane Sharman lane@opendoors.com

Methodology
----------------
Services are added by implementing the management interface,
org.webmacro.mgmt.ManagementService.

A second file, org.webmacro.mgmt.ManagementSupport is a singleton
which acts a gateway to all "registered" management services.

General Development Status
----------------------------
April, 2001
Lane implemented the basic files and wrote a cache flushing
service. Other services need to be written. The ManagementService
interface may need some regfinement.

ManagementSupport needs to "auto-discover" management services.
There are a number of ways to do this besides providing a runtime
addService() method.



