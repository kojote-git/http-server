## http-server
simple http server

### What is it?
In general, it is a small library that provides basic means to write http servers and web services though it's seems more 
like an experimental project for learning purposes. I tried to keep it simple but convenient and capable enough.

It's currently being developed so there might be bugs that I'm not aware of.

### Example
Let's write a simple server that consists of one controller - `EchoController`. This controller responds us with a similar message 
we pass to it:

```java
public class EchoController {

    @RequestMapping("/echo/{message}")
    public HttpResponse echoMessage(@PathVar("message") String message) {
        return new StringHttpResponse(HttpStatus.OK, message);
    }

    @RequestMapping("/echo/{message}/{n}")
    @DirectVariablesMapping
    public HttpResponse echoMessageNTimes(String message, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(message);
        }
        return new StringHttpResponse(HttpStatus.OK, sb.toString());
    }
}
```

Here we have two controller methods. The first method returns a value of `message` path variable that is substituted when 
we actually make request to the server; the second method returns the `message` `n` times.

Also, note that there are serveral options of how to retrieve values of path variables. 

The first option is by using `@PathVar` annotation, which is quite straightforward. Using it, you define which method parameter
corresponds to which path variable. The value of path variable is automatically converted to the type of the parameter. 

The second, which is interesting one, is to declare `@DirectVariablesMapping` on your controller method which indicates
that you want path variables from your URI template to be directly mapped onto parameters of your controller method. You can also specify from which position this mapping starts. This might be useful if you need, for example, information about the request itself. Than this method will look like this:

```java
    @RequestMapping("/echo/{message}/{n}")
    @DirectVariablesMapping(startIndex = 1)
    public HttpResponse echoMessageNTimes(HttpRequest req, String message, int n) {
        ...
    }
```
Though this type of mapping has some limitations: 
- you must specify the same number of parameters of your method as the number of path variables in your URI
- these parameters must be at the end of parameters list and all other parameters that you want to inject into your method
must precede this mapping between path variables and parameters.

Having all of this, we now can configure and run the sever.

```java
public class Main {
    public static void main(String[] args) throws IOException {
        ServerConfiguration config = new TreeServerConfiguration()
            .addController(new EchoController());
        HttpServer server = new HttpServer(config, 8080);
        server.start();
    }
}
```
Go to your browser and type http://localhost:8080/echo/hello
