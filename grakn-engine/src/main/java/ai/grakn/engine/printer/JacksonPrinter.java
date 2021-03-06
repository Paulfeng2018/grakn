/*
 * Grakn - A Distributed Semantic Database
 * Copyright (C) 2016-2018 Grakn Labs Limited
 *
 * Grakn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Grakn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Grakn. If not, see <http://www.gnu.org/licenses/agpl.txt>.
 */

package ai.grakn.engine.printer;

import ai.grakn.concept.Concept;
import ai.grakn.engine.controller.response.Answer;
import ai.grakn.engine.controller.response.ConceptBuilder;
import ai.grakn.graql.ComputeQuery;
import ai.grakn.graql.internal.printer.Printer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is used to convert the responses from graql queries into objects which can be Jacksonised into their
 * correct Json representation.
 *
 * @author Filipe Peliz Pinto Teixeira
 */
public class JacksonPrinter extends Printer<Object> {
    private static ObjectMapper mapper = new ObjectMapper();

    public static JacksonPrinter create(){
        return new JacksonPrinter();
    }

    @Override
    public String complete(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error during serialising {%s}", object), e);
        }
    }

    @Override
    public Object concept(Concept concept) {
        return ConceptBuilder.build(concept);
    }

    @Override
    public Object queryAnswer(ai.grakn.graql.admin.Answer answer) {
        return Answer.create(answer);
    }

    //TODO: implement JacksonPrinter for ComputeAnswer properly!
    @Override
    public Object computeAnswer(ComputeQuery.Answer computeAnswer) {
        return object(computeAnswer);
    }

    @Override
    public Object bool(boolean bool) {
        return bool;
    }

    @Override
    public Object object(Object object) {
        return object;
    }

    @Override
    public Object map(Map map) {
        Stream<Map.Entry> entries = map.<Map.Entry>entrySet().stream();
        return entries.collect(Collectors.toMap(
                entry -> build(entry.getKey()),
                entry -> build(entry.getValue())
        ));
    }

    @Override
    public Object collection(Collection collection) {
        return collection.stream().map(object -> build(object)).collect(Collectors.toList());
    }

    @Override
    public Object optional(Optional optional) {
        if(optional.isPresent()){
            return build(optional.get());
        } else {
            return null;
        }
    }
}
