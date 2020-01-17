import { Branch } from './branch';

export interface Repository {
    trackAllBranches: boolean;
    selectedBranches: Branch[];
    url: string;
    name: string;
}
