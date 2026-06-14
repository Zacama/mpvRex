# Building mpvRex

This guide explains how to build the app from source and install it on a device.
No NDK or native toolchain is required — libmpv ships as a prebuilt `.aar`
(`app/libs/mpv-android-lib-*.aar`), so a standard Android/Gradle setup is enough.

## Requirements

| Tool | Version | Notes |
|---|---|---|
| JDK | 17 (21 also works) | Source/target is Java 17; the build runs fine on JDK 21 too |
| Android SDK Platform | `android-36` | `compileSdk = 36` |
| Android SDK Build-Tools | `36.0.0` | |
| Android SDK Platform-Tools | latest | provides `adb` for installing |
| Gradle | — | Use the bundled wrapper (`./gradlew`), Gradle 9.2; do not install Gradle manually |

`minSdk = 26` (Android 8.0), `targetSdk = 36`.

## One-time setup (macOS / Homebrew)

This is the exact path used to build this fork. Adapt paths for Linux/Windows.

```bash
# 1. JDK + Android command-line tools
brew install openjdk@21 android-commandlinetools

# 2. Install the required SDK packages and accept licenses
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
yes | sdkmanager --licenses
sdkmanager "platforms;android-36" "build-tools;36.0.0" "platform-tools"

# 3. Point the project at the SDK (local.properties is git-ignored)
echo "sdk.dir=/opt/homebrew/share/android-commandlinetools" > local.properties
```

> If you use Android Studio instead, just open the project — it manages the JDK
> and SDK for you, and you can skip the manual steps above.

**The JAVA_HOME gotcha:** `openjdk@21` from Homebrew is keg-only (not on `PATH`),
so a bare `./gradlew` fails with *"Unable to locate a Java Runtime."* Always
export `JAVA_HOME` first (or set it permanently in your shell profile):

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
```

## Build a debug APK

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
./gradlew :app:assembleDebug
```

Output goes to `app/build/outputs/apk/debug/`. The `abi` split produces one APK
per architecture plus a universal one:

| File | Target |
|---|---|
| `app-arm64-v8a-debug.apk` | Modern phones (2017+) — **recommended** |
| `app-armeabi-v7a-debug.apk` | Older 32-bit phones |
| `app-x86-debug.apk`, `app-x86_64-debug.apk` | Emulators |
| `app-universal-debug.apk` | Any device (largest, contains all ABIs) |

Debug builds use the `applicationId` suffix `.debug`, so they install
**alongside** a release build without conflict. They are signed with the local
debug keystore, so the in-app "check for updates" feature cannot update them.

Faster inner-loop tasks:

```bash
./gradlew :app:compileDebugKotlin   # type-check only, no APK
```

## Build a release APK

Release builds enable R8 minification + resource shrinking (`isMinifyEnabled`,
`isShrinkResources`) and must be signed with your own keystore.

```bash
# Create a keystore once
keytool -genkey -v -keystore ~/mpvrex-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 -alias mpvrex

# Build, passing the signing config as Gradle properties
./gradlew :app:assembleRelease \
  -PreleaseKeyStore=$HOME/mpvrex-release.jks \
  -PreleaseKeyStorePassword=YOUR_STORE_PASSWORD \
  -PreleaseKeyAlias=mpvrex \
  -PreleaseKeyPassword=YOUR_KEY_PASSWORD
```

Without the `-PreleaseKeyStore` property the release build is left unsigned
(`signingConfig` is skipped). Output: `app/build/outputs/apk/release/`.

> A self-signed release build is not signed with the upstream developer's key,
> so the in-app updater still won't apply official GitHub releases over it.

## Install on a device

**Via USB (adb):**

```bash
# Enable Developer Options → USB debugging on the phone, plug it in
adb devices                                   # confirm the device is listed
adb install -r app/build/outputs/apk/debug/app-arm64-v8a-debug.apk
```

`-r` reinstalls/updates in place, keeping app data (works because debug builds
share the same signature + `applicationId`).

**Manual:** copy the chosen APK to the phone (AirDrop, cloud, USB storage) and
tap it; allow "install from unknown sources" when prompted.

## Verifying translations (i18n)

After changing strings, check resource consistency before building. See
[docs/i18n.md](i18n.md) for the key-consistency / placeholder-check script and
the rule that *extra* keys in a translation break the build while *missing* keys
fall back to English. The release gate is:

```bash
./gradlew :app:lintVitalRelease   # must pass; catches blocking lint issues
```

## Troubleshooting

- **"Unable to locate a Java Runtime"** — `JAVA_HOME` is not set; see the gotcha
  above.
- **"SDK location not found"** — create `local.properties` with `sdk.dir=...`,
  or set the `ANDROID_HOME` environment variable.
- **"Failed to find Build Tools / platform android-36"** — run the `sdkmanager`
  install step above.
- **Slow first build** — Gradle downloads the wrapper and dependencies on the
  first run; subsequent builds are cached and fast.
