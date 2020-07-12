package service;

import com.github.jgonian.ipmath.Ipv4;
import com.github.jgonian.ipmath.Ipv4Range;
import com.github.jgonian.ipmath.Ipv6;
import com.github.jgonian.ipmath.Ipv6Range;
import helper.DownloadHelper;

import javax.inject.Singleton;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@Singleton
public class CountryIpService {

    Pattern IPV4 = Pattern.compile("(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])", Pattern.CASE_INSENSITIVE);

    String ipCountryURL = "http://www.iwik.org/ipcountry";
    String[] countries = {"AD", "AE", "AF", "AG", "AI", "AL", "AM", "AO", "AP", "AQ", "AR", "AS", "AT", "AU", "AW", "AX", "AZ", "BA", "BB", "BD", "BE", "BF", "BG", "BH", "BI", "BJ", "BL", "BM", "BN", "BO", "BQ", "BR", "BS", "BT", "BW", "BY", "BZ", "CA", "CC", "CD", "CF", "CG", "CH", "CI", "CK", "CL", "CM", "CN", "CO", "CR", "CU", "CV", "CW", "CY", "CZ", "DE", "DJ", "DK", "DM", "DO", "DZ", "EC", "EE", "EG", "ER", "ES", "ET", "EU", "FI", "FJ", "FK", "FM", "FO", "FR", "GA", "GB", "GD", "GE", "GF", "GG", "GH", "GI", "GL", "GM", "GN", "GP", "GQ", "GR", "GT", "GU", "GW", "GY", "HK", "HN", "HR", "HT", "HU", "ID", "IE", "IL", "IM", "IN", "IO", "IQ", "IR", "IS", "IT", "JE", "JM", "JO", "JP", "KE", "KG", "KH", "KI", "KM", "KN", "KP", "KR", "KW", "KY", "KZ", "LA", "LB", "LC", "LI", "LK", "LR", "LS", "LT", "LU", "LV", "LY", "MA", "MC", "MD", "ME", "MF", "MG", "MH", "MK", "ML", "MM", "MN", "MO", "MP", "MQ", "MR", "MS", "MT", "MU", "MV", "MW", "MX", "MY", "MZ", "NA", "NC", "NE", "NF", "NG", "NI", "NL", "NO", "NP", "NR", "NU", "NZ", "OM", "PA", "PE", "PF", "PG", "PH", "PK", "PL", "PM", "PR", "PS", "PT", "PW", "PY", "QA", "RE", "RO", "RS", "RU", "RW", "SA", "SB", "SC", "SD", "SE", "SG", "SI", "SK", "SL", "SM", "SN", "SO", "SR", "SS", "ST", "SV", "SX", "SY", "SZ", "TC", "TD", "TG", "TH", "TJ", "TK", "TL", "TM", "TN", "TO", "TR", "TT", "TV", "TW", "TZ", "UA", "UG", "UM", "US", "UY", "UZ", "VA", "VC", "VE", "VG", "VI", "VN", "VU", "WF", "WS", "YE", "YT", "ZA", "ZM", "ZW"};

    Map<String, Set<Ipv4Range>> rangeIpv4 = new TreeMap<>();
    Map<String, Set<Ipv6Range>> rangeIpv6 = new TreeMap<>();

    public String urlV4(String countryCode) {
        return String.format("%s/%s.cidr", ipCountryURL, countryCode);
    }

    public String urlV6(String countryCode) {
        return String.format("%s/%s.ipv6", ipCountryURL, countryCode);
    }

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
                    .map(r -> r.start()).map(ip -> new AbstractMap.SimpleEntry<>(countryKey, ip.toString()) );
        }
        if (rangeIpv6.containsKey(countryKey)) {
            return rangeIpv6.get(countryKey)
                    .stream().findAny()
                    .map(r -> r.start()).map(ip -> new AbstractMap.SimpleEntry<>(countryKey, ip.toString()) );
        }
        return Optional.empty();
    }

    public void downloadStartUp(){
        if(DownloadHelper.listFiles().length == 0){
            downloadAllAndLoad();
        } else {
            System.out.println("Already downloaded");
            loadMaps();
        }
    }

    public void downloadAllAndLoad() {
        CompletableFuture.runAsync( ()-> this.downloadAllCountries()).thenAccept(aVoid -> loadMaps());
    }

    private void downloadAllCountries(){
        var urls = new HashSet<String>();
        for (var country : countries) {
            urls.add(urlV4(country));
            urls.add(urlV6(country));
        }
        DownloadHelper.downloadFilesAsync(urls);
    }

    public String[] files(){
        return DownloadHelper.files();
    }

    public void loadMaps() {
        var files = Arrays.asList(DownloadHelper.listFiles());
        files.stream().filter(f -> f.getName().endsWith("cidr")).forEach(this::parseFileV4);
        files.stream().filter(f -> f.getName().endsWith("ipv6")).forEach(this::parseFileV6);
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

    private String noExtension(String fileName){
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

}