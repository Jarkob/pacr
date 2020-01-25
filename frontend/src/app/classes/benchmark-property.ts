import { ResultInterpretation } from './result-interpretation';

export interface BenchmarkProperty {
    id: number;
    name: string;
    unit: string;
    interpretation: ResultInterpretation;
}
