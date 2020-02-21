export interface CommitHistoryItem {
    commitHash: string;
    commitDate: Date;
    compared: boolean;
    authorDate: Date;
    entryDate: Date;
    commitMessage: string;
    significant: boolean;
    globalError: boolean;
    globalErrorMessage: string;
}
