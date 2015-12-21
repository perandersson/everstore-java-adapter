package examples.grizzly.repositories;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import everstore.api.Adapter;
import everstore.java.repositories.StatefulRepository;
import everstore.java.utils.Optional;
import examples.grizzly.models.OrgId;
import examples.grizzly.models.Organization;

public class OrgRepository {
    private final Adapter adapter;
    private final Cache<OrgId, StatefulRepository<Organization>> repositories;

    public OrgRepository(Adapter adapter, int maxStates) {
        this.adapter = adapter;
        this.repositories = Caffeine.newBuilder()
                .maximumSize(maxStates).<OrgId, StatefulRepository<Organization>>build();
    }

    public Optional<OrgStatefulRepository> get(final OrgId orgId) {
        final String journalName = "/java/grizzly/org/" + orgId.value;

        final StatefulRepository<Organization> repository =
                repositories.get(orgId, id -> new StatefulRepository<>(adapter, journalName));
        return repository.openTransaction().map(t -> new OrgStatefulRepository(t, repository, orgId));
    }
}
