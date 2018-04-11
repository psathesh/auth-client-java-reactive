## Common Auth Client Reactive

### steps
steps to run the application

* Run application with "gradle bootrun". Application runs on port 9015.
* Open browser or postman and hit "GET:  /v1/teacher" API

-----

### Notes

* RbsAuthenticationManager sets the user details object as below

```
((AbstractAuthenticationToken)tokenAuthentication).setDetails("USER DETAILS");
```
* RbsAuthorizationManager gets the authentication details as below

```
        LOGGER.info("authentication = " + authentication.block());
```

            or

```
        ServerWebExchange exchange = ((AuthorizationContext)object).getExchange();
        Principal principal = exchange.getPrincipal().block();
        LOGGER.info("principal = " + principal);
```

* Either way the user details is not available


-------

##### Warning

Aplication tested only with bootrun (gradle bootRun)

##### ToDo
Check Style, findBug are not included as part of this.
