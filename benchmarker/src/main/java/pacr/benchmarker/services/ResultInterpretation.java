package pacr.benchmarker.services;

/**
 * Specifies how a result should be interpreted.
 */
public enum ResultInterpretation {

    /**
     * Lower values are regarded better.
     */
    LESS_IS_BETTER,

    /**
     * Higher values are regarded better.
     */
    MORE_IS_BETTER,

    /**
     * Values are not regarded as better or worse.
     */
    NEUTRAL

}
