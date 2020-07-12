package api;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import service.CountryIpService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@OpenAPIDefinition(info = @Info(
        title = "Api-Country-ip",
        version = "1.0.0"),
        servers = {
                @Server(url = "https://api-country-ip.herokuapp.com", description = "Heroku - https"),
                @Server(url = "http://api-country-ip.herokuapp.com", description = "Heroku - http"),
                @Server(url = "http://localhost:8080", description = "8080"),
        }
)
@ApplicationPath("/api")
@ApplicationScoped
public class App extends Application {

    @Inject
    CountryIpService countryIpService;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        countryIpService.downloadStartUp();
    }

}