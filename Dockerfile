FROM maven:3.8.4-amazoncorretto-17 AS builder
WORKDIR /workdir/server
COPY pom.xml /workdir/server/pom.xml

COPY src /workdir/server/src
RUN mvn clean install
RUN mkdir -p target/dependency
WORKDIR /workdir/server/target/dependency
RUN jar -xf ../*.jar

FROM openjdk:17
USER root
EXPOSE 8080

ARG DEPENDENCY=/workdir/server/target/dependency
COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app
CMD ["java","-cp","app:app/lib/*","com.backend.reservation.ReservationApplication"]