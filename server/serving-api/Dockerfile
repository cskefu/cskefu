FROM adoptopenjdk/openjdk15:jdk-15.0.1_9-alpine-slim as build
WORKDIR /workspace/app
COPY target/core*.jar /workspace/app
RUN mkdir -p /workspace/app/dependency && (cd /workspace/app/dependency; jar -xf ../*.jar)


FROM adoptopenjdk/openjdk15:jdk-15.0.1_9-alpine-slim
VOLUME /tmp

ARG DEPENDENCY=/workspace/app/dependency
RUN addgroup --system app_group && adduser --system app_user --ingroup app_group --home /app
COPY --from=build --chown=app_user ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build --chown=app_user ${DEPENDENCY}/META-INF /app/META-INF
COPY --chown=app_user target/internal-bundles /app/internal-bundles
COPY --from=build --chown=app_user ${DEPENDENCY}/BOOT-INF/classes /app

USER app_user:app_group
WORKDIR /app

ENTRYPOINT ["java","-cp","./:lib/*","com.cskefu.serving.api.SampleApplication"]
