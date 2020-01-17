import { BenchmarkProperty } from './benchmark-property';
import { Result } from './result';

export interface Benchmark {
    id: number;
    originalName: string;
    customName: string;
    description: string;
    results: Result[];
}
