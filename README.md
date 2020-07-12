# Country-ip-ranges

Detect the country based on an IP or obtain an IP informing a country.

eg:

    curl -X GET "https://api-country-ip.herokuapp.com/api/ip/ES"

or

    curl -X GET "https://api-country-ip.herokuapp.com/api/country/185.31.236.0"


You can compare the result in https://iplocation.com

Build

    gradle build
    
Run

    java -jar api/build/libs/ROOT-microbundle.jar
    
Then

http://localhost:8080/api/openapi-ui


# Ref

* https://www.myip.com/

* https://iplocation.com/
