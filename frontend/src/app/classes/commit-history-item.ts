export interface CommitHistoryItem {
    commitHash: string;
    commitDate: Date;
    authorDate: Date;
    entryDate: Date;
    commitMessage: string;
    significant: boolean;
    globalError: boolean;
    globalErrorMessage: string;
}
