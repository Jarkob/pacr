import { Benchmark } from './benchmark';

export interface BenchmarkGroup {
    id: number;
    name: string;
    benchmarks: Benchmark[];
}
