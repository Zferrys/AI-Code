# ===== 构建阶段 =====
FROM maven:3.6-jdk-8 AS build
WORKDIR /app
ENV MAVEN_OPTS="-Xms256m -Xmx512m"

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# ===== 运行阶段 =====
FROM tomcat:8.5-jre8
COPY --from=build /app/target/aicode-platform.war /usr/local/tomcat/webapps/ROOT.war
RUN mkdir -p /opt/aicode/uploads/avatars
ENV CATALINA_OPTS="-Xms256m -Xmx384m -DMYSQL_HOST=mysql -Dorg.apache.catalina.connector.URI_ENCODING=UTF-8 -Dorg.apache.catalina.connector.USE_BODY_ENCODING_FOR_QUERY_STRING=true"
EXPOSE 8080
CMD ["catalina.sh", "run"]
