package examples.grizzly;

import everstore.api.Adapter;
import examples.grizzly.repositories.OrgRepository;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class AppModule extends AbstractBinder {
    private final Adapter adapter;

    public AppModule(Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    protected void configure() {
        bind(new OrgRepository(adapter, 1000)).to(OrgRepository.class);
        bind(adapter).to(Adapter.class);
    }
}
