package pacr.webapp_backend.benchmarker_communication.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Manages a collection of PACR-Benchmarkers and ensures that a Benchmarker is only given out
 * if it is marked as free.
 */
@Component
public class BenchmarkerPool implements IBenchmarkerHandler, IBenchmarkerPool {

    private Map<String, SystemEnvironment> freeBenchmarkers;
    private Map<String, SystemEnvironment> occupiedBenchmarkers;

    private IJobReceiver jobReceiver;

    /**
     * Creates a new BenchmarkerPool.
     * @param jobReceiver the jobReceiver that gets called when a new Benchmarker is registered.
     */
    public BenchmarkerPool(IJobReceiver jobReceiver) {
        this.freeBenchmarkers = new HashMap<>();
        this.occupiedBenchmarkers = new HashMap<>();

        this.jobReceiver = jobReceiver;
    }

    @Override
    public boolean registerBenchmarker(String address, SystemEnvironment sysEnvironment) {
        verifyAddress(address);

        if (sysEnvironment == null) {
            throw new IllegalArgumentException("The system environment cannot be null.");
        }

        if (containsBenchmarker(address)) {
            return false;
        }

        freeBenchmarkers.put(address, sysEnvironment);

        jobReceiver.executeJob();

        return true;
    }

    @Override
    public boolean unregisterBenchmarker(String address) {
        verifyAddress(address);

        if (!containsBenchmarker(address)) {
            return false;
        }

        if (freeBenchmarkers.containsKey(address)) {
            freeBenchmarkers.remove(address);
        } else {
            occupiedBenchmarkers.remove(address);
        }

        return true;
    }

    private boolean containsBenchmarker(String address) {
        return freeBenchmarkers.containsKey(address) || occupiedBenchmarkers.containsKey(address);
    }

    @Override
    public Collection<SystemEnvironment> getBenchmarkerSystemEnvironment() {
        Collection<SystemEnvironment> systemEnvironments = new ArrayList<>();

        systemEnvironments.addAll(freeBenchmarkers.values());
        systemEnvironments.addAll(occupiedBenchmarkers.values());

        return systemEnvironments;
    }

    @Override
    public boolean hasFreeBenchmarkers() {
        return !freeBenchmarkers.isEmpty();
    }

    @Override
    public String getFreeBenchmarker() {
        if (hasFreeBenchmarkers()) {
            return freeBenchmarkers.keySet().stream().findFirst().get();
        }

        return null;
    }

    @Override
    public void freeBenchmarker(String address) {
        verifyAddress(address);

        if (occupiedBenchmarkers.containsKey(address)) {
            SystemEnvironment systemEnvironment = occupiedBenchmarkers.remove(address);

            freeBenchmarkers.put(address, systemEnvironment);
        }
    }

    @Override
    public void occupyBenchmarker(String address) {
        verifyAddress(address);

        if (freeBenchmarkers.containsKey(address)) {
            SystemEnvironment systemEnvironment = freeBenchmarkers.remove(address);

            occupiedBenchmarkers.put(address, systemEnvironment);
        }
    }

    private void verifyAddress(String address) {
        if (address == null || address.isEmpty() || address.isBlank()) {
            throw new IllegalArgumentException("The address is not valid.");
        }
    }
}
