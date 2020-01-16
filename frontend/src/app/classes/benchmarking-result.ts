import { BenchmarkGroup } from './benchmark-group';
import { Commit } from './commit';
import { SystemEnvironment } from './system-environment';

export interface BenchmarkingResult {
    hadGlobalError: boolean;
    errorMessage: string;
    commit: Commit;
    systemEnvironment: SystemEnvironment;
    groups: BenchmarkGroup[];
}
