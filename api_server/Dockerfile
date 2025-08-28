FROM amazoncorretto:17.0.7-alpine3.17
LABEL maintainer="hana0142@thomastone.co.kr"

# 빌드 타임 ARG (기본값 dev)
ARG SPRING_PROFILES_ACTIVE=dev
# 런타임 ENV (docker run 시 -e 로 덮어쓰기 가능)
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE

# JAR 복사
COPY ./build/libs/*.jar application.jar

# 실행
CMD ["java", "-jar", "./application.jar"]