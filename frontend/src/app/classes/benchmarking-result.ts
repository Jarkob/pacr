import { BenchmarkGroup } from './benchmark-group';
import { SystemEnvironment } from './system-environment';

export interface BenchmarkingResult {
    globalError: string;
    commitHash: string;
    commitMessage: string;
    commitEntryDate: Date;
    commitCommitDate: Date;
    commitAuthorDate: Date;
    commitRepositoryName: string;
    commitUrl: string;
    commitBranchNames: string[];
    commitParentHashes: string[];
    commitLabels: string[];
    systemEnvironment: SystemEnvironment;
    groups: BenchmarkGroup[];
}
