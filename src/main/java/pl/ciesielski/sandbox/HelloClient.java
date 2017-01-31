package pl.ciesielski.sandbox;

import com.netflix.ribbon.ClientOptions;
import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpResourceGroup;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
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
            .withConfigurationBasedServerList("localhost:8080")
    );

    this.helloTemplate = httpResourceGroup.newTemplateBuilder("hi")
        .withMethod("GET")
        .withUriTemplate("/hello")
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
    log.info("Hello: {}", getHello().join());
  }

  public static void main(String[] args) throws InterruptedException {
    new HelloClient().run();
    Thread.sleep(5000);
  }
}
