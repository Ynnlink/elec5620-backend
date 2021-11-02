package net.guides.springboot2.springboot2webappjsp.controllers;


import net.guides.springboot2.springboot2webappjsp.authentication.JwtUtil;
import net.guides.springboot2.springboot2webappjsp.domain.User;
import net.guides.springboot2.springboot2webappjsp.repositories.UserRepository;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("facial")
public class FacialController {

    @Autowired
    UserRepository userRepo;

    //Azure recognition api and key
    private final String endpoint = "https://5620.cognitiveservices.azure.com";
    private final String subscriptionKey = "2fe732e342444140ac9bf9094895dc78";

    @GetMapping("/person-group")
    public void createPersonGroup() {
        personGroup();
    }

    @PostMapping("/register")
    public Result userRegister(@RequestParam("file") MultipartFile file,
                               @RequestParam("mobile_phone") String mobilePhone,
                               @RequestParam("user_name") String name) {

        String face_id = null;

        //invalid condition
        if (file.getSize() == 0) {
            return Result.fail("File can't be empty!");
        }
        if (mobilePhone.length() == 0) {
            return Result.fail("Mobile phone is required!");
        }
        if (name.length() == 0) {
            return Result.fail("Name can't be empty!");
        }

        //facial recognition function
        try {
            face_id = faceDetect(file);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        if (face_id == null) {
            return Result.fail("Facial recognition fail!");
        } else {
            Result result = faceVerify(face_id);
            if (result.getCode().equals("0")) {
                return Result.fail("Already has register info!");
            } else {
                User user = new User();
                user.setUser_face_id(face_id);
                user.setUser_name(name);
                user.setTelephone(mobilePhone);
                userRepo.save(user);
                return Result.succ("Successfully register!");
            }
        }
    }

    @PostMapping("/login")
    public Result userLogin(@RequestParam("file") MultipartFile file) {

        //file can't be empty
        if (file.getSize() == 0) {
            return Result.fail("File can't be empty!");
        }

        String face_id;
        try {
            //detect the user's upload picture
            face_id = faceDetect(file);
            //verify id in database
            Result result = faceVerify(face_id);

            if (result.getCode().equals("0")) {
                //find identical user
                String token = JwtUtil.sign(result.getData().toString());

                if (token != null) {
                    //fill in token
                    return Result.succ("Successfully login!", "Token: " + token);
                } else {
                    return Result.fail("Access denied!");
                }

            } else {
                return Result.fail("No such user information exist, please register!");
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        return null;
    }


    @PostMapping("/logout")
    public Result userLogout() {
        return Result.succ("Successfully logout!");
    }

    //receive user's picture and return unique face id
    public String faceDetect(MultipartFile multipartfile) throws IOException {

        File file = null;

        if(!multipartfile.isEmpty()){

            //temp file location
            String contextPath = "C:\\Users\\Ning\\IdeaProjects\\springboot2-webapp-jsp\\src\\main\\resources\\tempDir";

            File f = new File(contextPath);
            if(!f.exists()){
                f.mkdirs();
            }

            String filename = multipartfile.getOriginalFilename();
            String filepath = contextPath+"/"+filename;
            File newFile = new File(filepath);
            newFile.createNewFile();

            multipartfile.transferTo(newFile);
            file = newFile;

        }

        HttpClient httpclient = HttpClientBuilder.create().build();

        try
        {
            URIBuilder builder = new URIBuilder(endpoint + "/face/v1.0/detect");

            // Request parameters. All of them are optional.
            builder.setParameter("detectionModel", "detection_03");
            builder.setParameter("returnFaceId", "true");

            // Prepare the URI for the REST API call.
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);

            // Request headers.
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

            // Request body.
            FileEntity reqEntity = new FileEntity(file, ContentType.APPLICATION_OCTET_STREAM);
            request.setEntity(reqEntity);

            // Execute the REST API call and get the response entity.
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if(file.exists()){
                file.delete();
            }

            if (entity != null) {
                // Format and display the JSON response.
                // backend diagnose
                System.out.println("\nREST Response:\n");
                String jsonString = EntityUtils.toString(entity).trim();
                JSONArray jsonArray = new JSONArray(jsonString);
                System.out.println(jsonArray.toString(2));

                //unique facial id
                if (jsonArray.getJSONObject(0).get("faceId") != null) {
                    return jsonArray.getJSONObject(0).get("faceId").toString();
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
        catch (Exception e) {
            // Display error message.
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }

    }

    //detect user's face_id and verify
    public Result faceVerify(String identify_id) {

        for (String id: userRepo.findAllID()) {

            HttpClient httpclient = HttpClientBuilder.create().build();

            try {
                URIBuilder builder = new URIBuilder(endpoint + "/face/v1.0/verify");

                // Prepare the URI for the REST API call.
                URI uri = builder.build();
                HttpPost request = new HttpPost(uri);

                // Request headers.
                request.setHeader("Content-Type", "application/json");
                request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

                String verification = "{" +
                        "\"faceId1\":" +
                        "\"" + identify_id + "\"," +
                        "\"faceId2\":" +
                        "\"" + id + "\"" +
                        "}";

                StringEntity reqEntity = new StringEntity(verification);
                request.setEntity(reqEntity);

                // Execute the REST API call and get the response entity.
                HttpResponse response = httpclient.execute(request);
                HttpEntity entity = response.getEntity();

                String jsonString = EntityUtils.toString(entity);
                JSONObject jsonObject = new JSONObject(jsonString);

                // Format and display the JSON response.
                // backend diagnose
                System.out.println("\nREST Response:\n");
                System.out.println(jsonObject);

                if (jsonObject.get("isIdentical").equals(true)) {
                    return Result.succ("Find in database!", id);
                }

            } catch (Exception e) {
                // Display error message.
                e.printStackTrace();
                System.out.println(e.getMessage());
                return Result.fail("Error verifying!");
            }
        }

        return Result.fail("No data!");

    }

    //define a default person group
    public void personGroup() {
        HttpClient httpclient = HttpClientBuilder.create().build();

        try {
            URIBuilder builder = new URIBuilder(endpoint + "/face/v1.0/persongroups/1");

            // Prepare the URI for the REST API call.
            URI uri = builder.build();
            HttpPut request = new HttpPut(uri);

            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

            // Request body
            StringEntity reqEntity = new StringEntity("{\"name\": \"group1\"}");
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                System.out.println(EntityUtils.toString(entity));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
