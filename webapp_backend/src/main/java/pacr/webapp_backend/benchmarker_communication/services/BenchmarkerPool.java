package pacr.webapp_backend.benchmarker_communication.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Manages a collection of PACR-Benchmarkers and ensures that a Benchmarker is only given out
 * if it is marked as free.
 */
@Component
public class BenchmarkerPool implements IBenchmarkerHandler, IBenchmarkerPool {

    private static final Logger LOGGER = LogManager.getLogger(BenchmarkerPool.class);

    private final Map<String, SystemEnvironment> allBenchmarkers;

    private final Set<String> occupiedBenchmarkers;
    private final Queue<String> freeBenchmarkers;

    private final Collection<INewRegistrationListener> newRegistrationListeners;

    /**
     * Creates a new BenchmarkerPool.
     */
    public BenchmarkerPool() {
        this.allBenchmarkers = new HashMap<>();
        this.occupiedBenchmarkers = new HashSet<>();
        this.freeBenchmarkers = new LinkedList<>();

        this.newRegistrationListeners = new ArrayList<>();
    }

    @Override
    public boolean registerBenchmarker(final String address, final SystemEnvironment sysEnvironment) {
        verifyAddress(address);

        Objects.requireNonNull(sysEnvironment, "The system environment cannot be null.");

        if (containsBenchmarker(address)) {
            return false;
        }

        freeBenchmarkers.add(address);
        allBenchmarkers.put(address, sysEnvironment);

        LOGGER.info("Registered the benchmarker with address '{}' to the system.", address);

        notifyRegistrationListeners();

        return true;
    }

    @Override
    public boolean unregisterBenchmarker(final String address) {
        verifyAddress(address);

        if (!containsBenchmarker(address)) {
            return false;
        }

        allBenchmarkers.remove(address);

        if (freeBenchmarkers.contains(address)) {
            freeBenchmarkers.remove(address);
        } else {
            occupiedBenchmarkers.remove(address);
        }

        LOGGER.info("Unregistered the benchmarker with address '{}' from the system.", address);

        return true;
    }

    @Override
    public SystemEnvironment getBenchmarkerSystemEnvironment(final String address) {
        if (!StringUtils.hasText(address)) {
            throw new IllegalArgumentException("The address cannot be null or empty.");
        }

        return allBenchmarkers.get(address);
    }

    @Override
    public Collection<String> getAllBenchmarkerAddresses() {
        return new ArrayList<>(allBenchmarkers.keySet());
    }

    private boolean containsBenchmarker(final String address) {
        return allBenchmarkers.containsKey(address);
    }

    @Override
    public boolean hasFreeBenchmarkers() {
        return !freeBenchmarkers.isEmpty();
    }

    @Override
    public String getFreeBenchmarker() {
        if (hasFreeBenchmarkers()) {
            // get free benchmarker and add it to the end of the queue again.
            final String address = freeBenchmarkers.poll();
            freeBenchmarkers.add(address);

            return address;
        }

        return null;
    }

    @Override
    public void freeBenchmarker(final String address) {
        verifyAddress(address);

        if (occupiedBenchmarkers.contains(address)) {
            occupiedBenchmarkers.remove(address);

            freeBenchmarkers.add(address);
        }
    }

    @Override
    public void occupyBenchmarker(final String address) {
        verifyAddress(address);

        if (freeBenchmarkers.contains(address)) {
            freeBenchmarkers.remove(address);

            occupiedBenchmarkers.add(address);
        }
    }

    @Override
    public void addListener(INewRegistrationListener registrationListener) {
        if (registrationListener != null && !newRegistrationListeners.contains(registrationListener)) {
            newRegistrationListeners.add(registrationListener);
        }
    }

    private void notifyRegistrationListeners() {
        for (final INewRegistrationListener listener : newRegistrationListeners) {
            listener.newRegistration();
        }
    }

    private void verifyAddress(String address) {
        if (!StringUtils.hasText(address)) {
            throw new IllegalArgumentException("The address is not valid.");
        }
    }
}
