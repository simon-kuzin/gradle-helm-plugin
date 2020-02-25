package org.unbrokendome.gradle.plugins.helm.dsl

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.unbrokendome.gradle.plugins.helm.util.mapProperty
import org.unbrokendome.gradle.plugins.helm.util.property
import javax.inject.Inject


/**
 * Defines options for linting Helm charts using the `helm lint` command.
 */
interface Linting {

    /**
     * If `true` (the default), run the linter.
     */
    val enabled: Property<Boolean>

    /**
     * If `true`, treat warnings from the linter as errors.
     */
    val strict: Property<Boolean>

    /**
     * Values to supply to the linter.
     */
    val values: MapProperty<String, Any>

    /**
     * Value files to supply to the linter.
     */
    val valueFiles: ConfigurableFileCollection
}


/**
 * Extension of [Linting] that supports setting values from a parent `Linting` instance.
 */
private interface LintingInternal : Linting, Hierarchical<Linting>


/**
 * Default implementation of [Linting].
 */
private open class DefaultLinting
@Inject constructor(objectFactory: ObjectFactory) : LintingInternal {

    final override val enabled: Property<Boolean> =
        objectFactory.property<Boolean>()
            .convention(true)

    final override val strict: Property<Boolean> =
        objectFactory.property()

    final override val values: MapProperty<String, Any> =
        objectFactory.mapProperty()

    final override val valueFiles: ConfigurableFileCollection =
          objectFactory.fileCollection()

    final override fun setParent(parent: Linting) {
        enabled.set(parent.enabled)
        strict.set(parent.strict)
        values.putAll(parent.values)
        valueFiles.from(parent.valueFiles)
    }
}


/**
 * Creates a new [Linting] object using the given [ObjectFactory].
 *
 * @receiver the Gradle [ObjectFactory] to create the object
 * @param parent the optional parent [Linting] object
 * @return the created [Linting] object
 */
internal fun ObjectFactory.createLinting(parent: Linting? = null): Linting =
    newInstance(DefaultLinting::class.java)
        .apply {
            parent?.let(this::setParent)
        }
