FROM openjdk:17
EXPOSE 8080
COPY build/libs/EnglishStudyTelegramBot-*.jar telegram-bot1.jar
ENTRYPOINT ["java", "-jar", "telegram-bot1.jar"]
