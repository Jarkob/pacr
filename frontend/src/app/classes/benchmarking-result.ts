import { BenchmarkGroup } from './benchmark-group';
import { SystemEnvironment } from './system-environment';

export interface BenchmarkingResult {
    globalError: boolean;
    commitHash: string;
    commitMessage: string;
    commitEntryDate: Date;
    commitCommitDate: Date;
    commitAuthorDate: Date;
    commitRepositoryid: string;
    commitBranchName: string;
    commitParentHashes: string[];
    commitLabels: string[];
    systemEnvironment: SystemEnvironment;
    groups: BenchmarkGroup[];
}
