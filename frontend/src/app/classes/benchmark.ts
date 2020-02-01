import { BenchmarkGroup } from './benchmark-group';
import { BenchmarkProperty } from './benchmark-property';

export interface Benchmark {
    id: number;
    customName: string;
    description: string;
    properties: BenchmarkProperty[];
    benchmarkGroup: BenchmarkGroup;
}
