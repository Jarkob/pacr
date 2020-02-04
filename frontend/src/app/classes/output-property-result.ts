export interface OutputPropertyResult {
    name: string;
    unit: string;
    interpretation: string;
    mean: number;
    lowerQuartile: number;
    upperQuartile: number;
    median: number;
    standardDeviation: number;
    hadLocalError: boolean;
    errorMessage: string;
    ratioToPreviousCommit: number;
    compared: boolean;
}
