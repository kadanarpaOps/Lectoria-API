package com.lectoria_api.common.infrastructure.cloudinary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Component
public class CloudinaryFileUpdateUtil {
    private static final long MAX_IMAGE_SIZE = 10L * 1024 * 1024;
    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryFileUpdateUtil.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    @Value("${cloudinary.cloud_name:}")
    private String cloudName;
    @Value("${cloudinary.api_key:}")
    private String apiKey;
    @Value("${cloudinary.api_secret:}")
    private String apiSecret;

    public ImageReference uploadImage(MultipartFile image) {
        return uploadImage(image, "lectoria");
    }

    public ImageReference uploadImage(MultipartFile image, String folder) {
        validateImage(image);
        ensureConfigured();
        try {
            long timestamp = System.currentTimeMillis() / 1000;
            Map<String, String> params = Map.of("folder", safeFolder(folder), "timestamp", Long.toString(timestamp));
            String boundary = "----Lectoria" + UUID.randomUUID().toString().replace("-", "");
            ByteArrayOutputStream body = new ByteArrayOutputStream();
            writeField(body, boundary, "api_key", apiKey);
            writeField(body, boundary, "folder", params.get("folder"));
            writeField(body, boundary, "timestamp", params.get("timestamp"));
            writeField(body, boundary, "signature", signature(params));
            writeFile(body, boundary, image);
            body.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint("/image/upload")))
                    .timeout(Duration.ofSeconds(45))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode result = objectMapper.readTree(response.body());
            if (response.statusCode() < 200 || response.statusCode() >= 300 || result.has("error")) {
                LOG.warn("Cloudinary image upload returned HTTP status {}", response.statusCode());
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Image upload failed");
            }
            return new ImageReference(result.path("secure_url").asText(), result.path("public_id").asText());
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (Exception exception) {
            LOG.warn("Cloudinary image upload request failed", exception);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Image upload failed", exception);
        }
    }

    public void deleteImage(String publicId) {
        if (publicId == null || publicId.isBlank()) return;
        ensureConfigured();
        try {
            long timestamp = System.currentTimeMillis() / 1000;
            Map<String, String> params = Map.of("public_id", publicId, "timestamp", Long.toString(timestamp), "invalidate", "true");
            String boundary = "----Lectoria" + UUID.randomUUID().toString().replace("-", "");
            ByteArrayOutputStream body = new ByteArrayOutputStream();
            for (Map.Entry<String, String> field : params.entrySet()) writeField(body, boundary, field.getKey(), field.getValue());
            writeField(body, boundary, "api_key", apiKey);
            writeField(body, boundary, "signature", signature(params));
            body.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint("/image/destroy")))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                LOG.warn("Cloudinary image deletion returned HTTP status {} for publicId '{}'",
                        response.statusCode(), publicId);
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Image removal failed");
            }
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (Exception exception) {
            LOG.warn("Cloudinary image deletion request failed for publicId '{}'", publicId, exception);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Image removal failed", exception);
        }
    }

    public String publicIdFromUrl(String url) {
        if (url == null || url.isBlank()) return null;
        int uploadIndex = url.indexOf("/upload/");
        if (uploadIndex < 0) return null;
        String path = url.substring(uploadIndex + "/upload/".length());
        String[] parts = path.split("/");
        int start = 0;
        if (parts.length > 0 && parts[0].matches("v[0-9]+")) start = 1;
        else if (parts.length > 1 && (parts[0].contains(",") || parts[0].contains("_"))) start = 1;
        if (start >= parts.length) return null;
        StringBuilder publicId = new StringBuilder();
        for (int i = start; i < parts.length; i++) {
            if (!publicId.isEmpty()) publicId.append('/');
            publicId.append(parts[i]);
        }
        int extension = publicId.lastIndexOf(".");
        if (extension > publicId.lastIndexOf("/")) publicId.setLength(extension);
        return publicId.isEmpty() ? null : publicId.toString();
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image is required");
        if (image.getSize() > MAX_IMAGE_SIZE) throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Image exceeds the 10 MB limit");
        if (image.getContentType() == null || !image.getContentType().toLowerCase().startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only image uploads are supported");
        }
    }

    private void ensureConfigured() {
        if (cloudName == null || cloudName.isBlank() || apiKey == null || apiKey.isBlank()
                || apiSecret == null || apiSecret.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Image storage is not configured");
        }
    }

    private String endpoint(String path) {
        return "https://api.cloudinary.com/v1_1/" + URLEncoder.encode(cloudName, StandardCharsets.UTF_8).replace("+", "%20") + path;
    }

    private String safeFolder(String folder) {
        if (folder == null || !folder.matches("[a-zA-Z0-9_/-]{1,100}") || folder.startsWith("/") || folder.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image folder");
        }
        return folder;
    }

    private String signature(Map<String, String> params) throws Exception {
        String base = new TreeMap<>(params).entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(java.util.stream.Collectors.joining("&")) + apiSecret;
        byte[] digest = MessageDigest.getInstance("SHA-1").digest(base.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(digest);
    }

    private void writeField(ByteArrayOutputStream output, String boundary, String name, String value) throws Exception {
        output.write(("--" + boundary + "\r\nContent-Disposition: form-data; name=\"" + name + "\"\r\n\r\n"
                + value + "\r\n").getBytes(StandardCharsets.UTF_8));
    }

    private void writeFile(ByteArrayOutputStream output, String boundary, MultipartFile image) throws Exception {
        String filename = image.getOriginalFilename() == null ? "image" : image.getOriginalFilename().replaceAll("[\\r\\n\"]", "_");
        output.write(("--" + boundary + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\""
                + filename + "\"\r\nContent-Type: " + image.getContentType() + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        output.write(image.getBytes());
        output.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    public record ImageReference(String url, String publicId) { }
}
