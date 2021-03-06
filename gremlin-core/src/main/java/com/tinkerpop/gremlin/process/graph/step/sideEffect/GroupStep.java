package com.tinkerpop.gremlin.process.graph.step.sideEffect;

import com.tinkerpop.gremlin.process.Traversal;
import com.tinkerpop.gremlin.process.TraversalEngine;
import com.tinkerpop.gremlin.process.Traverser;
import com.tinkerpop.gremlin.process.computer.MapReduce;
import com.tinkerpop.gremlin.process.graph.marker.EngineDependent;
import com.tinkerpop.gremlin.process.graph.marker.FunctionHolder;
import com.tinkerpop.gremlin.process.graph.marker.MapReducer;
import com.tinkerpop.gremlin.process.graph.marker.Reversible;
import com.tinkerpop.gremlin.process.graph.marker.SideEffectCapable;
import com.tinkerpop.gremlin.process.graph.step.sideEffect.mapreduce.GroupMapReduce;
import com.tinkerpop.gremlin.process.util.BulkSet;
import com.tinkerpop.gremlin.process.util.TraversalHelper;
import com.tinkerpop.gremlin.structure.Graph;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class GroupStep<S, K, V, R> extends SideEffectStep<S> implements SideEffectCapable, FunctionHolder<Object, Object>, Reversible, EngineDependent, MapReducer<Object, Collection, Object, Object, Map> {

    private final Map<K, R> reduceMap;
    private char state = 'k';
    private Function<S, K> keyFunction = s -> (K) s;
    private Function<S, V> valueFunction = s -> (V) s;
    private Function<Collection<V>, R> reduceFunction = null;
    private final String sideEffectKey;
    private boolean vertexCentric = false;

    public GroupStep(final Traversal traversal, final String sideEffectKey) {
        super(traversal);
        this.sideEffectKey = null == sideEffectKey ? this.getLabel() : sideEffectKey;
        TraversalHelper.verifySideEffectKeyIsNotAStepLabel(this.sideEffectKey, this.traversal);
        this.reduceMap = new HashMap<>();
        this.traversal.sideEffects().registerSupplierIfAbsent(this.sideEffectKey, HashMap<K, Collection<V>>::new);
        this.setConsumer(traverser -> {
            final Map<K, Collection<V>> groupByMap = traverser.sideEffects().get(this.sideEffectKey);
            doGroup(traverser, groupByMap, this.keyFunction, this.valueFunction);
            if (!this.vertexCentric) {
                if (null != reduceFunction && !this.starts.hasNext()) {
                    doReduce(groupByMap, this.reduceMap, this.reduceFunction);
                    traverser.sideEffects().set(this.sideEffectKey, this.reduceMap);
                }
            }
        });
    }

    @Override
    public String getSideEffectKey() {
        return this.sideEffectKey;
    }

    private static <S, K, V> void doGroup(final Traverser<S> traverser, final Map<K, Collection<V>> groupMap, final Function<S, K> keyFunction, final Function<S, V> valueFunction) {
        final K key = keyFunction.apply(traverser.get());
        final V value = valueFunction.apply(traverser.get());
        Collection<V> values = groupMap.get(key);
        if (null == values) {
            values = new BulkSet<>();
            groupMap.put(key, values);
        }
        TraversalHelper.addToCollectionUnrollIterator(values, value, traverser.bulk());
    }

    private static <K, V, R> void doReduce(final Map<K, Collection<V>> groupMap, final Map<K, R> reduceMap, final Function<Collection<V>, R> reduceFunction) {
        groupMap.forEach((k, vv) -> reduceMap.put(k, reduceFunction.apply(vv)));
    }

    @Override
    public void onEngine(final TraversalEngine traversalEngine) {
        this.vertexCentric = traversalEngine.equals(TraversalEngine.COMPUTER);
    }

    @Override
    public MapReduce<Object, Collection, Object, Object, Map> getMapReduce() {
        return new GroupMapReduce(this);
    }

    @Override
    public String toString() {
        return TraversalHelper.makeStepString(this, this.sideEffectKey);
    }

    public Function<Collection<V>, R> getReduceFunction() {
        return this.reduceFunction;
    }

    @Override
    public void addFunction(final Function<Object, Object> function) {
        if ('k' == this.state) {
            this.keyFunction = (Function) function;
            this.state = 'v';
        } else if ('v' == this.state) {
            this.valueFunction = (Function) function;
            this.state = 'r';
        } else if ('r' == this.state) {
            this.reduceFunction = (Function) function;
            this.state = 'x';
        } else {
            throw new IllegalStateException("The key, value, and reduce functions for group()-step have already been set");
        }
    }

    @Override
    public List<Function<Object, Object>> getFunctions() {
        if (this.state == 'k') {
            return Collections.emptyList();
        } else if (this.state == 'v') {
            return Arrays.asList((Function) this.keyFunction);
        } else if (this.state == 'r') {
            return Arrays.asList((Function) this.keyFunction, (Function) this.valueFunction);
        } else {
            return Arrays.asList((Function) this.keyFunction, (Function) this.valueFunction, (Function) this.reduceFunction);
        }
    }
}
