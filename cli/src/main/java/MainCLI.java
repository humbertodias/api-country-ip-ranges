import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import service.CountryIpService;

import javax.enterprise.inject.se.SeContainerInitializer;
import java.util.List;

import static java.lang.System.*;

public class MainCLI {

    @Parameter(names = {"-d"}, description = "Download files")
    private boolean download;

    @Parameter(names = {"-ip"}, description = "IP v4/v6")
    private String ip;

    @Parameter(names = {"-country"}, description = "Country US,ES, etc")
    private List<String> countries;

    public static void main(String... args) {
        // setup standalone CDI
        try (var container = SeContainerInitializer.newInstance().initialize()) {
            var service = container.select(CountryIpService.class).get();

            var main = new MainCLI();
            var cmd = JCommander.newBuilder()
                    .addObject(main)
                    .build();
            if (args.length < 1) {
                cmd.usage();
            } else {
                cmd.parse(args);
                main.run(service);
            }

        }

    }

    public void run(CountryIpService service) {

        if (download) {
            service.downloadAll();
        } else {
            service.loadMaps();
        }

        if (ip != null) {
            out.println(service.getCountry(ip));
        }


        if (countries != null) {
            countries.forEach(country -> out.println(service.getIp(country)));
        }

    }

}


