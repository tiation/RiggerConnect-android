# Android Build Environment
FROM openjdk:17-jdk-slim

# Install Android SDK
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/tools/bin:${ANDROID_HOME}/platform-tools

# Create android user and directory
RUN useradd -m android && \
    mkdir -p ${ANDROID_HOME} && \
    chown -R android:android ${ANDROID_HOME}

# Switch to android user
USER android
WORKDIR /home/android

# Download and install Android SDK
RUN wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip && \
    unzip -q commandlinetools-linux-9477386_latest.zip && \
    mkdir -p ${ANDROID_HOME}/cmdline-tools/latest && \
    mv cmdline-tools/* ${ANDROID_HOME}/cmdline-tools/latest/ && \
    rm -rf commandlinetools-linux-9477386_latest.zip cmdline-tools

# Accept licenses and install required SDK components
RUN yes | ${ANDROID_HOME}/cmdline-tools/latest/bin/sdkmanager --licenses && \
    ${ANDROID_HOME}/cmdline-tools/latest/bin/sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# Set up the app directory
WORKDIR /app
COPY --chown=android:android . .

# Make gradlew executable
RUN chmod +x gradlew

# Build the Android app
RUN ./gradlew assembleDebug

# Expose volume for APK output
VOLUME ["/app/app/build/outputs"]

CMD ["./gradlew", "assembleDebug"]
