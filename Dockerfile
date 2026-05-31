# =========================
# STAGE 1: BUILD JAR
# =========================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

# build project
RUN mvn clean package -DskipTests


# =========================
# STAGE 2: RUNTIME IMAGE
# =========================
FROM eclipse-temurin:17-jdk

WORKDIR /app

# =========================
# INSTALL DEPENDENCIES (FIXED)
# =========================
RUN apt-get update --fix-missing && apt-get install -y \
    wget \
    curl \
    unzip \
    ca-certificates \
    gnupg \
    fonts-liberation \
    libnss3 \
    libgbm1 \
    libasound2 \
    libx11-xcb1 \
    libatk-bridge2.0-0 \
    libgtk-3-0 \
    libxdamage1 \
    libxrandr2 \
    libu2f-udev \
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
# COPY JAR FROM BUILD
# =========================
COPY --from=build /app/target/*.jar app.jar


# =========================
# RUN APPLICATION
# =========================
CMD ["java", "-jar", "app.jar"]
