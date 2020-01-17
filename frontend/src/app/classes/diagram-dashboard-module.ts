import { Repository } from './repository';
import { Benchmark } from './benchmark';

export interface DiagramDashboardModule {
    repositories: Repository[];
    benchmarks: Benchmark[];
}
