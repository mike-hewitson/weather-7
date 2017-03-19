FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/weather-7.jar /weather-7/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/weather-7/app.jar"]
