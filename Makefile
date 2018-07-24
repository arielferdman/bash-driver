-include .sdk/Makefile

$(if $(filter true,$(sdkloaded)),,$(error You must install bblfsh-sdk))

NATIVE_SCRIPT := native/src/main/bash/native.bash
DOWNLOAD_VENDOR = make
BUILD = gradle
JAR := native/build/libs/native-jar-with-dependencies.jar

test-native-internal:
	cd native; \
		$(DOWNLOAD_VENDOR); \
		$(BUILD) test

build-native-internal:
	cd native; \
		$(DOWNLOAD_VENDOR); \
		$(BUILD) shadowJar;
	mkdir -p $(BUILD_PATH)/bin || true;
	cp $(JAR) $(BUILD_PATH)/bin/;
	cp $(NATIVE_SCRIPT) $(BUILD_PATH)/bin/native;
	chmod +x $(BUILD_PATH)/bin/native
