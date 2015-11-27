package examples.grizzly.repositories;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import everstore.api.Adapter;
import examples.grizzly.models.OrgId;

public class OrgRepository {
    private final Adapter adapter;
    private final Cache<OrgId, OrgStatefulRepository> repositories;

    public OrgRepository(Adapter adapter, int maxStates) {
        this.adapter = adapter;
        this.repositories = Caffeine.newBuilder()
                .maximumSize(maxStates).<OrgId, OrgStatefulRepository>build();
    }

    public OrgStatefulRepository get(final OrgId orgId) {
        return repositories.get(orgId, id -> new OrgStatefulRepository(adapter, id));
    }
}
