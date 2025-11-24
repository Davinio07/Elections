package nl.hva.elections.persistence.model;

import java.util.Objects;

/**
 * Represents a registered political party in the Dutch election.
 */
public class PoliticalParty {
    private String registeredAppellation;

    public PoliticalParty() {
    }

    public PoliticalParty(String registeredAppellation) {
        this.registeredAppellation = registeredAppellation;
    }

    public String getRegisteredAppellation() {
        return registeredAppellation;
    }

    public void setRegisteredAppellation(String registeredAppellation) {
        this.registeredAppellation = registeredAppellation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PoliticalParty that = (PoliticalParty) o;
        return Objects.equals(registeredAppellation, that.registeredAppellation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registeredAppellation);
    }

    @Override
    public String toString() {
        return "PoliticalParty{" +
                "registeredAppellation='" + registeredAppellation + '\'' +
                '}';
    }
}