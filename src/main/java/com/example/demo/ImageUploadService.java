package com.example.demo;

import com.example.demo.Paveiksliukai.Paveikslelis;
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

import java.sql.SQLException;
import java.util.List;

@Service
public class ImageUploadService {

    @Autowired
    private PaveikslelisRepository paveikslelisRepository;

    private static final String IMAGEBB_API_URL = "https://api.imgbb.com/1/upload";
    private static final String API_KEY = "b56871776a7fe74f31fd57e570f70e2b";

    public void uploadImages() {
        try {
            System.out.println("Tikrinami neįkelti paveiksliukai");
            List<Paveikslelis> images = paveikslelisRepository.findImagesToUpload();
            System.out.println("Rasta " + images.size() + " paveiksliukų eilėje.");

            for (Paveikslelis paveikslelis : images) {
                System.out.println(Thread.currentThread().getName() + " Įkeliamas paveikslėlis su ID: " + paveikslelis.getId());
                String url = uploadToImageBB(paveikslelis.getPaveikslelis());

                if (url != null) {
                    System.out.println("Paveikslėlis įkeltas sėkmingai, atnaujinamas URL su ID: " + paveikslelis.getId());
                    paveikslelisRepository.updateImageUrl(paveikslelis.getId(), url);
                } else {
                    System.out.println("Nepavyko įkelti paveikslėlio su ID: " + paveikslelis.getId());
                }

                System.out.println("Sekundės pertraukėlė :)");
                Thread.sleep(3000);
            }

            System.out.println("Visi paveikslėliai sukelti.");

        } catch (SQLException e) {
            System.out.println("Database error occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted.");
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }



    // Chat GPT
    private String uploadToImageBB(byte[] imageBytes) {
        String imageUrl = null;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost uploadFile = new HttpPost(IMAGEBB_API_URL);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("key", API_KEY, ContentType.TEXT_PLAIN);
            builder.addBinaryBody("image", imageBytes, ContentType.APPLICATION_OCTET_STREAM, "image.jpg");

            HttpEntity multipart = builder.build();
            uploadFile.setEntity(multipart);

            try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
                HttpEntity responseEntity = response.getEntity();
                String jsonResponse = EntityUtils.toString(responseEntity);

                JSONObject jsonObject = new JSONObject(jsonResponse);
                if (jsonObject.getBoolean("success")) {
                    imageUrl = jsonObject.getJSONObject("data").getString("url");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageUrl;
    }
}
