package com.ing.bdd.graphql;

import com.ing.bdd.tailrecursion.TailCall;
import graphql.GraphQL;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.ing.bdd.tailrecursion.TailCalls.done;
import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Configuration
public class GraphQLProvider {
    @Bean
    public GraphQL graphQL(GraphQLSchema schema) {
        return GraphQL
            .newGraphQL(schema)
            .build();
    }

    @Bean
    public GraphQLSchema buildSchema(RuntimeWiring runtimeWiring) throws IOException {
        ClassPathResource resource = new ClassPathResource("schema.graphqls");
        String sdl = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    @Bean
    public RuntimeWiring buildDataFetcherWiring(List<DataFetcherWrapper<?>> dataFetcherWrappers) {
        RuntimeWiring.Builder builder = buildDataFetcherWiringRecursively(
            RuntimeWiring.newRuntimeWiring(),
            io.vavr.collection.List.ofAll(dataFetcherWrappers)
        ).invoke();

        builder.scalar(ExtendedScalars.DateTime);

        return builder.build();
    }

    private TailCall<RuntimeWiring.Builder> buildDataFetcherWiringRecursively(RuntimeWiring.Builder runtimeWiringBuilder,
                                                                      io.vavr.collection.List<DataFetcherWrapper<?>> vavrList) {
        if (vavrList.isEmpty()) {
            return done(runtimeWiringBuilder);
        } else {
            DataFetcherWrapper<?> wrapper = vavrList.head();
            return () -> buildDataFetcherWiringRecursively(
                runtimeWiringBuilder
                    .type(newTypeWiring(wrapper.getParentType())
                        .dataFetcher(wrapper.getFieldName(), wrapper.getDataFetcher())),
                vavrList.tail()
            );
        }
    }
}
