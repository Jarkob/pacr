export interface Result {
    name: string;
    unit: string;
    interpretation: string;
    mean: number;
    lowerQuartile: number;
    median: number;
    upperQuartile: number;
    standardDeviation: number;
    hadLocalError: boolean;
}
