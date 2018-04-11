package com.lib.auth;


import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@WebFluxTest
public class WebfluxTesting {
    
    /**
    
      OPEN URL
    . open url should give you 200 response with CORRECT teacher credential "Authorization" header BASIC testing123$
    . open url should give you 200 response with CORRECT teacher token "Authorization" header BEARER 123456789
    . open url should give you 200 response with WRONG teacher credential "Authorization" header BASIC testingwrong
    . open url should give you 200 response with WRONG teacher token "Authorization" header BEARER 999999999
    . open url should give you 200 response with BLANK credential "Authorization" header "BLANK"
    . open url should give you 200 response with MISSING credential "Authorization" header
    
    . open url should give you 404 response with CORRECT teacher credential "Authorization" header BASIC testing123$ and WRONG url
    
    
      TEACHER URL
    . teacher url should give you 200 response with CORRECT teacher credential "Authorization" header BASIC testing123$
    . teacher url should give you 200 response with CORRECT teacher token "Authorization" header BEARER 123456789
    . teacher URL should give you 401 response with WRONG teacher credential "Authorization" header BASIC testingwrong
    . teacher URL should give you 401 response with WRONG teacher token "Authorization" header BEARER 999999999
    
    . teacher url should give you 401 response with BLANK credential "Authorization" header "BLANK"
    . teacher url should give you 401 response with MISSING credential "Authorization" header
    
    . teacher url should give you 404 response with CORRECT teacher credential "Authorization" header BASIC testing123$ and WRONG url
    
    . teacher url should give you 403 response with CORRECT student credential "Authorization" header BASIC testing123$
    
    
      STUDENT URL
    . student url should give you 200 response with CORRECT "student" credential "Authorization" header BASIC testing123$
    . student url should give you 200 response with CORRECT "teacher" credential "Authorization" header BASIC testing123$
    . teacher url should give you 200 response with CORRECT "teacher" credential "Authorization" header BASIC testing123$
    . teacher url should give you 403 response with CORRECT "student" credential "Authorization" header BASIC testing123$
        
      
      BOTH USER
    . teacher url should give you 200 response with CORRECT "both" credential "Authorization" header BASIC testing123$
    . student url should give you 200 response with CORRECT "both" credential "Authorization" header BASIC testing123$
    
    
    **/
    
    
    private static final String HOSTNAME = "http://localhost:9015";
    
    private static WebTestClient webClient = WebTestClient.bindToServer().baseUrl(HOSTNAME).build();
    
    String teacher_basic_correct = "Basic " + Base64Utils.encodeToString(("teacher1" + ":" + "testing123$").getBytes(StandardCharsets.UTF_8));
    String teacher_bearer_correct = "correct";
    
    String teacher_basic_wrong = "Basic " + Base64Utils.encodeToString(("teacherwrong" + ":" + "passwordwrong").getBytes(StandardCharsets.UTF_8));
    String teacher_bearer_wrong = "wrong";
    
    String student_basic_correct = "Basic " + Base64Utils.encodeToString(("student1" + ":" + "testing123$").getBytes(StandardCharsets.UTF_8));
    
    String both_basic_correct = "Basic " + Base64Utils.encodeToString(("both1" + ":" + "testing123$").getBytes(StandardCharsets.UTF_8));
    
    String BLANK = "";
    
    private void setUp() {
        
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("userId", "ffffffff572352b2e4b0189d37410710");
        formData.add("clientId", "ir0UtVqoO3yOKx8MCuVqbbBrjxEZdJLG");
        formData.add("clientSecret", "XHZhg2es8MmkTp2A5Q5eAxKToiP2no3W");
        formData.add("scope", "rbs");
        formData.add("grant_type", "client_credentials");
    }
    

