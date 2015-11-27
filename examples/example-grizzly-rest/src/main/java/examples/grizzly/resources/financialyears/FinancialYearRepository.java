package examples.grizzly.resources.financialyears;

import examples.grizzly.models.FinancialYear;
import examples.grizzly.models.FinancialYearId;
import examples.grizzly.models.FinancialYears;
import examples.grizzly.models.OrgId;
import examples.grizzly.resources.EventStoreResource;
import examples.grizzly.service.FinancialYearService;
import examples.grizzly.service.OrganizationService;
import org.glassfish.jersey.server.ManagedAsync;
import rx.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Singleton
@Path("/v1/financialyear")
@Produces(APPLICATION_JSON)
public class FinancialYearRepository implements EventStoreResource {

    private final OrganizationService organizationService;
    private final FinancialYearService financialYearService;

    @Inject
    public FinancialYearRepository(OrganizationService organizationService, FinancialYearService financialYearService) {
        this.organizationService = organizationService;
        this.financialYearService = financialYearService;
    }

    @GET
    @ManagedAsync
    public void getFinancialYears(@Suspended AsyncResponse response,
                                  @HeaderParam("orgId") final String id) {
        response.setTimeout(1000, MILLISECONDS);

        final OrgId orgId = OrgId.fromString(id);
        final Observable<FinancialYears> result = organizationService.get(orgId)
                .map(org -> org.financialYears);

        handleResponse(response, result);
    }

    @GET
    @Path("/{id}")
    @ManagedAsync
    public void getFinancialYear(@Suspended final AsyncResponse response,
                                 @HeaderParam("orgId") final String id,
                                 @PathParam("id") final String financialYearId) {
        response.setTimeout(1000, MILLISECONDS);

        final OrgId orgId = OrgId.fromString(id);
        final Observable<FinancialYear> result =
                financialYearService.getFinancialYear(orgId, FinancialYearId.fromString(financialYearId));

        handleResponse(response, result);
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @ManagedAsync
    public void addFinancialYears(@Suspended final AsyncResponse response,
                                  @HeaderParam("orgId") final String id,
                                  final FinancialYearCandidate candidate) {
        response.setTimeout(1000, MILLISECONDS);

        final OrgId orgId = OrgId.fromString(id);
        final Observable<FinancialYear> result =
                financialYearService.addFinancialYear(orgId, candidate);

        handleResponse(response, result);
    }
}
