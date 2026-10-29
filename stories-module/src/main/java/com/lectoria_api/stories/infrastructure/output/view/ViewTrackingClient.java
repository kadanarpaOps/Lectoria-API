package com.lectoria_api.stories.infrastructure.output.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.nio.charset.StandardCharsets;

@Component
public class ViewTrackingClient {
    private static final Logger LOG = LoggerFactory.getLogger(ViewTrackingClient.class);
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    @Value("${view.url:}")
    private String viewUrl;

    public void track(String storyId, String userId) {
        if (viewUrl == null || viewUrl.isBlank()) return;
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(viewUrl + "/counter/" + storyId))
                    .timeout(Duration.ofSeconds(10)).method("PATCH", HttpRequest.BodyPublishers.noBody()).build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                LOG.warn("External view tracking returned HTTP {} for storyId '{}'", response.statusCode(), storyId);
            }
        } catch (Exception exception) {
            LOG.warn("External view tracking failed for storyId '{}'", storyId, exception);
        }
    }

    public void registerStory(MapStory story) {
        if (viewUrl == null || viewUrl.isBlank()) return;
        try {
            String json = "{\"bookId\":\"" + escape(story.id()) + "\",\"title\":\"" + escape(story.title())
                    + "\",\"authorId\":\"" + escape(story.authorId()) + "\"}";
            HttpRequest request = HttpRequest.newBuilder(URI.create(viewUrl))
                    .header("Content-Type", "application/json").timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8)).build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() < 200 || response.statusCode() >= 300) LOG.warn("External story registration returned HTTP {} for storyId '{}'", response.statusCode(), story.id());
        } catch (Exception exception) {
            LOG.warn("External story registration failed for storyId '{}'", story.id(), exception);
        }
    }

    public void removeStory(String storyId) {
        if (viewUrl == null || viewUrl.isBlank()) return;
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(viewUrl + "/" + storyId)).timeout(Duration.ofSeconds(10)).DELETE().build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() < 200 || response.statusCode() >= 300) LOG.warn("External story view cleanup returned HTTP {} for storyId '{}'", response.statusCode(), storyId);
        } catch (Exception exception) {
            LOG.warn("External story view cleanup failed for storyId '{}'", storyId, exception);
        }
    }
    private String escape(String value) { return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\""); }
    public record MapStory(String id, String title, String authorId) { }
}
