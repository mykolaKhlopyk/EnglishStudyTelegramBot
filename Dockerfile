FROM openjdk:17-alpine
EXPOSE 8080
COPY build/libs/EnglishStudyTelegramBot-*.jar telegram-bot.jar
ENTRYPOINT ["java", "-jar", "telegram-bot.jar"]