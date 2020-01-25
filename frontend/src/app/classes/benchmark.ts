import { Result } from './result';
import { BenchmarkProperty } from './benchmark-property';
import { BenchmarkGroup } from './benchmark-group';

export interface Benchmark {
    id: number;
    originalName: string;
    customName: string;
    description: string;
    properties: BenchmarkProperty;
    group: BenchmarkGroup;
}
