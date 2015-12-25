package examples.grizzly.repositories.factory;

import examples.grizzly.AppModule;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;

import javax.inject.Singleton;

public class RepositoryModuleBinder {
    public void bind(AppModule module) {
        module.bind(RepositoryFactory.class).to(RepositoryFactory.class).in(Singleton.class);
        module.bind(RepositoryFactoryProvider.class).to(ValueFactoryProvider.class).in(Singleton.class);
        module.bind(RepositoryFactoryInjectionResolver.class)
                .to(new TypeLiteral<InjectionResolver<RepositoryParam>>() {
                }).in(Singleton.class);
    }
}
