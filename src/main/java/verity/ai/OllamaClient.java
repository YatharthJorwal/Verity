package verity.ai;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import verity.config.VerityConfig;

/**
 * Talks to a local Ollama server. Every call runs on one dedicated background thread,
 * so the game thread never waits on the model. Callers get a CompletableFuture and must
 * hand results back to the server thread themselves (for example with server.execute).
 */
public final class OllamaClient {
	/** The model's answer and how long the request took. */
	public record Reply(String content, long millis) {
	}

	private static final Gson GSON = new Gson();
	private static final int MAX_REPLY_TOKENS = 256;
	private static final double TEMPERATURE = 0.8;

	private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
		Thread thread = new Thread(runnable, "verity-brain");
		thread.setDaemon(true);
		return thread;
	});

	private static final HttpClient HTTP = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(3))
			.build();

	private OllamaClient() {
	}

	/** Sends one system prompt and one user message. Never blocks the caller. */
	public static CompletableFuture<Reply> chat(String systemPrompt, String userMessage) {
		VerityConfig config = VerityConfig.get();
		return CompletableFuture.supplyAsync(() -> send(config, systemPrompt, userMessage), EXECUTOR);
	}

	private static Reply send(VerityConfig config, String systemPrompt, String userMessage) {
		long start = System.nanoTime();

		JsonArray messages = new JsonArray();
		messages.add(message("system", systemPrompt));
		messages.add(message("user", userMessage));

		JsonObject options = new JsonObject();
		options.addProperty("temperature", TEMPERATURE);
		options.addProperty("num_predict", MAX_REPLY_TOKENS);

		JsonObject body = new JsonObject();
		body.addProperty("model", config.ollamaModel);
		body.add("messages", messages);
		body.addProperty("stream", false);
		body.addProperty("think", false);
		body.add("options", options);

		String baseUrl = config.ollamaUrl.replaceAll("/+$", "");
		HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/api/chat"))
				.timeout(Duration.ofSeconds(config.ollamaTimeoutSeconds))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(body)))
				.build();

		try {
			HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() != 200) {
				throw new IOException("Ollama returned HTTP " + response.statusCode() + ": " + shorten(response.body()));
			}
			JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
			String content = json.getAsJsonObject("message").get("content").getAsString();
			content = content.replaceAll("(?s)<think>.*?</think>", "").strip();
			long millis = (System.nanoTime() - start) / 1_000_000L;
			return new Reply(content, millis);
		} catch (IOException e) {
			throw new CompletionException(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new CompletionException(e);
		}
	}

	private static JsonObject message(String role, String content) {
		JsonObject message = new JsonObject();
		message.addProperty("role", role);
		message.addProperty("content", content);
		return message;
	}

	private static String shorten(String text) {
		return text.length() <= 200 ? text : text.substring(0, 200) + "...";
	}
}
