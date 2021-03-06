package pl.ciesielski.sandbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RibbonSandboxApplication {

  public static void main(String[] args) {
    SpringApplication.run(RibbonSandboxApplication.class, args);

    new HelloClient().run();
  }
}