    @Test
    public void getOpenURLTest() {

        setUp();
        
        /* open url should give you 200 response with CORRECT teacher credential "Authorization" header BASIC testing123$ */
        webClient.get().uri(HOSTNAME + "/v1/openurl")
        .header("Authorization", teacher_basic_correct)
        .exchange()
        .expectStatus().isOk();

        
        /* open url should give you 200 response with CORRECT teacher token "Authorization" header BEARER 123456789 */
        
        
        /* open url should give you 200 response with WRONG teacher credential "Authorization" header BASIC testingwrong */
        webClient.get().uri(HOSTNAME + "/v1/openurl")
        .header("Authorization", teacher_basic_wrong)
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED); // ------------------- ERROR -------------------
        
        
        /* open url should give you 200 response with WRONG teacher token "Authorization" header BEARER 999999999 */
        
        
        /* open url should give you 200 response with BLANK credential "Authorization" header "BLANK" */
        webClient.get().uri(HOSTNAME + "/v1/openurl")
        .header("Authorization", BLANK)
        .exchange()
        .expectStatus().isOk();
        
        
        /* open url should give you 200 response with MISSING credential "Authorization" header */
        webClient.get().uri(HOSTNAME + "/v1/openurl")
        .exchange()
        .expectStatus().isOk();

        
        /* open url should give you 404 response with CORRECT teacher credential "Authorization" header BASIC testing123$ and WRONG url */
        webClient.get().uri(HOSTNAME + "/v1/openurl/404")
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
        .expectBody().jsonPath("$.message").isEqualTo("No matching handler"); // ------------------- UNHANDLED -------------------
        
    }
    
    @Test
    public void getTeacherTest() {

        /* teacher url should give you 200 response with CORRECT "teacher" credential "Authorization" header BASIC testing123$ */
        webClient.get().uri(HOSTNAME + "/v1/teacher")
        .header("Authorization", teacher_basic_correct)
        .exchange()
        .expectStatus().isOk();
        
        
        /* teacher url should give you 200 response with CORRECT teacher token "Authorization" header BEARER 123456789 */
        
        
        /* teacher URL should give you 401 response with WRONG teacher credential "Authorization" header BASIC testingwrong */
        webClient.get().uri(HOSTNAME + "/v1/teacher")
        .header("Authorization", teacher_basic_wrong)
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED);

        
        /* teacher URL should give you 401 response with WRONG teacher token "Authorization" header BEARER 999999999 */
        
        
        /* teacher url should give you 401 response with BLANK credential "Authorization" header "BLANK" */
        webClient.get().uri(HOSTNAME + "/v1/teacher")
        .header("Authorization", BLANK)
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
        .expectBody().jsonPath("$.message").isEqualTo("Not Authenticated");
        
        /* teacher url should give you 401 response with MISSING credential "Authorization" header */
        webClient.get().uri(HOSTNAME + "/v1/teacher")
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
        .expectBody().jsonPath("$.message").isEqualTo("Not Authenticated");
        
        /* teacher url should give you 404 response with CORRECT teacher credential "Authorization" header BASIC testing123$ and WRONG url */
        webClient.get().uri(HOSTNAME + "/v1/teacher/404")
        .header("Authorization", teacher_basic_correct) // ------------------- UNHANDLED -------------------
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
        .expectBody().jsonPath("$.message").isEqualTo("No matching handler");
        
        /* teacher url should give you 403 response with CORRECT student credential "Authorization" header BASIC testing123$ */
        webClient.get().uri(HOSTNAME + "/v1/teacher")
        .header("Authorization", student_basic_correct)
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.FORBIDDEN)
        .expectBody().equals("Access Denied"); // ------------------- UNHANDLED -------------------
    }

    @Test
    public void getAccessTest() {
        
        /* student url should give you 200 response with CORRECT "student" credential "Authorization" header BASIC testing123$ */
        webClient.get().uri(HOSTNAME + "/v1/student")
        .header("Authorization", student_basic_correct)
        .exchange()
        .expectStatus().isOk();
        
        /* student url should give you 200 response with CORRECT "teacher" credential "Authorization" header BASIC testing123$ */
        webClient.get().uri(HOSTNAME + "/v1/student")
        .header("Authorization", teacher_basic_correct)
        .exchange()
        .expectStatus().isOk();
        
        /* teacher url should give you 403 response with CORRECT "student" credential "Authorization" header BASIC testing123$ */
        webClient.get().uri(HOSTNAME + "/v1/teacher")
        .header("Authorization", student_basic_correct)
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.FORBIDDEN)
        .expectBody().equals("Access Denied"); // ------------------- UNHANDLED -------------------
    }
    
    @Test
    public void getBothTest() {
        
        /* teacher url should give you 200 response with CORRECT "both" credential "Authorization" header BASIC testing123$ */
        webClient.get().uri(HOSTNAME + "/v1/teacher")
        .header("Authorization", both_basic_correct)
        .exchange()
        .expectStatus().isOk();
        
        /* student url should give you 200 response with CORRECT "both" credential "Authorization" header BASIC testing123$ */
        webClient.get().uri(HOSTNAME + "/v1/student")
        .header("Authorization", both_basic_correct)
        .exchange()
        .expectStatus().isOk();
    }
    
}





