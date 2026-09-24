package de.kitshn

import kotlin.test.Test
import kotlin.test.assertEquals

class ReferenceCountedResourceTest {

    @Test
    fun resourceRemainsActiveUntilLastUserReleasesIt() {
        val events = mutableListOf<String>()
        val resource = ReferenceCountedResource<String>(
            onFirstAcquire = { events += "enable:$it" },
            onLastRelease = { events += "disable:$it" }
        )

        resource.acquire("screen")
        resource.acquire("screen")
        resource.release("screen")

        assertEquals(listOf("enable:screen"), events)

        resource.release("screen")

        assertEquals(listOf("enable:screen", "disable:screen"), events)
    }

    @Test
    fun resourcesAreCountedIndependently() {
        val events = mutableListOf<String>()
        val resource = ReferenceCountedResource<String>(
            onFirstAcquire = { events += "enable:$it" },
            onLastRelease = { events += "disable:$it" }
        )

        resource.acquire("first")
        resource.acquire("second")
        resource.release("first")
        resource.release("second")

        assertEquals(
            listOf("enable:first", "enable:second", "disable:first", "disable:second"),
            events
        )
    }
}
