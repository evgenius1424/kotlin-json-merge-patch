# Kotlin JSON Merge Patch

[![CI](https://github.com/evgenius1424/kotlin-json-merge-patch/actions/workflows/ci.yml/badge.svg)](https://github.com/evgenius1424/kotlin-json-merge-patch/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.evgenius1424/kotlin-json-merge-patch)](https://central.sonatype.com/artifact/io.github.evgenius1424/kotlin-json-merge-patch)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.8+-purple.svg)](https://kotlinlang.org)

A **lightweight**, **high-performance** Kotlin library implementing [RFC 7396](https://tools.ietf.org/rfc/rfc7396.txt) compliant JSON Merge Patch for `kotlinx.serialization`.

## Installation

```kotlin
implementation("io.github.evgenius1424:kotlin-json-merge-patch:1.0.0")
```

## Usage

```kotlin
import io.github.evgenius1424.jsonmergepatch.mergePatch
import kotlinx.serialization.json.Json

val target = Json.parseToJsonElement("""{"a": "b", "c": "d"}""")
val patch = Json.parseToJsonElement("""{"a": "z", "c": null, "e": "f"}""")

val result = target.mergePatch(patch)
// {"a": "z", "e": "f"}
```

## Requirements

- Kotlin 1.8+
- Java 8+
- kotlinx.serialization.json

## License

This project is licensed under the Apache License, Version 2.0. See the [LICENSE](LICENSE) file for details.