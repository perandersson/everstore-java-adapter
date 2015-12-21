package examples.grizzly.resources.financialyears;

import everstore.java.utils.Optional;
import examples.grizzly.models.FinancialYear;
import examples.grizzly.models.FinancialYearId;
import examples.grizzly.models.FinancialYears;
import examples.grizzly.models.OrgId;
import examples.grizzly.repositories.OrgRepository;
import examples.grizzly.repositories.OrgStatefulRepository;
import examples.grizzly.services.FinancialYearService;
import org.glassfish.jersey.server.ManagedAsync;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

import static examples.grizzly.rest.ResourceUtils.handleGet;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Singleton
@Path("/v1/financialyears")
@Produces(APPLICATION_JSON)
public class FinancialYearRepository {

    private final OrgRepository orgRepository;
    private final FinancialYearService financialYearService;

    @Inject
    public FinancialYearRepository(final OrgRepository orgRepository,
                                   final FinancialYearService financialYearService) {
        this.orgRepository = orgRepository;
        this.financialYearService = financialYearService;
    }

    @GET
    @ManagedAsync
    public void getFinancialYears(@Suspended AsyncResponse response,
                                  @HeaderParam("orgId") String id) {
        response.setTimeout(1000, MILLISECONDS);
        final OrgId orgId = OrgId.fromString(id);

        final Optional<OrgStatefulRepository> statefulRepository = orgRepository.get(orgId);
        handleGet(response, statefulRepository.flatMap(repository -> {
            final Optional<FinancialYears> financialYears = financialYearService.getFinancialYears(repository);
            repository.close();
            return financialYears;
        }));
    }

    @GET
    @Path("/{id: \\d+}")
    @ManagedAsync
    public void getFinancialYear(@Suspended AsyncResponse response,
                                 @HeaderParam("orgId") String id,
                                 @PathParam("id") String finYearId) {
        response.setTimeout(1000, MILLISECONDS);
        final OrgId orgId = OrgId.fromString(id);
        final FinancialYearId financialYearId = FinancialYearId.fromString(finYearId);

        final Optional<OrgStatefulRepository> statefulRepository = orgRepository.get(orgId);
        handleGet(response, statefulRepository.flatMap(repository -> {
            final Optional<FinancialYear> financialYear = financialYearService.findFinancialYear(repository, financialYearId);
            repository.close();
            return financialYear;
        }));
    }
}
