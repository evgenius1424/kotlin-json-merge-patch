@file:Suppress("unused")

package io.github.evgenius1424.jsonmergepatch

import kotlinx.serialization.json.*
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput, Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
open class JsonMergePatchBenchmarks {

    private lateinit var smallTarget: JsonElement
    private lateinit var smallPatch: JsonElement
    private lateinit var mediumTarget: JsonElement
    private lateinit var mediumPatch: JsonElement
    private lateinit var largeTarget: JsonElement
    private lateinit var largePatch: JsonElement
    private lateinit var deeplyNestedTarget: JsonElement
    private lateinit var deeplyNestedPatch: JsonElement
    private lateinit var arrayTarget: JsonElement
    private lateinit var arrayPatch: JsonElement
    private lateinit var nullRemovalTarget: JsonElement
    private lateinit var nullRemovalPatch: JsonElement
    private lateinit var mixedOperationsTarget: JsonElement
    private lateinit var mixedOperationsPatch: JsonElement
    private lateinit var emptyPatch: JsonElement

    @Setup
    fun setup() {
        setupSmall()
        setupMedium()
        setupLarge()
        setupDeepNested()
        setupArrayReplacement()
        setupNullRemoval()
        setupMixedOperations()
        emptyPatch = buildJsonObject { }
    }

    @Benchmark
    fun smallObjectMerge(): JsonElement = smallTarget.mergePatch(smallPatch)

    @Benchmark
    fun mediumObjectMerge(): JsonElement = mediumTarget.mergePatch(mediumPatch)

    @Benchmark
    fun largeObjectMerge(): JsonElement = largeTarget.mergePatch(largePatch)

    @Benchmark
    fun deeplyNestedMerge(): JsonElement = deeplyNestedTarget.mergePatch(deeplyNestedPatch)

    @Benchmark
    fun arrayReplacement(): JsonElement = arrayTarget.mergePatch(arrayPatch)

    @Benchmark
    fun nullRemoval(): JsonElement = nullRemovalTarget.mergePatch(nullRemovalPatch)

    @Benchmark
    fun mixedOperations(): JsonElement = mixedOperationsTarget.mergePatch(mixedOperationsPatch)

    @Benchmark
    fun emptyPatchNoOp(): JsonElement = mediumTarget.mergePatch(emptyPatch)

    @Benchmark
    fun primitiveReplacement(): JsonElement = smallTarget.mergePatch(buildJsonObject { put("replace", "all") })

    private fun setupSmall() {
        smallTarget = buildJsonObject {
            put("name", "John")
            put("age", 30)
            put("city", "NYC")
        }
        smallPatch = buildJsonObject {
            put("age", 31)
            put("email", "john@example.com")
        }
    }

    private fun setupMedium() {
        mediumTarget = buildJsonObject {
            put("id", "user-123")
            put("username", "johndoe")
            put("email", "john@example.com")
            put("firstName", "John")
            put("lastName", "Doe")
            put("age", 30)
            put("isActive", true)
            put("role", "admin")
            put("department", "Engineering")
            put("location", "NYC")
            put("phone", "+1-555-0123")
            put("extension", "456")
            put("manager", "jane-smith")
            put("salary", 100000)
            put("currency", "USD")
            put("startDate", "2020-01-15")
            put("lastLogin", "2025-12-24T10:30:00Z")
            put("loginCount", 1024)
            put("failedLogins", 0)
            put("preferences", buildJsonObject {
                put("theme", "dark")
                put("notifications", true)
                put("language", "en")
            })
        }
        mediumPatch = buildJsonObject {
            put("age", 31)
            put("lastLogin", "2025-12-25T09:15:00Z")
            put("loginCount", 1025)
            put("preferences", buildJsonObject {
                put("theme", "light")
                put("fontSize", 14)
            })
            put("skills", buildJsonObject {
                put("kotlin", "expert")
                put("java", "advanced")
            })
        }
    }

    private fun setupLarge() {
        largeTarget = buildJsonObject {
            repeat(100) { i -> put("field$i", "value$i") }
            put("metadata", buildJsonObject {
                repeat(20) { i -> put("meta$i", "metaValue$i") }
            })
            put("config", buildJsonObject {
                put("database", buildJsonObject {
                    put("host", "localhost")
                    put("port", 5432)
                    put("name", "mydb")
                })
                put("cache", buildJsonObject {
                    put("enabled", true)
                    put("ttl", 3600)
                })
            })
        }
        largePatch = buildJsonObject {
            repeat(10) { i -> put("field$i", "updated$i") }
            put("metadata", buildJsonObject {
                repeat(5) { i -> put("meta$i", "updated$i") }
            })
            put("config", buildJsonObject {
                put("cache", buildJsonObject { put("ttl", 7200) })
            })
            repeat(5) { i -> put("newField$i", "new$i") }
        }
    }

    private fun setupDeepNested() {
        deeplyNestedTarget = buildNestedObject(8, "value") { put("final", "deepValue") }
        deeplyNestedPatch = buildNestedObject(8, null) {
            put("final", "updatedDeepValue")
            put("newDeep", "added")
        }
    }

    private fun setupArrayReplacement() {
        arrayTarget = buildJsonObject {
            put("data", buildJsonObject {
                put("items", buildJsonObject {
                    put("id", 1)
                    put("name", "item")
                })
            })
        }
        arrayPatch = buildJsonObject {
            put("data", buildJsonObject { put("items", "replaced") })
        }
    }

    private fun setupNullRemoval() {
        nullRemovalTarget = buildJsonObject {
            put("keep1", "value1")
            put("remove1", "toRemove1")
            put("keep2", "value2")
            put("remove2", "toRemove2")
            put("keep3", "value3")
            put("remove3", "toRemove3")
            put("nested", buildJsonObject {
                put("keepNested1", "nested1")
                put("removeNested1", "toRemove")
                put("keepNested2", "nested2")
            })
        }
        nullRemovalPatch = buildJsonObject {
            put("remove1", JsonNull)
            put("remove2", JsonNull)
            put("remove3", JsonNull)
            put("nested", buildJsonObject { put("removeNested1", JsonNull) })
        }
    }

    private fun setupMixedOperations() {
        mixedOperationsTarget = buildJsonObject {
            put("userId", "user-456")
            put("status", "active")
            put("oldField", "willBeRemoved")
            put("updateMe", "oldValue")
            put("nested", buildJsonObject {
                put("keep", "keepThis")
                put("update", "oldNested")
                put("remove", "removeThis")
            })
            put("deepNested", buildJsonObject {
                put("level1", buildJsonObject { put("data", "original") })
            })
        }
        mixedOperationsPatch = buildJsonObject {
            put("status", "inactive")
            put("oldField", JsonNull)
            put("updateMe", "newValue")
            put("newField", "added")
            put("nested", buildJsonObject {
                put("update", "newNested")
                put("remove", JsonNull)
                put("add", "addedNested")
            })
            put("deepNested", buildJsonObject {
                put("level1", buildJsonObject {
                    put("data", "updated")
                    put("newData", "addedDeep")
                })
            })
        }
    }
}

private fun buildNestedObject(
    depth: Int,
    dataValue: String?,
    leafContent: JsonObjectBuilder.() -> Unit
): JsonElement = buildJsonObject {
    if (depth == 1) {
        dataValue?.let { put("data", it) }
        leafContent()
    } else {
        dataValue?.let { put("data", it) }
        put("level${9 - depth}", buildNestedObject(depth - 1, dataValue, leafContent))
    }
}