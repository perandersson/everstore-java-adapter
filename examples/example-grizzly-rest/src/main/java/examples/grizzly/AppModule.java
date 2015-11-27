package examples.grizzly;

import everstore.api.Adapter;
import examples.grizzly.repositories.OrgRepository;
import examples.grizzly.service.FinancialYearService;
import examples.grizzly.service.OrganizationService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class AppModule extends AbstractBinder {
    private final Adapter adapter;

    public AppModule(Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    protected void configure() {
        bind(new OrgRepository(adapter, 1000)).to(OrgRepository.class);
        bind(OrganizationService.class).to(OrganizationService.class);
        bind(FinancialYearService.class).to(FinancialYearService.class);
        bind(adapter).to(Adapter.class);
    }
}
