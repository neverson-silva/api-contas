FROM openjdk:11 AS TEMP_BUILD_IMAGE
COPY . .
RUN ./gradlew build

FROM openjdk:11
WORKDIR app
ENV ARTIFACT_NAME=api-contas-0.0.1.war
COPY --from=TEMP_BUILD_IMAGE /build/libs/$ARTIFACT_NAME /app/ROOT.war
EXPOSE 9090

CMD ["java","-jar","-Xms512m", "-Xmx1024m","/app/ROOT.war"]

