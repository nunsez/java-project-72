FROM openjdk:21-jdk-bookworm AS build
WORKDIR /app
COPY . .
RUN ./gradlew installShadowDist --no-daemon --warning-mode all

FROM openjdk:21-jdk-bookworm AS runner
COPY --from=build /app/build/install/app-shadow /app
CMD ["/app/bin//app"]
