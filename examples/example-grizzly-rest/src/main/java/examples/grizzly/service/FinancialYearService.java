package examples.grizzly.service;

import everstore.api.Transaction;
import examples.grizzly.events.FinancialYearAdded;
import examples.grizzly.models.FinancialYear;
import examples.grizzly.models.FinancialYearId;
import examples.grizzly.models.OrgId;
import examples.grizzly.repositories.OrgRepository;
import examples.grizzly.repositories.OrgStatefulRepository;
import examples.grizzly.resources.financialyears.FinancialYearCandidate;
import rx.Observable;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

import static examples.grizzly.resources.ResourceMapper.*;
import static rx.Observable.from;

public class FinancialYearService {

    private final OrgRepository orgRepository;
    private final OrganizationService organizationService;

    @Inject
    public FinancialYearService(OrgRepository orgRepository, OrganizationService organizationService) {
        this.orgRepository = orgRepository;
        this.organizationService = organizationService;
    }

    /**
     * Retrieves the financial year with the given id
     *
     * @param orgId
     * @param id
     * @return
     */
    public Observable<FinancialYear> getFinancialYear(final OrgId orgId, final FinancialYearId id) {
        return organizationService.get(orgId).map(org -> org.financialYears.findById(id)
                .map(financialYear -> {
                    notFound(financialYear == null, () -> "Financial year: " + id + " was not found");
                    return financialYear;
                }).get());
    }

    /**
     * Add a new financial year for the supplied organization
     *
     * @param orgId
     * @param request
     * @return
     */
    public Observable<FinancialYear> addFinancialYear(final OrgId orgId,
                                                      final FinancialYearCandidate request) {
        final OrgStatefulRepository repository = orgRepository.get(orgId);
        final Observable<Transaction> transaction = repository.openTransaction();
        return transaction
                .flatMap(repository::findOrg)
                .flatMap(org -> {
                    notFound(org == null, () -> "Organization: " + request + " was not found");
                    conflict(org.financialYears.overlapps(request.startDate, request.endDate),
                            () -> "You already have a financial year overlapping the supplied start and end date");

                    final FinancialYearAdded event = new FinancialYearAdded(new FinancialYearId(),
                            request.startDate, request.endDate);
                    return from(repository.saveEvents(transaction, event));
                })
                .map(commitResult -> {
                    commitFailed(commitResult, () -> "Could not add financial year: " + request);
                    FinancialYearAdded event = (FinancialYearAdded) commitResult.events.get(0);
                    return new FinancialYear(event.id, event.startDate, event.endDate);
                });
    }
}
