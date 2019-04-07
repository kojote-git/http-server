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
    public HttpResponse echoMessage(HttpRequest request, PathVariables vars) {
        String message = vars.getPathVariable("message");
        return stringResponse(HttpStatus.OK, message);
    }

    @RequestMapping("/echo/{message}/{n}")
    public HttpResponse echoMessageNTimes(HttpRequest request, PathVariables vars) {
        String message = vars.getPathVariable("message");
        int n = Integer.parseInt(vars.getPathVariable("n"));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(message);
        }
        return stringResponse(HttpStatus.OK, sb.toString());
    }
}
```

Here we have two controller methods. The first method returns a value of `message` parameter that is substituted when 
we actually make request to the server; the second method returns the `message` `n` times.

The `stringResponse(HttpStatus, String)` method is a utility method to construct responses with the contents of the string passed
to it:
```java
private HttpResponse stringResponse(HttpStatus status, String str) {
    byte[] bytes = str.getBytes();
    return HttpResponseBuilder.create()
        .setStatus(status)
        .addHeader("Content-Type", "text/plain")
        .addHeader("Content-Length", bytes)
        .setResponseBody(new ByteResponseBody(bytes))
        .build();
}
```

Now we need to run the server itself.

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
