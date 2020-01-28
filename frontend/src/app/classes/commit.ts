import { Repository } from './repository';

export interface Commit {
    commitHash: string;
    commitDate: Date;
    authorDate: Date;
    entryDate: Date;
    commitMessage: string;
    labels: string[];
    repository: Repository;
    branches: string[];
    parentHashes: string[];
}
