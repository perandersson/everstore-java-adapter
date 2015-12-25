package examples.grizzly.repositories.factory;

import everstore.java.utils.Optional;
import examples.grizzly.models.OrgId;
import examples.grizzly.repositories.OrgRepository;
import examples.grizzly.repositories.OrgStatefulRepository;
import org.glassfish.jersey.server.internal.inject.AbstractContainerRequestValueFactory;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;

public final class RepositoryFactory extends AbstractContainerRequestValueFactory<Optional<OrgStatefulRepository>> {

    private final OrgRepository repository;

    @Inject
    public RepositoryFactory(OrgRepository repository) {
        this.repository = repository;
    }

    public Optional<OrgStatefulRepository> provide() {
        MultivaluedMap<String, String> headers = getContainerRequest().getHeaders();
        final String orgIdAsString = headers.getFirst("orgId");
        final OrgId orgId = OrgId.fromString(orgIdAsString);
        return repository.get(orgId);
    }
}
