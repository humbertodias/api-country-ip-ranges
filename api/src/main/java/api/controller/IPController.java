package api.controller;

import api.helper.IPHelper;
import service.CountryIpService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Optional;

@Path("/")
@Singleton
public class IPController {

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
    @Path("myip")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject myip(@Context HttpServletRequest request) {
        var builder = Json.createObjectBuilder();
        builder.add("remoteIp", IPHelper.getClientIpAddr(request));
        builder.add("remoteHost", request.getRemoteHost());
        builder.add("remotePort", request.getRemotePort());
        builder.add("remoteUser", "" + request.getRemoteUser());
        builder.add("userAgent", IPHelper.getUserAgent(request));
        builder.add("clientOS", IPHelper.getClientOS(request));
        builder.add("clientBrowser", IPHelper.getClientBrowser(request));
        return builder.build();
    }

    @GET
    @Path("files")
    public String [] files() {
        return countryIpService.files();
    }

    @GET
    @Path("download")
    public void download() {
        countryIpService.downloadAllAndLoad();
    }

    @GET
    @Path("load")
    public int load() {
        return countryIpService.loadMaps();
    }

    @GET
    @Path("country/{ip}")
    public Optional<String> country(@PathParam("ip") String ip) {
        return countryIpService.getCountry(ip);
    }

    @GET
    @Path("ip/{country}")
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<AbstractMap.SimpleEntry> ip(@PathParam("country") String country) {
        return countryIpService.getIp(country);
    }

}