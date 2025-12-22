package io.github.evgenius1424.jsonmergepatch

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.intellij.lang.annotations.Language
import kotlin.test.Test
import kotlin.test.assertEquals

class JsonMergePatchTest {

    @Test
    fun `array patch replaces entire target`() {
        testMerge("""{"a":"b"}""", """["c"]""", """["c"]""")
    }

    @Test
    fun `string patch replaces entire target`() {
        testMerge("""{"a":"foo"}""", """"bar"""", """"bar"""")
    }

    @Test
    fun `null patch replaces entire target`() {
        testMerge("""{"a":"foo"}""", """null""", """null""")
    }

    @Test
    fun `empty patch preserves object target`() {
        testMerge("""{"a":"b"}""", """{}""", """{"a":"b"}""")
    }

    @Test
    fun `empty patch converts non-object target to empty object`() {
        testMerge("""42""", """{}""", """{}""")
    }

    @Test
    fun `patch preserves properties not in patch`() {
        testMerge("""{"a":"1","b":"2"}""", """{"a":"x"}""", """{"a":"x","b":"2"}""")
    }

    @Test
    fun `patch adds new property`() {
        testMerge("""{"a":"b"}""", """{"b":"c"}""", """{"a":"b","b":"c"}""")
    }

    @Test
    fun `patch replaces existing property`() {
        testMerge("""{"a":"b"}""", """{"a":"c"}""", """{"a":"c"}""")
    }

    @Test
    fun `null removes existing property`() {
        testMerge("""{"a":"b","c":"d"}""", """{"a":null}""", """{"c":"d"}""")
    }

    @Test
    fun `null removes only property leaving empty object`() {
        testMerge("""{"a":"b"}""", """{"a":null}""", """{}""")
    }

    @Test
    fun `null for non-existent property is no-op`() {
        testMerge("""{"a":"b"}""", """{"c":null}""", """{"a":"b"}""")
    }

    @Test
    fun `null in patch ignored when target is non-object`() {
        testMerge("""42""", """{"a":null}""", """{}""")
    }

    @Test
    fun `existing null in target preserved when not patched`() {
        testMerge("""{"e":null}""", """{"a":1}""", """{"e":null,"a":1}""")
    }

    @Test
    fun `nested object merges recursively`() {
        testMerge("""{"a":{"b":"1","c":"2"}}""", """{"a":{"b":"x"}}""", """{"a":{"b":"x","c":"2"}}""")
    }

    @Test
    fun `null removes nested property`() {
        testMerge("""{"a":{"b":"c"}}""", """{"a":{"b":"d","c":null}}""", """{"a":{"b":"d"}}""")
    }

    @Test
    fun `empty nested patch preserves nested object`() {
        testMerge("""{"a":{"b":"c"}}""", """{"a":{}}""", """{"a":{"b":"c"}}""")
    }

    @Test
    fun `object patch replaces primitive value`() {
        testMerge("""{"a":"string"}""", """{"a":{"b":"c"}}""", """{"a":{"b":"c"}}""")
    }

    @Test
    fun `array patch replaces object value`() {
        testMerge("""{"a":"c"}""", """{"a":["b"]}""", """{"a":["b"]}""")
    }

    @Test
    fun `object patch replaces array value`() {
        testMerge("""{"a":["b"]}""", """{"a":"c"}""", """{"a":"c"}""")
    }

    @Test
    fun `array target replaced by object patch`() {
        testMerge("""[1,2]""", """{"a":"b","c":null}""", """{"a":"b"}""")
    }

    @Test
    fun `array patch replaces array target`() {
        testMerge("""["a","b"]""", """["c","d"]""", """["c","d"]""")
    }

    @Test
    fun `deeply nested null creates empty nested object`() {
        testMerge("""{}""", """{"a":{"bb":{"ccc":null}}}""", """{"a":{"bb":{}}}""")
    }

    @Test
    fun `deeply nested null with non-object target`() {
        testMerge("""{"a":"string"}""", """{"a":{"b":{"c":null}}}""", """{"a":{"b":{}}}""")
    }

    @Test
    fun `patch replaces array containing object`() {
        testMerge("""{"a":[{"b":"c"}]}""", """{"a":[1]}""", """{"a":[1]}""")
    }

    @Test
    fun `boolean patch replaces entire target`() {
        testMerge("""{"a":"b"}""", """true""", """true""")
    }

    @Test
    fun `number patch replaces entire target`() {
        testMerge("""{"a":"b"}""", """42""", """42""")
    }

    @Test
    fun `null in patch removes property even when target value is null`() {
        testMerge("""{"a":null,"b":"x"}""", """{"a":null}""", """{"b":"x"}""")
    }

    @Test
    fun `empty object in patch creates empty object property`() {
        testMerge("""{}""", """{"a":{}}""", """{"a":{}}""")
    }

    @Test
    fun `nested empty object in patch creates nested objects`() {
        testMerge("""{}""", """{"a":{"b":{}}}""", """{"a":{"b":{}}}""")
    }

    @Test
    fun `nested null on non-existent nested property creates empty parent`() {
        testMerge("""{}""", """{"a":{"b":null}}""", """{"a":{}}""")
    }

    @Test
    fun `boolean value inside object replaces primitive`() {
        testMerge("""{"a":1,"b":"x"}""", """{"a":false}""", """{"a":false,"b":"x"}""")
    }

    @Test
    fun `number value inside object replaces primitive`() {
        testMerge("""{"a":1}""", """{"a":2}""", """{"a":2}""")
    }

    private val String.json: JsonElement get() = Json.parseToJsonElement(this)

    private fun testMerge(
        @Language("JSON") target: String,
        @Language("JSON") patch: String,
        @Language("JSON") expected: String
    ) {
        assertEquals(expected.json, target.json.mergePatch(patch.json))
    }
}
