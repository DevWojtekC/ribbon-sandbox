package pl.ciesielski.sandbox;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wojtek on 31.01.2017.
 */
@RestController
public class HelloController {

  @GetMapping(path = "/hello")
  public String sayHello() {
    return "Hello";
  }
}
