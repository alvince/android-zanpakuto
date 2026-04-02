# Repository Guidelines

## Project Structure & Module Organization
This repository is a multi-module Android/Kotlin library suite. Reusable libraries live in `core`, `lifecycle`, `view`, `viewbinding`, `databinding`, `reactivex-rxjava2`, and `serialization-gson`. Demo apps live in `sample` and `sample-databinding`. Each module follows the standard Android layout: `src/main` for production code and resources, `src/test` for local JVM tests, and `src/androidTest` only where instrumentation coverage is needed.

## Build, Test, and Development Commands
Use the Gradle wrapper from the repository root.

- `./gradlew assemble`: builds all modules and sample apps.
- `./gradlew test`: runs JVM unit tests across modules.
- `./gradlew connectedAndroidTest`: runs instrumentation tests for sample apps on a device/emulator.
- `./gradlew :sample:installDebug`: installs the sample app locally for manual verification.
- `./gradlew clean`: removes build outputs.

Publishing is configured in the library modules; release tasks depend on Sonatype credentials from `local.properties`, Gradle properties, or environment variables.

## Coding Style & Naming Conventions
Follow `.editorconfig`: UTF-8, LF endings, 4-space indentation, final newline, and a `160` character soft limit for Kotlin files. Keep Kotlin code idiomatic and consistent with existing extension-heavy APIs. Use `UpperCamelCase` for types, `lowerCamelCase` for functions and properties, and keep package names under `cn.alvince.zanpakuto.<module>`. Name Android resources with lowercase underscore style, for example `home_main_activity.xml`.

## Testing Guidelines
JUnit is the default unit test framework in library modules. Place tests under `src/test/java/...` and name them `*Test` or `*UnitTest`, matching the existing pattern such as `TimeUnitTest.kt`. Put device-only coverage in `src/androidTest`. Add tests for behavior changes in the affected module before updating sample apps.

## Commit & Pull Request Guidelines
Recent history uses scoped Conventional Commit prefixes such as `feat:[core] ...`, `fix:[gson] ...`, `chore: ...`, and `doc: ...`. Keep that format and include the module scope when applicable. Pull requests should describe the changed module(s), summarize user-visible API or behavior changes, link related issues, and include screenshots or recordings when sample UI behavior changes.

## Security & Configuration Tips
Do not commit `local.properties` or Sonatype credentials. Keep repository URLs and publishing settings aligned with the existing Gradle configuration, and prefer updating shared versions in `config.gradle` and `lib_depends.gradle` rather than hardcoding them per module.
