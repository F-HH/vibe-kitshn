package de.kitshn

/**
 * Keeps a shared resource active until every user has released it.
 *
 * This is useful for platform resources that are represented by one global flag, but can be
 * requested by multiple independently composed screens.
 */
internal class ReferenceCountedResource<T>(
    private val onFirstAcquire: (T) -> Unit,
    private val onLastRelease: (T) -> Unit
) {
    private val referenceCounts = mutableMapOf<T, Int>()

    fun acquire(resource: T) {
        val referenceCount = referenceCounts[resource] ?: 0
        if(referenceCount == 0) onFirstAcquire(resource)
        referenceCounts[resource] = referenceCount + 1
    }

    fun release(resource: T) {
        val referenceCount = referenceCounts[resource] ?: return

        if(referenceCount == 1) {
            referenceCounts.remove(resource)
            onLastRelease(resource)
        } else {
            referenceCounts[resource] = referenceCount - 1
        }
    }
}
