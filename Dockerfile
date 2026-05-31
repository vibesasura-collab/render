# =========================
# STAGE 1: BUILD
# =========================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests


# =========================
# STAGE 2: RUNTIME
# =========================
FROM eclipse-temurin:17-jdk

WORKDIR /app

# =========================
# INSTALL SYSTEM DEPENDENCIES (FIXED FOR RENDER UBUNTU)
# =========================
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    unzip \
    ca-certificates \
    gnupg \
    fonts-liberation \
    libnss3 \
    libgbm1 \
    libasound2t64 \
    libx11-xcb1 \
    libatk-bridge2.0-0 \
    libgtk-3-0 \
    libxdamage1 \
    libxrandr2 \
    libxcomposite1 \
    libxext6 \
    libxfixes3 \
    libdrm2 \
    libpangocairo-1.0-0 \
    libatk1.0-0 \
    libcups2 \
    && rm -rf /var/lib/apt/lists/*


# =========================
# INSTALL GOOGLE CHROME
# =========================
RUN wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb \
    && dpkg -i google-chrome-stable_current_amd64.deb || apt-get -fy install \
    && rm google-chrome-stable_current_amd64.deb


# =========================
# COPY JAR
# =========================
COPY --from=build /app/target/*.jar app.jar


# =========================
# RUN APP
# =========================
CMD ["java", "-jar", "app.jar"]
