import { OutputBenchmark } from './output-benchmark';
import { SystemEnvironment } from './system-environment';
export interface CommitBenchmarkingResult {
    id: number;
    globalError: boolean;
    errorMessage: string;
    commitHash: string;
    commitMessage: string;
    commitEntryDate: Date;
    commitCommitDate: Date;
    commitAuthorDate: Date;
    commitRepositoryId: number;
    commitRepositoryName: string;
    commitURL: string;
    comparisonCommitHash: string;
    commitBranchNames: string[];
    commitParentHashes: string[];
    commitLabels: string[];
    systemEnvironment: SystemEnvironment;
    benchmarksList: OutputBenchmark[];
}
