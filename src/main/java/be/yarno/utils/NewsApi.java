package be.yarno.utils;

import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.request.EverythingRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.concurrent.CompletableFuture;

/**
 * Van de Weyer Yarno
 * 21/01/2023
 */
public class NewsApi {
    private final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private final String TOKEN = dotenv.get("NEWS_TOKEN");
    private final NewsApiClient client = new NewsApiClient(TOKEN);

    /**
     * Method to get the headlines of the news source
     * @return a completable future with the headlines
     */
    public CompletableFuture<String> getHeadlines() {
        CompletableFuture<String> future = new CompletableFuture<>();
        StringBuilder str = new StringBuilder();

        client.getEverything(new EverythingRequest.Builder().domains("demorgen.be").sortBy("publishedAt").build(), new NewsApiClient.ArticlesResponseCallback() {
            @Override
            public void onSuccess(ArticleResponse response) {
                for (int i = 0; i < 5; i++) {
                    str.append("\n● ").append(response.getArticles().get(i).getTitle());
                }
                future.complete(str.toString());
            }

            @Override
            public void onFailure(Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });
        return future;
    }
}
