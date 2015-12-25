package examples.grizzly.repositories.factory;

import everstore.java.utils.Optional;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.internal.inject.AbstractContainerRequestValueFactory;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.model.Parameter;

import javax.inject.Inject;

class RepositoryFactoryProvider extends AbstractValueFactoryProvider {

    private final RepositoryFactory factory;

    @Inject
    public RepositoryFactoryProvider(MultivaluedParameterExtractorProvider mpep,
                                     ServiceLocator injector,
                                     RepositoryFactory factory) {
        super(mpep, injector, Parameter.Source.UNKNOWN);
        this.factory = factory;
    }

    @Override
    public AbstractContainerRequestValueFactory<?> createValueFactory(Parameter parameter) {
        final Class<?> paramType = parameter.getRawType();
        final RepositoryParam annotation = parameter.getAnnotation(RepositoryParam.class);
        if (annotation != null && paramType.isAssignableFrom(Optional.class)) {
            return factory;
        }
        return null;
    }
}
