package org.scapy.core.mod;

import org.scapy.core.accessors.IRenderableNode;
import org.scapy.utils.Filter;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class for filtering what models get rendered by the game engine.
 *
 * <p>
 * This class internally contains a set of <code>Filter</code> objects, each of
 * which establish a condition for model rendering. When the game engine begins
 * rendering a model, it will pass through all of the filters. If <em>any</em>
 * of the filters do not match the model, then the model with not be rendered by
 * the game engine. A <code>ModelRenderEvent</code> will be generated,
 * regardless of whether the model gets filtered out.
 *
 * <p>
 * Initially, the internal set of filters is empty. Filters can be added or
 * removed using the <code>addRenderableFilter</code> or
 * <code>removeRenderableFilter</code> methods, respectively. Because the
 * internal collection is a type of <code>Set</code>, duplicate filters are not
 * retained.
 *
 * <p>
 * The methods in this class are thread-safe.
 *
 * @author Martin Tuskevicius
 */
public final class RenderingFilters {

    private static final Set<Filter<IRenderableNode>> filterSet = Collections.newSetFromMap(new ConcurrentHashMap<Filter<IRenderableNode>, Boolean>());

    /**
     * Prevents external initialization.
     */
    private RenderingFilters() {

    }

    /**
     * Adds a filter for rendering models.
     *
     * @param filter the filter.
     * @throws NullPointerException if <code>filter</code> is <code>null</code>.
     */
    public static void addRenderableFilter(Filter<IRenderableNode> filter) {
        filterSet.add(filter);
    }

    /**
     * Removes a filter for rendering models.
     *
     * @param filter the filter.
     * @throws NullPointerException if <code>filter</code> is <code>null</code>.
     */
    public static void removeRenderableFilter(Filter<IRenderableNode> filter) {
        filterSet.remove(filter);
    }

    static boolean matches(IRenderableNode model) {
        for (Filter<IRenderableNode> modelFilter : filterSet) {
            if (!modelFilter.matches(model)) {
                return false;
            }
        }
        return true;
    }
}