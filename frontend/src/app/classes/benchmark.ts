import { BenchmarkProperty } from './benchmark-property';

export interface Benchmark {
    id: number;
    benchmarkName: string;
    customName: string;
    description: string;
    propertyList: BenchmarkProperty[];
}
