/**
 * Computational Intelligence Library (CIlib)
 * Copyright (C) 2003 - 2010
 * Computational Intelligence Research Group (CIRG@UP)
 * Department of Computer Science
 * University of Pretoria
 * South Africa
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.cilib.type.parser;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import net.sourceforge.cilib.type.parser.analysis.DepthFirstAdapter;
import net.sourceforge.cilib.type.parser.lexer.Lexer;
import net.sourceforge.cilib.type.parser.lexer.LexerException;
import net.sourceforge.cilib.type.parser.node.ABoundedBoundsStatement;
import net.sourceforge.cilib.type.parser.node.ABoundsBoundedStatement;
import net.sourceforge.cilib.type.parser.node.ADoubleNumber;
import net.sourceforge.cilib.type.parser.node.AIntegerNumber;
import net.sourceforge.cilib.type.parser.node.ANonEmptyExponentStatement;
import net.sourceforge.cilib.type.parser.node.AStatement;
import net.sourceforge.cilib.type.parser.node.Start;
import net.sourceforge.cilib.type.parser.node.TType;
import net.sourceforge.cilib.type.parser.parser.Parser;
import net.sourceforge.cilib.type.parser.parser.ParserException;
import net.sourceforge.cilib.type.types.Bounds;
import net.sourceforge.cilib.type.types.Numeric;
import net.sourceforge.cilib.type.types.Type;
import net.sourceforge.cilib.type.types.container.StructuredType;
import net.sourceforge.cilib.type.types.container.TypeList;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * The domain parser converts a provided domain string representation into
 * a {@code StructuredType}. If the domain string defines a simple {@code Vector}
 * based representation of {@code Numeric} types, then a {@code Vector} is returned.
 */
public final class DomainParser {

    private DomainParser() {
    }

    /**
     * Parse the provided domain string and return the constructed representation.
     * @param <E> The structured type.
     * @param domain The string to parse.
     * @return A {@code TypeList} is returned by default, but if the type is defined
     *         to consist of {@code Numeric} types, a {@code Vector} instance is returned.
     */
    public static <E extends StructuredType<? extends Type>> E parse(String domain) {
        try {
            Evaluator e = new Evaluator();
            Lexer lexer = new Lexer(new PushbackReader(new StringReader(domain), 100));
            Parser parser = new Parser(lexer);
            Start ast = parser.parse();
            ast.apply(e);
            @SuppressWarnings("unchecked")
            E result = (E) e.typeList; // This is a typesafe operation

            if (isVector(result)) {
                @SuppressWarnings("unchecked")
                E vector = (E) toVector(result);
                return vector;
            }

            return result;
        } catch (IOException io) {
            throw new RuntimeException("IOException somehow happened during the parsing of the domain: " + domain, io);
        } catch (LexerException lexer) {
            throw new RuntimeException("A lexer error was caused during parsing of domain string: " + domain, lexer);
        } catch (ParserException ex) {
            throw new RuntimeException("An exception occured during the parsing of: " + domain, ex);
        }
    }

    /**
     * Convert the {@code TypeList} into a {@code Vector} if all elements are
     * {@code Numeric} types.
     * @param representation The current data structure of the representation.
     * @return {@code true} if the structure should really be a {@code Vector},
     *         {@code false} otherwise.
     */
    private static boolean isVector(StructuredType<? extends Type> representation) {
        for (Type type : representation) {
            if (!(type instanceof Numeric)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Convert the provided {@code representation} into a {@code Vector}.
     * @param representation The {@code StructuredType} to convert.
     * @return The converted vector object.
     */
    private static <E extends StructuredType<? extends Type>> Vector toVector(E representation) {
        Vector.Builder vector = Vector.newBuilder();

        for (Type type : representation) {
            vector.add((Numeric) type);
        }

        return vector.build();
    }

    private static class Evaluator extends DepthFirstAdapter {

        private TypeList typeList = new TypeList();
        private BoundsFactory boundsFactory = new BoundsFactory();

        @Override
        public void outAStatement(AStatement node) {
            ExponentVisitor exponentVisitor = new ExponentVisitor();
            BoundVisitor boundVisitor = new BoundVisitor();

            node.getBoundsStatement().apply(boundVisitor);
            node.getExponentStatement().apply(exponentVisitor);

            TypeCreator creator = getCreator(node.getType());
            for (int i = 0; i < exponentVisitor.getExponentValue(); i++) {
                Type instance = null;

                if (boundVisitor.value && Double.compare(boundVisitor.lowerBound, Double.NEGATIVE_INFINITY) == 0) {
                    instance = creator.create();
                } else if (boundVisitor.value && Double.compare(boundVisitor.lowerBound, Double.NEGATIVE_INFINITY) != 0) {
                    instance = creator.create(boundVisitor.lowerBound);
                } else {
                    Bounds bounds = boundsFactory.create(boundVisitor.lowerBound, boundVisitor.upperBound);
                    instance = creator.create(bounds);
                }

                typeList.add(instance);
            }
        }

        private TypeCreator getCreator(TType type) {
            TypeCreator instance = null;

            try {
                // create an instance of the TypeCreator
                Class<?> creatorClass = Class.forName("net.sourceforge.cilib.type.parser." + type.getText());
                instance = (TypeCreator) creatorClass.newInstance();
            } catch (ClassNotFoundException c) {
                throw new UnsupportedOperationException("Cannot find class: net.sourceforge.cilib.type.creator." + type.getText(), c);
            } catch (IllegalAccessException i) {
                throw new UnsupportedOperationException("Cannot access!", i);
            } catch (InstantiationException in) {
                throw new UnsupportedOperationException("Cannot instantiate class: net.sourceforge.cilib.type.creator." + type.getText(), in);
            }

            return instance;
        }
    }

    private static class ExponentVisitor extends DepthFirstAdapter {

        private int exponentValue = 1;

        @Override
        public void outANonEmptyExponentStatement(ANonEmptyExponentStatement node) {
            this.exponentValue = Integer.valueOf(node.getPositiveInteger().getText());
        }

        public int getExponentValue() {
            return exponentValue;
        }
    }

    private static class BoundVisitor extends DepthFirstAdapter {

        private boolean value = true;
        private double lowerBound = Double.NEGATIVE_INFINITY;
        private double upperBound = Double.POSITIVE_INFINITY;

        @Override
        public void outABoundsBoundedStatement(ABoundsBoundedStatement node) {
            value = false;
            node.getNumber().apply(new DepthFirstAdapter() {

                @Override
                public void outADoubleNumber(ADoubleNumber node) {
                    upperBound = Double.valueOf(node.getDecimal().getText());
                }

                @Override
                public void outAIntegerNumber(AIntegerNumber node) {
                    upperBound = Integer.valueOf(node.getPositiveInteger().getText());
                }
            });
        }

        @Override
        public void outABoundedBoundsStatement(ABoundedBoundsStatement node) {
            node.getValue().apply(new DepthFirstAdapter() {

                @Override
                public void outADoubleNumber(ADoubleNumber node) {
                    lowerBound = Double.valueOf(node.getDecimal().getText());
                }

                @Override
                public void outAIntegerNumber(AIntegerNumber node) {
                    lowerBound = Integer.valueOf(node.getPositiveInteger().getText());
                }
            });

            node.getBoundedStatement().apply(this);
        }
    }
}
