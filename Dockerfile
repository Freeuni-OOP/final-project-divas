# --- build stage: compile the WAR ---
FROM maven:3.8-jdk-8 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q package -DskipTests

# --- runtime stage: run it on Tomcat ---
FROM tomcat:9-jdk8-openjdk
# deploy at the root context, so the app is available at http://localhost:8080/
RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY --from=build /app/target/QuizWebsite.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
