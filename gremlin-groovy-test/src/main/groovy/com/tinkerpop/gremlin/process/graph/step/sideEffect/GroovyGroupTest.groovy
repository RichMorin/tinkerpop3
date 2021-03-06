package com.tinkerpop.gremlin.process.graph.step.sideEffect

import com.tinkerpop.gremlin.process.Traversal
import com.tinkerpop.gremlin.process.graph.step.ComputerTestHelper
import com.tinkerpop.gremlin.structure.Vertex

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public abstract class GroovyGroupTest {

    public static class StandardTest extends GroupTest {

        @Override
        public Traversal<Vertex, Map<String, Collection<Vertex>>> get_g_V_group_byXnameX() {
            g.V.group.by('name')
        }

        @Override
        public Traversal<Vertex, Map<String, Collection<String>>> get_g_V_hasXlangX_groupXaX_byXlangX_byXnameX_out_capXaX() {
            g.V.has('lang').group('a').by('lang').by('name').out.cap('a')
        }

        @Override
        public Traversal<Vertex, Map<String, Integer>> get_g_V_hasXlangX_group_byXlangX_byX1X_byXsizeX() {
            g.V.has('lang').group.by('lang').by { 1 }.by { it.size() }
        }

        @Override
        public Traversal<Vertex, Map<String, Integer>> get_g_V_asXxX_out_groupXaX_byXnameX_byXitX_byXsizeX_jumpXx_2X_capXaX() {
            g.V.as("x").out.group('a').by('name').by { it }.by { it.size() }.jump("x", 2).cap("a");
        }

        @Override
        public Traversal<Vertex, Map<String, Integer>> get_g_V_asXxX_out_groupXaX_byXnameX_byXitX_byXsizeX_jumpXx_loops_lt_2X_capXaX() {
            g.V.as("x").out().group('a').by('name').by { it }.by { it.size() }.jump("x") { it.loops() < 2 }.cap("a");
        }
    }

    public static class ComputerTest extends GroupTest {

        @Override
        public Traversal<Vertex, Map<String, Collection<Vertex>>> get_g_V_group_byXnameX() {
            ComputerTestHelper.compute("g.V.group.by('name')", g)
        }

        @Override
        public Traversal<Vertex, Map<String, Collection<String>>> get_g_V_hasXlangX_groupXaX_byXlangX_byXnameX_out_capXaX() {
            ComputerTestHelper.compute("g.V.has('lang').group('a').by('lang').by('name').out.cap('a')", g)
        }

        @Override
        public Traversal<Vertex, Map<String, Integer>> get_g_V_hasXlangX_group_byXlangX_byX1X_byXsizeX() {
            ComputerTestHelper.compute("g.V.has('lang').group.by('lang').by{1}.by{it.size()}", g)
        }

        @Override
        public Traversal<Vertex, Map<String, Integer>> get_g_V_asXxX_out_groupXaX_byXnameX_byXitX_byXsizeX_jumpXx_2X_capXaX() {
            ComputerTestHelper.compute("g.V.as('x').out.group('a').by('name').by{it}.by{it.size()}.jump('x', 2).cap('a')", g)
        }

        @Override
        public Traversal<Vertex, Map<String, Integer>> get_g_V_asXxX_out_groupXaX_byXnameX_byXitX_byXsizeX_jumpXx_loops_lt_2X_capXaX() {
            ComputerTestHelper.compute("g.V.as('x').out().group('a').by('name').by{it}.by{it.size()}.jump('x') { it.loops() < 2 }.cap('a')", g)
        }
    }
}
