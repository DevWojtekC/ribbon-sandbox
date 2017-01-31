package pl.ciesielski.sandbox;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wojtek on 31.01.2017.
 */
@RestController
@Slf4j
public class HelloController {
  private final AtomicInteger integer = new AtomicInteger(0);

  @GetMapping(path = "/hello")
  public ResponseEntity<String> sayHello() {
    int callCount = integer.incrementAndGet();
    log.info("Processing call {} ", callCount);
    if (callCount % 5 < 2) {
      log.warn("Simulating error - should be replayed");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    } else {
      return ResponseEntity.ok("Hello " + callCount);
    }
  }
}
