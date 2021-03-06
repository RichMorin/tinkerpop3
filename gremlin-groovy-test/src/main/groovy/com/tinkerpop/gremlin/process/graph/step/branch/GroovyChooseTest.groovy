package com.tinkerpop.gremlin.process.graph.step.branch

import com.tinkerpop.gremlin.process.Traversal
import com.tinkerpop.gremlin.process.graph.step.ComputerTestHelper
import com.tinkerpop.gremlin.process.graph.step.branch.ChooseTest
import com.tinkerpop.gremlin.structure.Vertex

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public abstract class GroovyChooseTest {

    public static class StandardTest extends ChooseTest {

        @Override
        public Traversal<Vertex, String> get_g_V_chooseXname_length_5XoutXinX_name() {
            g.V.choose({ it.name.length() == 5 },
                    g.of().out(),
                    g.of().in()).name;
        }

        @Override
        public Traversal<Vertex, String> get_g_v1_chooseX0XoutX_name(Object v1Id) {
            g.V(v1Id).choose({ 0 }, [0: g.of().out.name]);
        }

        @Override
        public Traversal<Vertex, String> get_g_V_hasXageX_chooseXname_lengthX5_in_4_out_3_bothX_name() {
            g.V().has('age').choose({ it.name.length() },
                    [5: g.of().in(),
                     4: g.of().out(),
                     3: g.of().both()]).name;
        }

        @Override
        public Traversal<Vertex, Object> get_g_V_chooseXout_count_nextX2L_name_3L_valueMapX() {
            g.V().choose({ it.out().count().next(); }, [
                    2L: g.of().name,
                    3L: g.of().valueMap()])
        }
    }

    public static class ComputerTest extends ChooseTest {

        @Override
        public Traversal<Vertex, String> get_g_V_chooseXname_length_5XoutXinX_name() {
            ComputerTestHelper.compute("""g.V.choose({ it.name.length() == 5 },
                    g.of().out(),
                    g.of().in).name""", g);
        }

        @Override
        public Traversal<Vertex, String> get_g_v1_chooseX0XoutX_name(Object v1Id) {
            ComputerTestHelper.compute("g.V(${v1Id}).choose({ 0 }, [0: g.of().out.name])", g);
        }

        @Override
        public Traversal<Vertex, String> get_g_V_hasXageX_chooseXname_lengthX5_in_4_out_3_bothX_name() {
            ComputerTestHelper.compute("""g.V.has('age').choose({ it.name.length() },
                    [5: g.of().in,
                     4: g.of().out,
                     3: g.of().both]).name""", g);
        }

        @Override
        public Traversal<Vertex, Object> get_g_V_chooseXout_count_nextX2L_name_3L_valueMapX() {
            ComputerTestHelper.compute("""g.V.choose({ it.out().count().next(); }, [
                    2L: g.of().name,
                    3L: g.of().valueMap])""", g);
        }
    }
}
