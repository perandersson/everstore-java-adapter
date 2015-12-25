package examples.grizzly;

import everstore.api.Adapter;
import examples.grizzly.repositories.OrgRepository;
import examples.grizzly.repositories.factory.RepositoryModuleBinder;
import examples.grizzly.services.FinancialYearService;
import examples.grizzly.services.OrgService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class AppModule extends AbstractBinder {
    private final Adapter adapter;

    public AppModule(Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    protected void configure() {
        bind(new OrgRepository(adapter, 1000)).to(OrgRepository.class);
        final OrgService orgService = new OrgService();
        bind(orgService).to(OrgService.class);
        bind(new FinancialYearService(orgService)).to(FinancialYearService.class);
        bind(adapter).to(Adapter.class);
        new RepositoryModuleBinder().bind(this);
    }
}
