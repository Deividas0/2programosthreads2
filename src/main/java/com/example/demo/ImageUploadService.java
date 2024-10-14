package com.example.demo;

import com.example.demo.Paveiksliukai.PaveikslelisRepository;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class ImageUploadService {

    @Autowired
    private PaveikslelisRepository paveikslelisRepository;

    private static final String IMAGEBB_API_URL = "https://api.imgbb.com/1/upload";
    private static final String API_KEY = "b56871776a7fe74f31fd57e570f70e2b";

    public String uploadImage(byte[] imageBytes) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            String imageUrl = uploadToImageBB(base64Image);
            if (imageUrl != null) {
                paveikslelisRepository.updateImageUrl(imageUrl);
                System.out.println("Image uploaded successfully: " + imageUrl);
                return imageUrl;
            } else {
                System.out.println("Failed to upload image.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String uploadToImageBB(String base64Image) throws Exception {
        String imageUrl = null;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost uploadFile = new HttpPost(IMAGEBB_API_URL);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("key", API_KEY, ContentType.TEXT_PLAIN);
            builder.addTextBody("image", base64Image, ContentType.TEXT_PLAIN);

            HttpEntity multipart = builder.build();
            uploadFile.setEntity(multipart);

            try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
                HttpEntity responseEntity = response.getEntity();
                String jsonResponse = EntityUtils.toString(responseEntity);

                System.out.println("ImageBB API Response: " + jsonResponse);


                JSONObject jsonObject = new JSONObject(jsonResponse);

                if (jsonObject.has("data")) {
                    imageUrl = jsonObject.getJSONObject("data").getString("url");
                } else {
                    System.out.println("Error: ImageBB API did not return 'data' field. Full response: " + jsonResponse);
                }
            }
        }

        return imageUrl;
    }
}
