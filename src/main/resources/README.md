# **Server API**
API version: v1.0.0
Documentation: 24.04.2024

## 1 Getting started
### 1.1 Adding the API using Maven

Navigate to your ``pom.xml``. There add a new repository like this: \
```xml
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>
```
Next add the dependency for this API:
```xml
<dependency>
    <groupId>com.github.Redstoner-2019</groupId>
    <artifactId>ServerAPI</artifactId>
    <version>version</version>
</dependency>
```
Replace ``version`` with the version you want to use.

### 1.2 Creating a Server

Create a new Java class extending ``ODServer``. An example:
```java
public class Server extends ODServer {
    public static void main(String[] args) {

    }
}
```
Now you have to initialize the server.\
Using ``setup(8007, ConnectionProtocol.TCP);`` you can set the Server port to be ``8007`` and use the protocol ``TCP``.
If you were to use ``setup(8007, ConnectionProtocol.UDP);`` you can also use the ``UDP`` protocol which may be faster, but doesnt guarantee a successful transfer. \
\
Our current code should looks something like this now: 
```java
public class Server extends ODServer {
    public static void main(String[] args) {
        setup(8007, ConnectionProtocol.TCP);
    }
}
```
Next up you can call the ``start();`` method to start the server.

### 1.3 Creating a Client

Create a new Java class extending ``ODClient``. An example:
```java
public class Client extends ODClient {
    public static void main(String[] args) {

    }
}
```

To now connect to a Server, call ``connect("localhost", 8007, ConnectionProtocol.TCP);``, where ``localhost`` is the server address, ``8007`` is the port the server is running on and ``ConnectionProtocol.TCP`` is again the Connection Protocol, which must match up with the protocol on the Server.
```java
public class Client extends ODClient {
    public static void main(String[] args) {
        connect("localhost",8007, ConnectionProtocol.TCP);
    }
}
```