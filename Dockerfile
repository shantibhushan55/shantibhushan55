FROM openjdk:20
EXPOSE 9090
ADD target/springboot-keycloak-main.jar springboot-keycloak-main.jar
ENTRYPOINT ["java","-jar","/springboot-keycloak-main.jar"]