package examples.grizzly.models;

public final class Organization {
    public final OrgId id;
    public final String name;
    public final FinancialYears financialYears;

    public Organization(OrgId id, String name, FinancialYears financialYears) {
        this.id = id;
        this.name = name;
        this.financialYears = financialYears;
    }

    /**
     * Set the name and return the result as a new user instance
     *
     * @param newName The new organization name
     * @return A new organization instance.
     */
    public Organization setName(String newName) {
        return new Organization(id, newName, financialYears);
    }

    /**
     * Add a financial year for the organization
     *
     * @param financialYear The financial year we want to add
     * @return A new organization instance.
     */
    public Organization addFinancialYear(FinancialYear financialYear) {
        final FinancialYears copy = financialYears.clone();
        copy.add(financialYear);
        return new Organization(id, name, copy);
    }

    @Override
    public String toString() {
        return "Organization{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", financialYears=" + financialYears +
                '}';
    }
}
