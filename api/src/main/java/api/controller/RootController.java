package api.controller;

import service.CountryIpService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Path("/")
@Singleton
public class RootController {

    @Inject
    CountryIpService countryIpService;

    @GET
    @Path("manifest")
    @Produces(MediaType.TEXT_PLAIN)
    public String manifest() throws IOException {
        var input = this.getClass().getResourceAsStream("/META-INF/MANIFEST.MF");
        return new String(input.readAllBytes(), StandardCharsets.UTF_8);
    }

    @GET
    @Path("files")
    public String [] files() {
        return countryIpService.files();
    }

    @GET
    @Path("download")
    public void download() {
        countryIpService.downloadAll();
    }

    @GET
    @Path("country/{ip}")
    public Optional<String> country(@PathParam("ip") String ip) {
        return countryIpService.getCountry(ip);
    }

    @GET
    @Path("ip/{country}")
    public Optional<Object> ip(@PathParam("country") String country) {
        return countryIpService.getIp(country);
    }

    @PostConstruct
    void setup(){
        countryIpService.loadMaps();
    }

}