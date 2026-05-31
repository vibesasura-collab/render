# Stage 1: build Java app
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests

# Stage 2: runtime
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Install Chrome + dependencies (IMPORTANT for Selenium)
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    unzip \
    libnss3 \
    libgbm1 \
    libasound2 \
    libx11-xcb1 \
    libatk-bridge2.0-0 \
    libgtk-3-0 \
    libxdamage1 \
    libxrandr2 \
    libu2f-udev \
    fonts-liberation \
    && rm -rf /var/lib/apt/lists/*

# Install Google Chrome
RUN wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb \
    && dpkg -i google-chrome-stable_current_amd64.deb || apt-get -fy install \
    && rm google-chrome-stable_current_amd64.deb

# copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# run bot
CMD ["java", "-jar", "app.jar"]
