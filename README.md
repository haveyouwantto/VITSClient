# VITS TTS Client

VITS TTS Client is an Android application that allows you to connect to a VITS TTS API server and generate speech from text using the selected voice. It is designed to work with [tts-server-android](https://github.com/jing332/tts-server-android), which is a TTS dispatcher that can handle requests from multiple TTS engines.

## Requirements

- Android 7.0 or later
- A locally deployed instance of the VITS TTS API

## Installation

You can download the APK from the [releases page](https://github.com/haveyouwantto/VITSClient/releases) or build the app yourself.

## Usage

The first time you launch the app, you need to tap the "Settings" button and enter the API address. After that, tap the "Reload" button to load the available voices from the server.

The main screen of the app is used for testing whether the TTS server is working correctly. You can enter some text and click the "Speak" button to have the TTS server generate audio for the text.

## Deploying the VITS TTS API
The VITS TTS API is a separate project that needs to be deployed locally in order to use this app. The source code for the server can be found in the [haveyouwantto/MoeGoe](https://github.com/haveyouwantto/MoeGoe) repository, which is a fork of the original project.

## Note

This app is still in the early stages of development and its stability and performance cannot be guaranteed.

## Building from source

To build the app from source, follow these steps:

1. Clone the repository:
   ```
   git clone https://github.com/haveyouwantto/VITSClient.git
   ```
2. Open the project in Android Studio.
3. Build the app using Android Studio.

## License

This app is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

# VITS TTS客户端

VITS TTS客户端是一个安卓应用程序，可以连接到VITS TTS API服务器，并使用选择的语音从文本生成语音。它是设计为与[tts-server-android](https://github.com/jing332/tts-server-android)配合使用，后者是一个TTS分发程序，可以处理多个TTS引擎的请求。

## 要求

- Android 7.0或更高版本
- 一个本地部署的 VITS TTS API 实例

## 安装

您可以从[发布页面](https://github.com/haveyouwantto/VITSClient/releases)下载APK，或自行构建应用程序。

## 使用

第一次启动应用程序时，您需要点击“设置”按钮并输入API地址。然后，点击“重新加载”按钮以从服务器加载可用语音。

该应用程序的主屏幕用于测试TTS服务器是否正常工作。您可以输入一些文本并点击“播放”按钮，使TTS服务器为文本生成音频。

## 部署 VITS TTS API
VITS TTS API 是一个独立的项目，需要本地部署才能使用该应用程序。服务器的源代码可以在 [haveyouwantto/MoeGoe](https://github.com/haveyouwantto/MoeGoe) 代码库中找到，该代码库是原始项目的 fork。

## 注意

此应用程序仍处于早期开发阶段，其稳定性和性能不能得到保证。

## 从源代码构建

要从源代码构建应用程序，请按照以下步骤操作：

1. 克隆存储库：
   ```
   git clone https://github.com/haveyouwantto/VITSClient.git
   ```
2. 在Android Studio中打开项目。
3. 使用Android Studio构建应用程序。

## 许可证

此应用程序根据MIT许可证获得许可。有关详细信息，请参阅[LICENSE](LICENSE)文件。