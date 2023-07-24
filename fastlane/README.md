fastlane documentation
----

# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```sh
xcode-select --install
```

For _fastlane_ installation instructions, see [Installing _fastlane_](https://docs.fastlane.tools/#installing-fastlane)

# Available Actions

## Android

### android upload_internal

```sh
[bundle exec] fastlane android upload_internal
```

Increment the build number and Compile a release build

### android playstore

```sh
[bundle exec] fastlane android playstore
```

Deploy a new version to the Google Play

### android build

```sh
[bundle exec] fastlane android build
```

Build Debug APK

### android release

```sh
[bundle exec] fastlane android release
```

Build Release APK

### android bundle

```sh
[bundle exec] fastlane android bundle
```

Build Release AAB bundle

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
