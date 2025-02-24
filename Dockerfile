FROM openjdk:8-jdk

WORKDIR /app

COPY . .

RUN chmod +x gradlew

RUN ./gradlew desktop:dist
RUN cp -R android/assets/* desktop/build/libs/

RUN ./gradlew server:dist
RUN cp -R android/assets/* server/build/libs/

RUN ./gradlew html:dist