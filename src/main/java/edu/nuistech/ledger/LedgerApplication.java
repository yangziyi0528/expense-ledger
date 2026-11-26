package edu.nuistech.ledger;

import java.io.PrintWriter;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class LedgerApplication {
    public static void main(String[] args) {
        int exitCode;
        // Picocli owns all user arguments. Spring starts only the non-web application context.
        try (var context = new SpringApplicationBuilder(LedgerApplication.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .registerShutdownHook(false)
                .properties("logging.level.root=OFF")
                .run()) {
            exitCode = context.getBean(LedgerCli.class).run(args,
                    new PrintWriter(System.out, true), new PrintWriter(System.err, true));
        }
        System.exit(exitCode);
    }
}
