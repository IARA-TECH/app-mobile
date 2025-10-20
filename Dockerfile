FROM openjdk:17-jdk-slim AS build

RUN apt-get update && apt-get install -y wget unzip && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /usr/local/android-sdk/cmdline-tools
WORKDIR /usr/local/android-sdk/cmdline-tools
RUN wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O cmdline-tools.zip && \
    unzip cmdline-tools.zip -d temp && \
    rm cmdline-tools.zip && \
    mv temp/cmdline-tools latest

ENV ANDROID_HOME=/usr/local/android-sdk
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

RUN yes | sdkmanager --licenses
RUN sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

WORKDIR /app

COPY app/google-services.json app/google-services.json

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew assembleDebug

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/app/build/outputs/apk/debug/app-debug.apk /app/app-debug.apk

CMD ["bash"]
