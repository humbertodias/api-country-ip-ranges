package service;

import com.github.jgonian.ipmath.Ipv4;
import com.github.jgonian.ipmath.Ipv4Range;
import com.github.jgonian.ipmath.Ipv6;
import com.github.jgonian.ipmath.Ipv6Range;
import helper.DownloadHelper;
import org.jsoup.Jsoup;

import javax.inject.Singleton;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@Singleton
public class CountryIpService {

    Pattern IPV4 = Pattern.compile("(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])", Pattern.CASE_INSENSITIVE);

    String ipCountryURL = "http://www.iwik.org/ipcountry/";
    Map<String, Set<Ipv4Range>> rangeIpv4 = new TreeMap<>();
    Map<String, Set<Ipv6Range>> rangeIpv6 = new TreeMap<>();

    private static boolean containsV6(Set<Ipv6Range> range, Ipv6 ip) {
        return range.stream().anyMatch(s -> s.contains(ip));
    }

    private static boolean containsV4(Set<Ipv4Range> range, Ipv4 ip) {
        return range.stream().anyMatch(s -> s.contains(ip));
    }

    public Optional<String> getCountry(String ip) {
        if (IPV4.matcher(ip).matches()) {
            var ipv4 = Ipv4.of(ip);
            return rangeIpv4.entrySet().stream().filter(r -> containsV4(r.getValue(), ipv4)).map(e -> e.getKey()).findFirst();
        } else {
            var ipv6 = Ipv6.of(ip);
            return rangeIpv6.entrySet().stream().filter(r -> containsV6(r.getValue(), ipv6)).map(e -> e.getKey()).findFirst();
        }

    }

    public Optional<AbstractMap.SimpleEntry> getIp(String country) {
        var countryKey = country.toUpperCase();
        if (rangeIpv4.containsKey(countryKey)) {
            return rangeIpv4.get(countryKey)
                    .stream().findAny()
                    .map(r -> r.start()).map(ip -> new AbstractMap.SimpleEntry<>(countryKey, ip.toString()));
        }
        if (rangeIpv6.containsKey(countryKey)) {
            return rangeIpv6.get(countryKey)
                    .stream().findAny()
                    .map(r -> r.start()).map(ip -> new AbstractMap.SimpleEntry<>(countryKey, ip.toString()));
        }
        return Optional.empty();
    }

    public void downloadStartUp() {
        if (DownloadHelper.listFiles().length == 0) {
            downloadAllAndLoad();
        } else {
            System.out.println("Already downloaded");
            loadMaps();
        }
    }

    public void downloadAllAndLoad() {
        CompletableFuture.runAsync(() -> this.downloadAllCountries());
    }

    private void downloadAllCountries() {
        DownloadHelper.downloadFiles(urls());
        loadMaps();
    }

    public String[] files() {
        return DownloadHelper.files();
    }

    public int loadMaps() {
        var files = Arrays.asList(DownloadHelper.listFiles());
        files.stream().filter(f -> f.getName().endsWith("cidr")).forEach(this::parseFileV4);
        files.stream().filter(f -> f.getName().endsWith("ipv6")).forEach(this::parseFileV6);
        return rangeIpv4.size() + rangeIpv6.size();
    }

    public void parseFileV4(File file) {
        var rangesByCountry = new HashSet<Ipv4Range>();
        try (var scanner = new Scanner(file)) {
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                try {
                    var range = Ipv4Range.parseCidr(scanner.nextLine());
                    rangesByCountry.add(range);
                } catch (Exception ignore) {
                }
            }
            var country = noExtension(file.getName());
            rangeIpv4.put(country, rangesByCountry);
        } catch (FileNotFoundException ignore) {
        }
    }

    public void parseFileV6(File file) {
        var rangesByCountry = new HashSet<Ipv6Range>();
        try (var scanner = new Scanner(file)) {
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                try {
                    var range = Ipv6Range.parse(scanner.nextLine());
                    rangesByCountry.add(range);
                } catch (Exception ignore) {
                }
            }
            var country = noExtension(file.getName());
            rangeIpv6.put(country, rangesByCountry);
        } catch (FileNotFoundException ignore) {
        }
    }

    private String noExtension(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    public Set<String> urls() {
        var hrefs = new TreeSet<String>();
        try {
            var doc = Jsoup.connect(ipCountryURL).get();
            var elements = doc.select("a[href$='.cidr'], a[href$='.ipv6']");
            elements.stream()
                    .map(e -> e.attr("href"))
                    .distinct()
                    .forEach(country -> hrefs.add(ipCountryURL + country));
            return hrefs;
        } catch (Exception e) {
            e.printStackTrace();
            return hrefs;
        }
    }

    public Set<String> countries() {
        var hrefs = new TreeSet<String>();
        try {
            var doc = Jsoup.connect(ipCountryURL).get();
            var elements = doc.select("a[href$='.cidr'], a[href$='.ipv6']");
            elements.stream()
                    .map(e -> e.attr("href"))
                    .map(this::noExtension)
                    .distinct()
                    .forEach(country -> hrefs.add(country));
            return hrefs;
        } catch (Exception e) {
            e.printStackTrace();
            return hrefs;
        }
    }

}