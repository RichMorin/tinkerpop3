package com.tinkerpop.gremlin.tinkergraph.process.graph;

import com.tinkerpop.gremlin.process.TraversalEngine;
import com.tinkerpop.gremlin.process.computer.GraphComputer;
import com.tinkerpop.gremlin.process.graph.DefaultGraphTraversal;
import com.tinkerpop.gremlin.process.graph.GraphTraversal;
import com.tinkerpop.gremlin.structure.Element;
import com.tinkerpop.gremlin.structure.Graph;
import com.tinkerpop.gremlin.tinkergraph.process.graph.step.map.TinkerGraphStep;
import com.tinkerpop.gremlin.tinkergraph.process.graph.strategy.TinkerGraphStepTraversalStrategy;
import com.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import com.tinkerpop.gremlin.tinkergraph.structure.TinkerHelper;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class TinkerGraphTraversal<S, E> extends DefaultGraphTraversal<S, E> {

    public TinkerGraphTraversal(final TinkerGraph graph, final Class<? extends Element> elementClass) {
        this.memory().set(Graph.Key.hidden("g"), graph);
        this.strategies().register(new TinkerGraphStepTraversalStrategy());
        this.addStep(new TinkerGraphStep(this, elementClass, graph));
    }

    public GraphTraversal<S, E> submit(final TraversalEngine engine) {
        if (engine instanceof GraphComputer)
            TinkerHelper.prepareTraversalForComputer(this);
        return super.submit(engine);
    }
}