package pl.ciesielski.sandbox;

import com.netflix.ribbon.ClientOptions;
import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpResourceGroup;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.StopWatch;
import rx.Observable;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

/**
 * Created by wojtek on 31.01.2017.
 */

@Slf4j
public class HelloClient {
  private final HttpResourceGroup httpResourceGroup;
  private final HttpRequestTemplate<ByteBuf> helloTemplate;

  public HelloClient() {
    this.httpResourceGroup = Ribbon.createHttpResourceGroup("hello-client",
        ClientOptions.create()
            .withConnectTimeout(1000)
            .withMaxAutoRetries(4)
            .withMaxAutoRetriesNextServer(2)
            .withReadTimeout(500)
            .withRetryOnAllOperations(true)
            .withConfigurationBasedServerList("localhost:8080")
    );

    this.helloTemplate = httpResourceGroup.newTemplateBuilder("hello")
        .withMethod("GET")
        .withUriTemplate("/hello")
        .withResponseValidator(response -> response.getStatus().equals(HttpResponseStatus.ACCEPTED))
        .build();
  }

  public CompletableFuture<String> getHello() {
    CompletableFuture<String> result = new CompletableFuture<>();
    helloTemplate.requestBuilder().build()
        .observe()
        .flatMap(this::convertHello)
        .doOnError(result::completeExceptionally)
        .single()
        .forEach(result::complete);

    return result;
  }

  public Observable<String> convertHello(ByteBuf input) {
    return Observable.just(input.toString(Charset.defaultCharset()));
  }

  public void run() {
    log.info("Starting calls...");
    StopWatch sw = new StopWatch();

    sw.start();
    while (sw.getTime() < 5000) {
      getHello().thenAccept(hello -> log.info("Hello: {}", hello));
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }


}
