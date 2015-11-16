package examples.grizzly.repositories;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import everstore.api.Adapter;
import everstore.api.CommitResult;
import examples.grizzly.models.OrgId;
import examples.grizzly.models.Organization;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    public CompletableFuture<Organization> findUser(final OrgId orgId) {
        return get(orgId).findUser();
    }
}
