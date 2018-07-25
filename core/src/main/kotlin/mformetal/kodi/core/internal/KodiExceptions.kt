package mformetal.kodi.core.internal

import mformetal.kodi.core.api.KodiKey
import mformetal.kodi.core.api.Scope

/**
 * @author - mbpeele on 10/11/17.
 */
internal class NoMatchingScopeException(scope: Scope) : IllegalArgumentException("No matching scope $scope exists.")

internal class NoMatchingKeyException(kodiKey: KodiKey<*>) : IllegalArgumentException("No matching key $kodiKey exists.")

internal class CyclicalNodeAdditionException(node: Node) : UnsupportedOperationException("Cannot cyclically add $node to itself.")

internal class RemovingNonChildNodeException(child: Node, parent: Node) : UnsupportedOperationException("Node $child is not a child of $parent")

internal class AmbiguousBindingException : IllegalStateException("Module cannot contain two of the same class without specifying a Tag.")

internal class DuplicateBindingException : IllegalStateException("Module cannot contain two of the same class with the same KodiKey.")

internal class InjectNotCalledException : RuntimeException("Call inject(Kodi) before accessing.")