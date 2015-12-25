package examples.grizzly.repositories.factory;

import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;

final class RepositoryFactoryInjectionResolver extends ParamInjectionResolver {
    public RepositoryFactoryInjectionResolver() {
        super(RepositoryFactoryProvider.class);
    }
}
