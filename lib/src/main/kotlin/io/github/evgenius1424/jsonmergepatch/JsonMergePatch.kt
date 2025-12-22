@file:Suppress("unused")

package io.github.evgenius1424.jsonmergepatch

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

/**
 * Applies a JSON Merge Patch to this [JsonElement] according to [RFC 7396](https://tools.ietf.org/rfc/rfc7396.txt).
 *
 * ```
 * val target = buildJsonObject {
 *     put("name", "John")
 *     put("age", 30)
 * }
 * val patch = buildJsonObject {
 *     put("age", 31)
 *     put("city", "NYC")
 * }
 * val result = target.mergePatch(patch)
 * // {"name":"John","age":31,"city":"NYC"}
 * ```
 */
public fun JsonElement.mergePatch(patch: JsonElement): JsonElement {
    if (patch !is JsonObject) return patch
    val target = this as? JsonObject ?: EmptyObject
    return when {
        patch.isEmpty() -> target
        else -> buildJsonObject {
            for ((key, value) in target) {
                if (key !in patch) put(key, value)
            }
            for ((key, patchValue) in patch) {
                when (patchValue) {
                    JsonNull -> Unit
                    is JsonObject -> put(key, (target[key] ?: EmptyObject).mergePatch(patchValue))
                    else -> put(key, patchValue)
                }
            }
        }
    }
}

private val EmptyObject = JsonObject(emptyMap())
