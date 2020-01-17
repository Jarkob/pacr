export interface Commit {
    sha: string;
    commitDate: Date;
    authorDate: Date;
    commitMessage: string;
    entryDate: Date;
    gitTags: string[];
}
