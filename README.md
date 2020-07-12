# Country-ip-ranges

Dynamic detection of country based on an IP or obtain an IP informing a country.

The [CIDR](https://en.wikipedia.org/wiki/Classless_Inter-Domain_Routing) files are refreshed daily on [www.iwik.org/ipcountry](http://www.iwik.org/ipcountry).

eg:

    curl -X GET "https://api-country-ip.herokuapp.com/api/ip/ES"

Response

    {"key":"ES","value":"185.31.236.0"}

or

    curl -X GET "https://api-country-ip.herokuapp.com/api/country/185.31.236.0"

Response

    "ES"
    
You can compare the result in https://iplocation.com

Build

    gradle build
    
Run

    java -jar api/build/libs/ROOT-microbundle.jar
    
Then

http://localhost:8080/api/openapi-ui


# Ref

* [CIDR - Classless Inter Domain Routing](https://en.wikipedia.org/wiki/Classless_Inter-Domain_Routing)

* [myip.com](https://www.myip.com)

* [iplocation](https://iplocation.com)

* [ip2location](https://www.ip2location.com/demo)

* [CSS Selectors](https://www.w3schools.com/cssref/css_selectors.asp)
