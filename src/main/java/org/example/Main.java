package org.example;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

//project to call our REST calls, instead of using Postman
public class Main {

  /*API = Application Program Interface =  a way for two programs to talk to with each other
    REST = Representational State Transfer
    code 200 = SUCCESS
    code 400 level responses = failure, which basically means there's something wrong with the request that you're sending ex: error 404 => which means that the endpoint you are trying to reach does not exist
    code 500 level responses = failure, something went wrong with the API on the server side => it's not your fault and there's nothing wrong with your request => that doesn't happen too much on good professional api's */

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

        Transcript transcript =  new Transcript();
        transcript.setAudio_url("https://github.com/johnmarty3/JavaAPITutorial/blob/main/Thirsty.mp4?raw=true");

        Gson gson = new Gson();
        String jsonRequest = gson.toJson(transcript);
        System.out.println("jsonRequest: " + jsonRequest);

        HttpRequest postRequest = (HttpRequest) HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript"))
                .header("Authorization", "3ece27e5e50d41cb8950f45905fcb09e")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(postResponse.body());

        transcript = gson.fromJson(postResponse.body(), Transcript.class);
        String transcriptId = transcript.getId();

        HttpRequest getRequest =  HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript/" + transcriptId))
                .header("Authorization", "your key here")
                .GET()
                .build();


        while (true) {
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            transcript = gson.fromJson(getResponse.body(), Transcript.class);

            System.out.println(transcript.getStatus());

            if ("completed".equals(transcript.getStatus()) || "error".equals(transcript.getStatus())) {
                break;
            }
            Thread.sleep(1000);
        }

        System.out.println("Transcription completed with the status of " + transcript.getStatus());
        System.out.println(transcript.getText());

    }
}