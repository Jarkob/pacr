import { Commit } from './commit';

export interface Repository {
    id: number;
    trackAllBranches: boolean;
    trackedBranches: string[];
    pullURL: string;
    name: string;
    hookSet: boolean;
    color: string;
    observeFromDate: Date;
    commitLinkPrefix: string;
    commits: Commit[];
}
