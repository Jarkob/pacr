import { Commit } from './commit';
import { Branch } from './branch';

export interface Repository {
    id: number;
    trackAllBranches: boolean;
    selectedBranches: Branch[];
    pullURL: string;
    name: string;
    isHookSet: boolean;
    color: any;
    observeFromDate: Date;
    commitLinkPrefix: string;
    commits: Commit[];
}
