package api.controller;

import api.helper.FileStreamingOutput;
import api.helper.IPHelper;
import helper.DownloadHelper;
import service.CountryIpService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.json.*;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

        var remote = Json.createObjectBuilder();
        remote.add("ip", IPHelper.getClientIpAddr(request));
        remote.add("host", request.getRemoteHost());
        remote.add("port", request.getRemotePort());
        remote.add("user", IPHelper.getRemoteUser(request));

        var client = Json.createObjectBuilder();
        client.add("agent", IPHelper.getUserAgent(request));
        client.add("os", IPHelper.getClientOS(request));
        client.add("browser", IPHelper.getClientBrowser(request));

        var root = Json.createObjectBuilder();
        root.add("remote", remote);
        root.add("client", client);

        return root.build();
    }

    @GET
    @Path("files")
    public String[] files() {
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

    @GET
    @Path("countries")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<String> countries() {
        return countryIpService.countries();
    }

    @GET
    @Path("zip")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response zip() throws Exception {
        var file = DownloadHelper.zip();
        var stream = new FileStreamingOutput(file);
        return Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
                .header("content-length", file.length())
                .header("content-disposition", String.format("attachment; filename=%s",file.getName())).build();
    }

}